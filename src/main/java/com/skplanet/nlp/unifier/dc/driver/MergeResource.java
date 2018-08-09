package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.data.comment.Comments;
import com.skplanet.nlp.unifier.dc.data.episode.AbsEpisode;
import com.skplanet.nlp.unifier.dc.data.episode.Episode;
import com.skplanet.nlp.unifier.dc.data.episode.EpisodeLoader;
import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.data.meta.UnifiedMeta;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Merge Resource by Their CID
 * TODO: 서비스, 카테고리에 의존적이지 않은 실행옵션 로직 만들어야 함.
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/13/15
 */
public class MergeResource {
    private static final Logger LOGGER = Logger.getLogger(MergeResource.class.getName());
    public static void main(String[] args) throws IOException {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "service", true, "service name", true);
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.addOption("o", "output", true, "output base", true);
        cli.parseOptions(args);

        String service = cli.getOption("s");
        String category = cli.getOption("c");
        String version = cli.getOption("v");
        BufferedWriter writer;

        // -------------- //
        // meta loader
        // -------------- //

        // external meta (naver)
        MetaLoader naverMetaLoader = new MetaLoader(service, category, "naver", version);
        Collection<Meta> naverMetaCollection = naverMetaLoader.loadSourceMeta();
        Map<String, Meta> naverMetaMap = new HashMap<String, Meta>();
        for (Meta meta : naverMetaCollection) {
            naverMetaMap.put(meta.getId(), meta);
        }

        // external meta (daum)
        Map<String, Meta> daumMetaMap;
        if(!category.equals(Prop.CATEGORY_NAME_MOVIE)) {
            MetaLoader daumMetaLoader = new MetaLoader(service, category, "daum", version);
            Collection<Meta> daumMetaCollection = daumMetaLoader.loadSourceMeta();
            daumMetaMap = new HashMap<String, Meta>();
            for (Meta meta : daumMetaCollection) {
                daumMetaMap.put(meta.getId(), meta);
            }
        }

        // external meta (kmdb)
        Map<String, Meta> kmdbMetaMap;
        if(category.equals(Prop.CATEGORY_NAME_MOVIE)) {
            MetaLoader kmdbMetaLoader = new MetaLoader(service, category, "kmdb", version);
            Collection<Meta> kmdbMetaCollection = kmdbMetaLoader.loadSourceMeta();
            kmdbMetaMap = new HashMap<String, Meta>();
            for (Meta meta : kmdbMetaCollection) {
                kmdbMetaMap.put(meta.getId(), meta);
            }
        }


        // service meta ( hoppin / tstore / skb etc )
        MetaLoader serviceMetaLoader = new MetaLoader(service, category, service, version);
        Map<String, Meta> serviceMetaMap = new HashMap<String, Meta>();
        Collection<Meta> serviceMetaCollection = serviceMetaLoader.loadSourceMeta();
        for (Meta meta : serviceMetaCollection) {
            serviceMetaMap.put(meta.getId(), meta);
        }

        // unified meta
        MetaLoader unifiedMetaLoader = new MetaLoader(null, category, null, version);
        Collection<Meta> unifiedMetaCollection = unifiedMetaLoader.loadUnifiedMeta();
        Map<String, Meta> unifiedMetaMap = new HashMap<String, Meta>();
        for (Meta meta : unifiedMetaCollection) {
            unifiedMetaMap.put(meta.getId(), meta);
        }

        // -------------- //
        // comment
        // -------------- //

        // naver comment
        Comments naverComments = new Comments(service, category, "naver", version);
        int naverCommentsExist = naverComments.load();

        Comments daumComments = null;
        if(category.equals(Prop.CATEGORY_NAME_DRAMAF) ||
                category.equals(Prop.CATEGORY_CODE_DRAMAK) ||
                category.equals(Prop.CATEGORY_CODE_ANIMATION)) {
            daumComments = new Comments(service, category, "daum", version);
            int daumCommentsExist = daumComments.load();
        }

        // -------------- //
        // episode
        // -------------- //
        Map<String, Episode> daumEpisodeMap = null;
        Map<String, Episode> serviceEpisodeMap = null;
        if(!category.equals(Prop.CATEGORY_NAME_MOVIE)) {
            // daum episode
            EpisodeLoader daumEpisodeLoader = new EpisodeLoader(service, category, "daum", version);
            Collection<Episode> daumEpisodeCollection = daumEpisodeLoader.load();
            daumEpisodeMap = new HashMap<String, Episode>();
            for (Episode episode : daumEpisodeCollection) {
                daumEpisodeMap.put(episode.getId(), episode);
            }

            // service episode
            EpisodeLoader serviceEpisodeLoader = new EpisodeLoader(service, category, service, version);
            Collection<Episode> serviceEpisodeCollection = serviceEpisodeLoader.load();
            serviceEpisodeMap = new HashMap<String, Episode>();
            for (Episode episode : serviceEpisodeCollection) {
                serviceEpisodeMap.put(episode.getId(), episode);
            }
        }

        // synopsis merge
        // loop through unified meta collection
        String outputBase = cli.getOption("o") + "/" + category;

        // for comments
        /*
        for (Meta unifiedMeta : unifiedMetaCollection) {
            String fileName = unifiedMeta.getId();
            File file = new File(outputBase + "/comments/" + fileName);
            List<String> sIdList = ((UnifiedMeta) unifiedMeta).getSourceIdList();

            if (sIdList == null) {
                LOGGER.info("sid list is null: " + unifiedMeta.getId());
                continue;
            }

            for (String sId : sIdList) {
                List<String> commentList;
                if (sId.startsWith("NV")) {
                    commentList = naverComments.getComments(sId.replace("NV_", ""));
                } else if (sId.startsWith("DM")) {
                    commentList = daumComments.getComments(sId.replace("DM_", ""));
                } else {
                    continue;
                }

                if (commentList == null) {
                    continue;
                }

                writer = new BufferedWriter(new FileWriter(file, true));
                for (String comment : commentList) {
                    writer.write(comment);
                    writer.newLine();
                }
                writer.close();
            }
        }
        */


        // for synopsis
        if(category.equals("dramak") || category.equals("dramaf") || category.equals("animation")) {
            for (Meta unifiedMeta : unifiedMetaCollection) {
                String fileName = unifiedMeta.getId();
                File file = new File(outputBase + "/synopsis/" + fileName);
                List<String> sIdList = ((UnifiedMeta) unifiedMeta).getSourceIdList();

                if (sIdList == null) {
                    LOGGER.info("sid list is null: " + unifiedMeta.getId());
                    continue;
                }

                writer = new BufferedWriter(new FileWriter(file, true));
                // ***** IMPORTANT ***** //
                // USE HOPPIN SYNOPSIS ONLY
                String synop = unifiedMeta.getSynopsis();
                writer.write("RS\t" + synop);
                writer.newLine();
                for (String sId : sIdList) {
                    List<AbsEpisode.Series> episodeList = null;
                    if (sId.startsWith("DM")) {
                        if (daumEpisodeMap.containsKey(sId.replace("DM_", ""))) {
                            episodeList = daumEpisodeMap.get(sId.replace("DM_", "")).getContent();
                        } else {
                            continue;
                        }
                    } else if (sId.startsWith("HP")) {
                        if (serviceEpisodeMap.containsKey(sId.replace("HP_", ""))) {
                            episodeList = serviceEpisodeMap.get(sId.replace("HP_", "")).getContent();
                        } else {
                            continue;
                        }
                    } else if(sId.startsWith("SB")) {
                        if (serviceEpisodeMap.containsKey(sId.replace("SB_", ""))) {
                            episodeList = serviceEpisodeMap.get(sId.replace("SB_", "")).getContent();
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }

                    if (episodeList == null) {
                        continue;
                    }

                    for (AbsEpisode.Series episode : episodeList) {
                        writer.write("ES\t" + episode.getSeriesContents());
                        writer.newLine();
                    }
                }
                writer.close();
            }
        }

        System.out.println("done");
    }
}
