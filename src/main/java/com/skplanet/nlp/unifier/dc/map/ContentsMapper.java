package com.skplanet.nlp.unifier.dc.map;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.data.meta.MovieMetaKMDB;
import com.skplanet.nlp.unifier.dc.data.meta.UnifiedMeta;
import com.skplanet.nlp.unifier.dc.util.EditDistance;
import com.skplanet.nlp.unifier.dc.util.HDFSUtil;
import com.skplanet.nlp.unifier.dc.util.LCSequence;
import com.skplanet.nlp.unifier.dc.util.Utilities;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Digital Contents Meta Mapper
 * it maps each digital contents source to unified meta data
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/8/15
 */
public class ContentsMapper {
	private static final Logger LOGGER = Logger.getLogger(ContentsMapper.class.getName());

	private static final double TITLE_WEIGHT = 0.6;
	private static final double DATE_WEIGHT = 0.0;
	private static final double DIRECTOR_WEIGHT = 0.2;
	private static final double ACTOR_WEIGHT = 0.2;

	// mapping threshold
	private final double mappingThreshold;

	// service name
	private String service;
	// category name
	private String category;
	// data source name
	private String source;
	// version
	private String version;

	// unified meta collection
	private Collection<Meta> unifiedMetaCollection;
	private Map<String, UnifiedMeta> unifiedMetaMap;
	private int currentUnifiedIdInteger = -1;
	private Map<String, String> prevMappedSourceIdMap;

	// user dict
	private MultiMap userMappingMap = null;

	// crawl exclusion set
	private Set<String> crawlExcludedSet = null;

	// meta split map
	private MultiMap splitMap = null;

	// exception pid map
	private MultiMap exceptionPidMap = null;

	// data source meta collection
	private Collection<Meta> sourceMetaCollection;
	private Map<String, Meta> sourceMetaMap;

	// unified meta loader
	private MetaLoader unifiedMetaLoader;
	// data source meta loader
	private MetaLoader sourceMetaLoader;

	// utilities
	private Utilities utilities = null;

	/**
	 * Constructor
	 *
	 * @param service  service name
	 * @param category category name
	 * @param source   data source name
	 * @param version  version
	 */
	public ContentsMapper(String service, String category, String source, String version) {
		// typesafe config
		Config unifyConfig = ConfigFactory.load("unification.conf");
		// omp-config config
		Configuration cpFiles = Configuration.getInstance();

		this.mappingThreshold = unifyConfig.getDouble("mapping-threshold");

		// init.
		this.service = service;
		this.category = category;
		this.source = source;
		this.version = version;
		this.utilities = new Utilities(this.service, this.category);

		// data source meta loading
		this.sourceMetaMap = new HashMap<String, Meta>();
		LOGGER.info("source meta loading: " + this.service + ":" + this.category + ":" + this.source);
		this.sourceMetaLoader = new MetaLoader(
				this.service,
				this.category,
				this.source,
				this.version
		);
		this.sourceMetaCollection = this.sourceMetaLoader.loadSourceMeta();
		for (Meta meta : this.sourceMetaCollection) {
			this.sourceMetaMap.put(meta.getId(), meta);
		}

		// ==================================== //
		// unified meta loading
		// ==================================== //
		this.unifiedMetaMap = new HashMap<String, UnifiedMeta>();
		this.prevMappedSourceIdMap = new HashMap<String, String>();
		this.unifiedMetaLoader = new MetaLoader(
				this.service,
				this.category,
				this.source,
				this.version
		);
		this.unifiedMetaCollection = this.unifiedMetaLoader.loadUnifiedMeta();
		if (this.unifiedMetaCollection == null) {
			this.unifiedMetaCollection = new ArrayList<Meta>();
		}

		// ============================================================ //
		// load previously mapped source meta and CID 2 Unified Mata Map
		// ============================================================ //
		LOGGER.info("previously loaded source meta loading start");
		List<Integer> uIdList = new ArrayList<Integer>();
		for (Meta umeta : this.unifiedMetaCollection) {
			this.unifiedMetaMap.put(umeta.getId(), (UnifiedMeta) umeta);
			uIdList.add(Integer.parseInt(umeta.getId().substring(1)));
			for (String pSourceId : ((UnifiedMeta) umeta).getSourceIdList()) {
				this.prevMappedSourceIdMap.put(pSourceId, umeta.getId());
			}
		}
		// get current max uid
		if (uIdList.size() == 0) {
			this.currentUnifiedIdInteger = 0;
		} else {
			this.currentUnifiedIdInteger = Collections.max(uIdList);
		}
		LOGGER.debug("current max uid: " + this.currentUnifiedIdInteger);
		LOGGER.info("previously loaded source meta loading done (" + this.prevMappedSourceIdMap.size() + ")");


		// ========================================== //
		// user mapping dictionary loading
		// ========================================== //
		URL userDictUrl = cpFiles.getResource(this.service + "-" + this.category + "-" + this.source + ".user");
		if (userDictUrl != null) {
			LOGGER.info("user mapping dictionary exist: " + this.service + ":" + this.category + ":" + this.source);
			this.userMappingMap = new MultiValueMap();
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(new File(userDictUrl.getFile())));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}
					String[] fields = line.trim().split("\\t");
					if (fields.length != 2) {
						LOGGER.warn("wrong field length: " + line);
						continue;
					}

					// field[0] : cid
					// field[1] : pid
					userMappingMap.put(Prop.getDataSourceCodeFromDataSourceName(this.source) + "_" + fields[1], fields[0]);

					// insert into prev. mapped map
					String modPid = Prop.getDataSourceCodeFromDataSourceName(this.source) + "_" + fields[1];
					if (this.prevMappedSourceIdMap.containsKey(modPid)) {
						if (!this.prevMappedSourceIdMap.get(modPid).equals(fields[0])) {
							LOGGER.info("[DEBUG] remove pid : " + modPid + " (mapped " + this.prevMappedSourceIdMap.get(modPid) + ")");
							this.prevMappedSourceIdMap.remove(modPid);
						}
					}
					this.prevMappedSourceIdMap.put(modPid, fields[0]);
					LOGGER.info("prev Mapping : " + modPid + " ==> " + fields[0]);

				}
				reader.close();
			} catch (FileNotFoundException e) {
				LOGGER.error("can't find the user dict file: " + userDictUrl.getFile(), e);
			} catch (IOException e) {
				LOGGER.error("can't read the user dict file: " + userDictUrl.getFile(), e);
			}

			MultiMap removeMapping = new MultiValueMap();
			// remove source id from unified id based on user mapping dictionary
			for (String cidKey : this.unifiedMetaMap.keySet()) {
				for (String pidKey : this.unifiedMetaMap.get(cidKey).getSourceIdList()) {
					if (this.userMappingMap.containsKey(pidKey) && !this.userMappingMap.get(pidKey).equals(cidKey)) {
						LOGGER.info("wrong mapping found: " + cidKey + "==>" + pidKey);
						LOGGER.info("remove " + pidKey + " from " + cidKey);
						removeMapping.put(cidKey, pidKey);
					}
				}
			}
			for (Object removeCID : removeMapping.keySet()) {
				for (String removePID : (List<String>) removeMapping.get(removeCID)) {
					LOGGER.info("[DEBUG] remove pid: " + removePID + " from uid: " + removeCID);
					this.unifiedMetaMap.get(removeCID).getSourceIdList().remove(removePID);
				}
			}
		}



		// ==================================== //
		// split source ids
		// ==================================== //
		// skb-movie-skb.split
		LOGGER.info("get split dict. resource: " + this.service + ":" + this.category + ":" + this.source + ".split");
		URL splitURL = cpFiles.getResource(this.service + "-" + this.category + "-" + this.source + ".split");
		this.splitMap = new MultiValueMap();
		// load pid to cid map first
		if (splitURL != null) {
			Map<String, String> tempMap = new HashMap<String, String>();
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(new File(splitURL.getFile())));
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}
					String[] fields = line.trim().split("\\^");
					for (int i = 0; i < fields.length; i++) {
						for (int j = 0; j < fields.length; j++) {
							if (i != j) {
								String serviceCode = Prop.getServiceCodeFromServiceName(this.source);
								this.splitMap.put(serviceCode + "_" + fields[i], serviceCode + "_" + fields[j]);
							}
						}
					}
				}
				reader.close();
			} catch (IOException e) {
				LOGGER.warn("failed to read split dictionary: " + splitURL.getFile());
			}

		}


		// ==================================== //
		// exclude previously tried to be mapped but failed!
		// ==================================== //
		if (!this.source.equals(Prop.DATA_SOURCE_NAME_SKB) &&
				!this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN) &&
				!this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)
				) {
			crawlExcludedSet = new HashSet<String>();
			File crawlExcludedSetFile = new File(cpFiles.getResource(this.service + "-" + this.category + "-" + this.source + ".exclude").getFile());
			BufferedReader crawlExcludeSetFileReader;
			try {
				crawlExcludeSetFileReader = new BufferedReader(new FileReader(crawlExcludedSetFile));
				String line;
				while ((line = crawlExcludeSetFileReader.readLine()) != null) {
					if (line.trim().length() == 0) {
						continue;
					}
					crawlExcludedSet.add(line.trim());
				}
				crawlExcludeSetFileReader.close();
				LOGGER.info("crawl exclude set loaded for " + this.category + ":" + this.source + " (" + crawlExcludedSet.size() + ")");
			} catch (FileNotFoundException e) {
				LOGGER.warn("no crawl exclude set file found: " + this.category + "-" + this.source + ".exclude", e);
			} catch (IOException e) {
				LOGGER.warn("failed to read crawl exclude set file: " + this.category + "-" + this.source + ".exclude.err", e);
			}
		}

		// ================================== //
		// mapping error list
		// ================================== //
		exceptionPidMap = new MultiValueMap();
		File exceptMapFile = new File(cpFiles.getResource(this.category + "-" + this.source + ".map.err").getFile());
		BufferedReader exceptMapFileReader;
		try {
			exceptMapFileReader = new BufferedReader(new FileReader(exceptMapFile));
			String line;
			while ((line = exceptMapFileReader.readLine()) != null) {
				if (line.trim().length() == 0 || line.startsWith("#")) {
					continue;
				}
				String[] fields = line.trim().split("\\t");
				if (fields.length != 2) {
					LOGGER.warn("exception list must have 2 separate field cid & pid: " + line);
					continue;
				}
				exceptionPidMap.put(fields[0], fields[1]);
			}
			exceptMapFileReader.close();
			LOGGER.info("exception list loaded for " + this.category + ":" + this.source + " (" + exceptionPidMap.size() + ")");
		} catch (FileNotFoundException e) {
			LOGGER.warn("no mapping exception list file found: " + this.category + "-" + this.source + ".map.err", e);
		} catch (IOException e) {
			LOGGER.warn("failed to read mapping exception list file: " + this.category + "-" + this.source + ".map.err", e);
		}

		// remove prevMapped incorrectly mapped id from exceptionPidMap
		LOGGER.info("size of prevMappedSourceIDMap before error filter: " + prevMappedSourceIdMap.size());
		String sourceTag = Prop.getDataSourceCodeFromDataSourceName(this.source);
		for (Object cid : exceptionPidMap.keySet()) {
			for (String errPid : (List<String>) exceptionPidMap.get(cid)) {
				LOGGER.debug("Incorrect Mapping: " + cid + " ==> " + sourceTag + "_" + errPid);
				if (prevMappedSourceIdMap.containsKey(sourceTag + "_" + errPid)) {
					LOGGER.info("[DEBUG] remove prev. mapped source id using exclude dict: " + sourceTag + "_" + errPid);
					prevMappedSourceIdMap.remove(sourceTag + "_" + errPid);
					if (!unifiedMetaMap.containsKey(cid)) {
						LOGGER.info("Can't find CID: " + cid + ", SID: " + sourceTag + "_" + errPid);
						continue;
					}
					List<String> srcIdList = unifiedMetaMap.get(cid).getSourceIdList();
					if (srcIdList.contains(sourceTag + "_" + errPid)) {
						unifiedMetaMap.get(cid).getSourceIdList().remove(sourceTag + "_" + errPid);
					} else {
						LOGGER.info("[DEBUG] the source id doesn't exist: " + sourceTag + "_" + errPid);
					}
				}
			}
		}
		LOGGER.info("size of prevMappedSourceIDMap after error filter: " + prevMappedSourceIdMap.size());

		// cleansing unified meta, i.e. remove entry which doesn't have any source id
		LOGGER.info("before cleansing unified meta: " + unifiedMetaMap.size());
		Set<String> removeCIDSet = new HashSet<String>();
		for (String cidKey : this.unifiedMetaMap.keySet()) {
			if (this.unifiedMetaMap.get(cidKey).getSourceIdList().size() == 0) {
				removeCIDSet.add(cidKey);
			}
		}
		for (String cidKey : removeCIDSet) {
			this.unifiedMetaMap.remove(cidKey);
		}
		LOGGER.info("after cleansing unified meta: " + unifiedMetaMap.size());
	}

	/**
	 * Mapping source meta collection to unified meta collection
	 */
	public Collection<Meta> map() {
		long begin, end;
		begin = System.currentTimeMillis();
		int count = 0;


		// ------------------------
		// mapping source id to cid
		for (Meta meta : this.sourceMetaCollection) {
			if (count % 100 == 0) {
				LOGGER.info("digital contents mapping (" + this.service + ":" + this.category + ":" + this.source + ": " + count + "/" + this.sourceMetaCollection.size() + ")");
			}
			count++;

			// -------------------------------------------------------
			// check if crawled exclusion meta
			// -------------------------------------------------------
			if (!meta.getService().equals(Prop.DATA_SOURCE_NAME_SKB) &&
					!meta.getService().equals(Prop.DATA_SOURCE_NAME_HOPPIN) &&
					!meta.getService().equals(Prop.DATA_SOURCE_NAME_TSTORE)
					) {
				if (this.crawlExcludedSet.contains(meta.getId())) {
					LOGGER.info("This is Crawl Exclusion: " + Prop.getDataSourceCodeFromDataSourceName(meta.getService()) + "_" +
									meta.getId()
					);
					continue;
				}
			}

			// -------------------------------------------------------
			// check if the given source id is already mapped
			String sourceMetaId = Prop.getDataSourceCodeFromDataSourceName(meta.getService()) + "_" + meta.getId();
			// TODO: 아래 원인을 근본적으로 분석해보아야 한다
			// 무슨 이유인지 source id mapping  은 있는데 통합 메타가 사라져서 Null Pointer Exception을 유발하고 있다 (아마도, 사전에서 강제로 삭제한 ID 때문에 그런 것으로 추정한다)
			String targetUID = this.prevMappedSourceIdMap.get(sourceMetaId);
			if (targetUID == null) {
				LOGGER.info("[DEBUG] source id : " + sourceMetaId);
				LOGGER.info("[DEBUG] 위에 source id에 해당하는 Unified ID 가 존재하지 않는다.");
			}
			UnifiedMeta targetUMeta = this.unifiedMetaMap.get(targetUID);
			if (targetUMeta == null) {
				LOGGER.info("[DEBUG] uid : " + targetUID);
				LOGGER.info("[DEBUG] 위에 uid에 해당하는 UMETA가 존재하지 않는다.");
			}
			if (targetUMeta != null && this.prevMappedSourceIdMap.containsKey(sourceMetaId)) {
				LOGGER.info("already mapped source meta id: " + sourceMetaId + " ==> " +
						this.prevMappedSourceIdMap.get(sourceMetaId));

				// 만약 해당 Unified Meta에 해당 source id 가 없다면 추가하라
				if (!this.unifiedMetaMap.get(this.prevMappedSourceIdMap.get(sourceMetaId)).getSourceIdList().contains(sourceMetaId)) {
					this.unifiedMetaMap.get(this.prevMappedSourceIdMap.get(sourceMetaId)).addSourceId(sourceMetaId);
				}

				// -------------------------------------------------------
				// update keywords & score & score count & purchase count

				// 1. add keywords if available
				if (meta.getClass() == MovieMetaKMDB.class) {
					if (((MovieMetaKMDB) meta).getKeywords().size() > 0) {
						for (String keyword : ((MovieMetaKMDB) meta).getKeywords()) {
							this.unifiedMetaMap.get(this.prevMappedSourceIdMap.get(sourceMetaId)).addKeywords(keyword);
						}
					}
				}

				// 2. update score if needed,
				if (meta.getScore().size() > 0) {
					this.unifiedMetaMap.get(this.prevMappedSourceIdMap.get(sourceMetaId)).updateScoresAndScoreCounts(
							meta.getService(),
							meta.getScore().get(0),
							meta.getScoreCount().get(0)
					);
				}

				// 3. update purchase count
				if (meta.getService().equals(Prop.SERVICE_NAME_HOPPIN) ||
						meta.getService().equals(Prop.SERVICE_NAME_TSTORE)) {
					this.unifiedMetaMap.get(this.prevMappedSourceIdMap.get(sourceMetaId)).updatePurchaseCount(
							Prop.getDataSourceCodeFromDataSourceName(meta.getService()),
							meta.getPurchaseCount().get(0)
					);
				}
				continue;
			}
			// -------------------------------------------------------
			// new item! found a match!
			// -------------------------------------------------------
			this.unifiedMetaLookUp(meta);
		}
		updateVersions(this.category, this.version);
		end = System.currentTimeMillis();
		LOGGER.info("Mapping Done (" + this.service + "-" + this.category + "-" + this.source + "), in " + (end - begin) + " msec.");


		return this.unifiedMetaCollection;
	}


	/**
	 * Look up similar unified meta for the given source meta
	 * @param sourceMeta source meta
	 */
	private void unifiedMetaLookUp(Meta sourceMeta) {

		// if there is no pre-loaded unified meta,
		// then create new unified meta and add it
		if (this.unifiedMetaCollection.size() == 0) {
			//
			// WARN: add only service meta! not external meta
			//
			if (sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_SKB) ||
					sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_HOPPIN) ||
					sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_TSTORE)
					) {

				Config newUnifiedMetaConfig = ConfigFactory.parseString(makeUnifiedMetaHocon(sourceMeta));
				UnifiedMeta newUnifiedMeta = new UnifiedMeta(newUnifiedMetaConfig, this.category);
				this.unifiedMetaCollection.add(newUnifiedMeta);
				this.unifiedMetaMap.put(newUnifiedMeta.getId(), newUnifiedMeta);
				// [TADW] log it for TADW managed file
				LOGGER.info("[TADW-UPDATE] :: " + newUnifiedMeta.getId() + "\tI\tI");
				return;
			}
		}

		double titleSim;
		double dateSim = 0.0;
		double directorSim;
		double actorSim;

		double titleSimMax = 0.0;
		double dateSimMax = 0.0;
		double directorSimMax = 0.0;
		double actorSimMax = 0.0;

		double simMax = 0.0;

		String result = null;
		String sNormTitle = this.utilities.getNormalizedTitleWithStopword(sourceMeta.getTitle());
		Set<String> uIdSet = this.unifiedMetaMap.keySet();
		for (String uId : uIdSet) {
			double sim = 0.0;

			if(this.source.equals(Prop.SERVICE_NAME_HOPPIN) ||
					this.source.equals(Prop.SERVICE_NAME_SKB) ||
					this.source.equals(Prop.SERVICE_NAME_TSTORE)
					) {
				List<String> sourceSplitList = (List<String>) splitMap.get(Prop.getServiceCodeFromServiceName(this.source) + "_" + sourceMeta.getId());
				boolean isSplitId = false;
				if (sourceSplitList != null) {
					isSplitId = Utilities.ListOverlapCheck(sourceSplitList, this.unifiedMetaMap.get(uId).getSourceIdList());
					if (isSplitId = true) {
						continue;
					}
				} /*else {
                    LOGGER.debug("source split list is null: " + this.service + ":" + this.category + ":" + this.source + " ==> " +
                            Prop.getServiceCodeFromServiceName(this.source) + "_" + sourceMeta.getId());
                }*/
			}

			// check mapping exception list
			if (exceptionPidMap.get(uId) != null) {
				if (((List<String>)exceptionPidMap.get(uId)).contains(sourceMeta.getId())) {
					continue;
				}
			}

			// ------------------- //
			// title similarities
			// ------------------- //
			UnifiedMeta uMeta = this.unifiedMetaMap.get(uId);
			String uNormTitle = this.utilities.getNormalizedTitleWithStopword(uMeta.getTitle());
			titleSim = LCSequence.getLCSRatio(sNormTitle, uNormTitle);

			LOGGER.debug("title comparison: " +
							uNormTitle + "(unified:" + uMeta.getId() + ") - " +
							sNormTitle + "(" + sourceMeta.getService() +
							":" + sourceMeta.getId() + ") : " +
							titleSim * 100.0 + "%"
			);

			// ------------------- //
			// date similarities
			// ::: 유연성을 위해서 년도만 체크한다
			// ------------------- //
			if (!sourceMeta.getDate().equals("null") && !uMeta.getDate().equals("null")) {
				int sDate = 0;
				int uDate = 0;
				// format check
				if (sourceMeta.getDate().length() < 4) {
					LOGGER.info("date length is less than 4: " + sourceMeta.getId() + " : " + sourceMeta.getTitle());
				} else {
					sDate = Integer.parseInt(sourceMeta.getDate().substring(0, 4));
				}

				if (uMeta.getDate().length() < 4) {
					LOGGER.info("date length is less than 4: " + uMeta.getId() + " : " + uMeta.getTitle());
				} else {
					uDate = Integer.parseInt(uMeta.getDate().substring(0, 4));
				}

				// TODO: 일단 date 유사도 비교는 binary (true|false) 이지만, 날짜의 차이등을 고려하여 유사도를 세분화 할 수도 있지 않을까?
				/*
				if (sDate - uDate != 0) {
					dateSim = 0.0;
				} else {
					dateSim = 1.0;
				}
				*/
				dateSim = 1.0;
			}
			LOGGER.debug("date comparison: " +
							uMeta.getDate() + "(unified:" + uMeta.getId() + ") - " +
							sourceMeta.getDate() + "(" + sourceMeta.getService() + ":" + sourceMeta.getId() + ") : " +
							dateSim * 100 + "%"
			);

			// ------------------- //
			// director similarities
			// ------------------- //
			List<String> sourceDirectors = sourceMeta.getDirectors();
			List<String> unifiedDirectors = unifiedMetaMap.get(uId).getDirectors();
			directorSim = getSimilarityForDirectorsAndActors(sourceDirectors, unifiedDirectors);

			LOGGER.debug("director comparison: " +
							uMeta.getDirectors() + "(unified:" + uMeta.getId() + ") - " +
							sourceMeta.getDirectors() + "(" + sourceMeta.getService() + ":" + sourceMeta.getId() + ") : " +
							directorSim * 100 + "%"
			);

			// 속도를 위해 exact matching 이 발생하면 바로 중단하고 look up 하지 않는다
			if (directorSim == 1.0) {
				double midSim = titleSim * TITLE_WEIGHT + dateSim * DATE_WEIGHT + directorSim * DIRECTOR_WEIGHT + 1.0 * ACTOR_WEIGHT;
				if (midSim > this.mappingThreshold) {
					LOGGER.debug("actors comparison skipped!");
					simMax = midSim;
					result = uId;
					titleSimMax = titleSim;
					dateSimMax = dateSim;
					directorSimMax = directorSim;
					actorSimMax = 1.0;
					break;
				}
			}

			// ------------------- //
			// actor similarities
			// ------------------- //
			List<String> sourceActors = sourceMeta.getActors();
			List<String> unifiedActors = unifiedMetaMap.get(uId).getActors();
			actorSim = getSimilarityForDirectorsAndActors(sourceActors, unifiedActors);

			LOGGER.debug("actors comparison: " +
							uMeta.getActors() + "(unified:" + uMeta.getId() + ") - " +
							sourceMeta.getActors() + "(" + sourceMeta.getService() + ":" + sourceMeta.getId() + ") : " +
							actorSim * 100 + "%"
			);

			sim = titleSim * TITLE_WEIGHT + dateSim * DATE_WEIGHT + directorSim * DIRECTOR_WEIGHT + actorSim * ACTOR_WEIGHT;
			if (simMax < sim) {
				result = uId;
				simMax = sim;
				titleSimMax = titleSim;
				dateSimMax = dateSim;
				directorSimMax = directorSim;
				actorSimMax = actorSim;
			}
		}

		// found
		if (simMax >= this.mappingThreshold) {

			LOGGER.info("Final Mapping: " + unifiedMetaMap.get(result).getId() + " <=> " + sourceMeta.getId() + " (t:" +
							titleSimMax * 100.0 + ", r:" + dateSimMax * 100.0 + ", d:" + directorSimMax * 100.0 + ", a:" +
							actorSimMax * 100.0 + ")"
			);

			// add source id
			String dataSourceCode = Prop.getDataSourceCodeFromDataSourceName(sourceMeta.getService());
			this.unifiedMetaMap.get(result).addSourceId(dataSourceCode + "_" + sourceMeta.getId());

			// add keywords if available
			if (sourceMeta.getClass() == MovieMetaKMDB.class) {
				if (((MovieMetaKMDB) sourceMeta).getKeywords().size() > 0) {
					for (String keyword : ((MovieMetaKMDB) sourceMeta).getKeywords()) {
						this.unifiedMetaMap.get(result).addKeywords(keyword);
					}
				}
			}

			// update score if needed,
			if (sourceMeta.getScore().size() > 0) {
				this.unifiedMetaMap.get(result).updateScoresAndScoreCounts(
						sourceMeta.getService(),
						sourceMeta.getScore().get(0),
						sourceMeta.getScoreCount().get(0)
				);
			}

			// 만약 unified meta의 등급이 null이고, source 쪽의 meta 등급이 null 이 아니라면 update하라
			if (this.unifiedMetaMap.get(result).getRate().equals("null") && !sourceMeta.getRate().equals("null")) {
				this.unifiedMetaMap.get(result).setRate(sourceMeta.getRate());
			}
		}
		// not found, so create new unified meta and add
		else {

			LOGGER.info("Mapping Threshold : " + this.mappingThreshold);
			LOGGER.info("SimMax : " + simMax);
			LOGGER.info("Final Mapping: " + unifiedMetaMap.get(result).getId() + " <=> " + sourceMeta.getId() + " (t:" +
							titleSimMax * 100.0 + ", r:" + dateSimMax * 100.0 + ", d:" + directorSimMax * 100.0 + ", a:" +
							actorSimMax * 100.0 + ")"
			);

            /*
            if (this.source.equals(Prop.DATA_SOURCE_NAME_KMDB) ||
                    this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                return;
            }
            */
			//
			// WARN: add only service meta! not external meta
			//
			if (sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_SKB) ||
					sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_HOPPIN) ||
					sourceMeta.getService().equals(Prop.DATA_SOURCE_NAME_TSTORE)
					) {
				Config newUnifiedMetaConfig = ConfigFactory.parseString(makeUnifiedMetaHocon(sourceMeta));
				UnifiedMeta newUnifiedMeta = new UnifiedMeta(newUnifiedMetaConfig, this.category);
				this.unifiedMetaCollection.add(newUnifiedMeta);
				this.unifiedMetaMap.put(newUnifiedMeta.getId(), newUnifiedMeta);
				// [TADW] if nothing matched, then create new unified meta and log it
				LOGGER.info("[TADW-UPDATE] :: " + this.category + "\t" + newUnifiedMeta.getId() + "\tI\tI");
			} else {
				LOGGER.info("NOT MATCHED:" + service + ":" + category + ":" + source + "==>" + sourceMeta.getId());
			}
		}
	}

	private double getSimilarityForDirectorsAndActors(List<String> sPeople, List<String> uPeople ) {
		int compMax = 3;

		List<String> aList;
		List<String> bList;

		if (sPeople.size() < uPeople.size()) {
			aList = uPeople;
			bList = sPeople;
		} else {
			aList = sPeople;
			bList = uPeople;
		}

		double editSim = -1;
		for (int i = 0; i < aList.size(); i++) {
			if (i > compMax) {
				break;
			}
			for (int j = 0; j < bList.size(); j++) {
				if (j > compMax) {
					break;
				}
				double sim = EditDistance.similarity(aList.get(i), bList.get(j));
				if (editSim < sim) {
					editSim = sim;
				}
			}
			// 만약 exact matching 이 한건이라도 있으면 1.0의 신뢰도를 보낸다
			if (editSim == 1.0) {
				return editSim;
			}
		}

		return editSim;
	}

	/**
	 * Update hdfs version update
	 * @param category category to update
	 * @param version version to update
	 */
	private void updateVersions(String category, String version) {
		Config hadoopConfig = ConfigFactory.load(Prop.HADOOP_CONFIG_NAME);
		HDFSUtil hdfsUtil = new HDFSUtil();
		String recentVersions = hdfsUtil.read(hadoopConfig.getString("unified-meta-version-path")).trim();
		Map<String, String> categoryVersionsMap = new HashMap<String, String>();
		Config versionConfig = ConfigFactory.parseString(recentVersions);
		String animationRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_ANIMATION);
		String dramakRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_DRAMAK);
		String dramafRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_DRAMAF);
		String movieRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_MOVIE);

		categoryVersionsMap.put(Prop.CATEGORY_NAME_ANIMATION, animationRecentVersion);
		categoryVersionsMap.put(Prop.CATEGORY_NAME_DRAMAF, dramafRecentVersion);
		categoryVersionsMap.put(Prop.CATEGORY_NAME_DRAMAK, dramakRecentVersion);
		categoryVersionsMap.put(Prop.CATEGORY_NAME_MOVIE, movieRecentVersion);

		categoryVersionsMap.remove(category);
		categoryVersionsMap.put(category, version);

		StringBuffer sb = new StringBuffer();
		for (String cate : categoryVersionsMap.keySet()) {
			sb.append(cate + " = \"" + categoryVersionsMap.get(cate) + "\"\n");
		}

		hdfsUtil.write(hadoopConfig.getString("unified-meta-version-path"), sb.toString());
	}

	/**
	 * Create Hocon formatted Unified Meta from the given source meta
	 * @param sourceMeta source meta to be formatted
	 * @return hocon formatted unified meta
	 */
	private String makeUnifiedMetaHocon(Meta sourceMeta) {
		// increase current uid by one
		this.currentUnifiedIdInteger++;
		String uIdNew = "C" + String.format("%08d", this.currentUnifiedIdInteger);
		StringBuffer uMetaNewSb = new StringBuffer();
		uMetaNewSb.append("id = \"").append(uIdNew).append("\"\n");
		uMetaNewSb.append("sid-list = [\n");
		uMetaNewSb.append("\t\t\"")
				.append(Prop.getDataSourceCodeFromDataSourceName(sourceMeta.getService()))
				.append("_")
				.append(sourceMeta.getId())
				.append("\",\n");
		uMetaNewSb.append("]\n");
		uMetaNewSb.append("title = \"").append(sourceMeta.getTitle()).append("\"\n");
		uMetaNewSb.append("org-title = \"").append(sourceMeta.getOrgTitle()).append("\"\n");
		uMetaNewSb.append("date = \"").append(sourceMeta.getDate()).append("\"\n");
		uMetaNewSb.append("synopsis = \"").append(sourceMeta.getSynopsis()).append("\"\n");
		uMetaNewSb.append("rate = \"").append(sourceMeta.getRate()).append("\"\n");
		uMetaNewSb.append("genres = [\n");
		for (String genre : sourceMeta.getGenres()) {
			uMetaNewSb.append("\t\t\"").append(genre).append("\",\n");
		}
		uMetaNewSb.append("]\n");
		uMetaNewSb.append("directors = [\n");
		for (String director : sourceMeta.getDirectors()) {
			uMetaNewSb.append("\t\t\"").append(director).append("\",\n");
		}
		uMetaNewSb.append("]\n");
		uMetaNewSb.append("actors = [\n");
		for (String actor : sourceMeta.getActors()) {
			uMetaNewSb.append("\t\t\"").append(actor).append("\",\n");
		}
		uMetaNewSb.append("]\n");
		uMetaNewSb.append("scores = [\n");
		if (sourceMeta.getScore().size() > 0) {
			uMetaNewSb.append("\t\t\"").append(
					Prop.getDataSourceCodeFromDataSourceName(sourceMeta.getService()) +
							"_" +
							sourceMeta.getScore().get(0)).append("\"\n");
		}
		uMetaNewSb.append("]\n");
		uMetaNewSb.append("score-counts = [\n");
		if (sourceMeta.getScoreCount().size() > 0) {
			uMetaNewSb.append("\t\t\"").append(
					Prop.getDataSourceCodeFromDataSourceName(sourceMeta.getService()) +
							"_" +
							sourceMeta.getScoreCount().get(0)).append("\"\n");
		}
		uMetaNewSb.append("]\n");
		//uMetaNewSb.append("purchase = ").append(sourceMeta.getPurchaseCount()).append("\n");
		uMetaNewSb.append("purchase = [\n");
		if (sourceMeta.getPurchaseCount().size() > 0) {
			if (!sourceMeta.getPurchaseCount().get(0).equals("null")) {
				uMetaNewSb.append("\t\t\"").append(
						Prop.getDataSourceCodeFromDataSourceName(sourceMeta.getService()) +
								"_" +
								sourceMeta.getPurchaseCount().get(0)).append("\"\n");
			}
		}
		uMetaNewSb.append("]\n");
		if (sourceMeta instanceof MovieMetaKMDB) {
			uMetaNewSb.append("keywords = [\n");
			for (String keyword : ((MovieMetaKMDB) sourceMeta).getKeywords()) {
				uMetaNewSb.append("\t\t\"").append(keyword).append("\",\n");
			}
			uMetaNewSb.append("]\n");
		} else {
			uMetaNewSb.append("keywords = [\n");
			uMetaNewSb.append("]\n");
		}
		uMetaNewSb.append("nations = [\n");
		for (String nation : sourceMeta.getNations()) {
			uMetaNewSb.append("\t\t\"").append(nation).append("\",\n");
		}
		uMetaNewSb.append("]\n");
		return uMetaNewSb.toString();
	}
}
