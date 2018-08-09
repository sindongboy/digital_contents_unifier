package com.skplanet.nlp.unifier.dc.data.meta;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.skplanet.nlp.unifier.dc.util.HDFSUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Meta Loading Class
 *
 * It supports following service and category
 *
 * // category ( service )
 * - movie ( hoppin, tstore )
 * - domestic TV drama ( hoppin )
 * - foreign TV drama ( hoppin )
 * - animation ( hoppin )
 *
 * ******************
 * * VERY IMPORTANT *
 * ******************
 * whenever you add a service or category, you must do following
 * - create inheritance of {@link AbsMeta} class
 * - implements {@link Meta} interface
 * - add a service or category to 'load()' method down here
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class MetaLoader {
    private static final Logger LOGGER = Logger.getLogger(MetaLoader.class.getName());

    private Config hadoopConfig = null;
    private Config metaConfig = null;

    private final String service;
    private final String category;
    private final String source;
    private final String version;

    private String metaFilePath;

    private HDFSUtil hdfsUtil = null;

    /**
     * Meta Loader Constructor
     * @param service service name
     * @param category category name
     * @param source data source
     * @param version version
     */
    public MetaLoader(final String service, final String category, final String source, final String version) {
        this.service = service;
        this.category = category;
        if (source == null) {
            this.source = this.service;
        } else {
            this.source = source;
        }
        this.version = version;
    }

    /**
     * Load Unified Meta file from HDFS
     * only needs 'category'
     * @return collection of unified meta, null if nothing exists
     */
    public Collection<Meta> loadUnifiedMeta() {
        this.hadoopConfig = ConfigFactory.load(Prop.HADOOP_CONFIG_NAME);
        this.hdfsUtil = new HDFSUtil();

        // get all umeta versions in hdfs root
        String recentVersions = hdfsUtil.read(hadoopConfig.getString("unified-meta-version-path")).trim();
        Config versionConfig = ConfigFactory.parseString(recentVersions);
        String animationRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_ANIMATION);
        String dramakRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_DRAMAK);
        String dramafRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_DRAMAF);
        String movieRecentVersion = versionConfig.getString(Prop.CATEGORY_NAME_MOVIE);
        String recentVersion;

        if (this.category.equals(Prop.CATEGORY_NAME_ANIMATION)) {
            this.metaFilePath = this.hadoopConfig.getString(Prop.UNIFIED_META_ANIMATION_HDFS_PATH);
            recentVersion = animationRecentVersion;
            animationRecentVersion = this.version;
        } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAK)) {
            this.metaFilePath = this.hadoopConfig.getString(Prop.UNIFIED_META_DRAMAK_HDFS_PATH);
            recentVersion = dramakRecentVersion;
            dramakRecentVersion = this.version;
        } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAF)) {
            this.metaFilePath = this.hadoopConfig.getString(Prop.UNIFIED_META_DRAMAF_HDFS_PATH);
            recentVersion = dramafRecentVersion;
            dramafRecentVersion = this.version;
        } else if (this.category.equals(Prop.CATEGORY_NAME_MOVIE)) {
            this.metaFilePath = this.hadoopConfig.getString(Prop.UNIFIED_META_MOVIE_HDFS_PATH);
            recentVersion = movieRecentVersion;
            movieRecentVersion = this.version;
        } else {
            LOGGER.error("No Unified Meta Path found for the given category");
            return null;
        }

        // recent version doesn't exist
        if (recentVersion.equals("null")) {
            // no umeta for loading
            return null;
        }

        // load most recent umeta file
        String unifiedMetaFileName = this.category + "-" + Prop.UNIFIED_META_EXT + "-" + recentVersion;
        String unifiedMetaRaw = this.hdfsUtil.read(this.metaFilePath + unifiedMetaFileName);

        // umeta doesn't exist, then something is wrong!
        if (unifiedMetaRaw == null) {
            LOGGER.info("no unified meta exist!");
            return null;
        }

        // parsing config items
        Config unifiedMetaConfig = ConfigFactory.parseString(unifiedMetaRaw);
        List<Config> subItems = new ArrayList<Config>(unifiedMetaConfig.getConfigList(Prop.UNIFIED_META_EXT));

        // no unified meta in it yet...
        if (subItems.size() == 0) {
            LOGGER.info("no unified meta exist!");
            return null;
        }

        // unified meta loading
        long begin, end;
        begin = System.currentTimeMillis();
        Collection<Meta> unifiedMetaCollection = new ArrayList<Meta>();
        for (Config item : subItems) {
            UnifiedMeta meta = new UnifiedMeta(item, this.category);
            if (meta.getSourceIdList().size() == 0) {
                continue;
            }
            unifiedMetaCollection.add(meta);
        }
        end = System.currentTimeMillis();
        LOGGER.info("unified meta data loaded : " + this.metaFilePath + " (" + unifiedMetaCollection.size() + "), in " + (end - begin) + " msec.");
        return unifiedMetaCollection;
    }

    /**
     * Load Meta specified by the given service, category, version
     * @return collection of meta data loaded
     */
    public Collection<Meta> loadSourceMeta() {
        this.metaFilePath = this.service + "-" + this.category + "-" + this.source + "-" + Prop.SOURCE_META_EXT + "-" + version;
        this.metaConfig = ConfigFactory.load(this.metaFilePath);
        LOGGER.info("try to load source meta: " + this.metaFilePath);
        List<Config> subItems = new ArrayList<Config>(metaConfig.getConfigList(Prop.SOURCE_META_EXT));

        Collection<Meta> result = new ArrayList<Meta>();
        long begin = System.currentTimeMillis();
        for (Config singleItem : subItems) {
            Meta singleMeta = null;
            // ---------- //
            // animation
            // ---------- //
            if (this.category.equals(Prop.CATEGORY_NAME_ANIMATION)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleMeta = new AnimationMetaDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                    singleMeta = new AnimationMetaNaver(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleMeta = new AnimationMetaHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet.
                    //singleMeta = new AnimationMetaTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            // ---------- //
            // dramak
            // ---------- //
            } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAK)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleMeta = new DramaDomesticMetaDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                    singleMeta = new DramaDomesticMetaNaver(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleMeta = new DramaDomesticMetaHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_SKB)) {
                    singleMeta = new DramaDomesticMetaSKB(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet
                    //singleMeta = new DramaDomesticTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAF)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleMeta = new DramaForeignMetaDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                    singleMeta = new DramaForeignMetaNaver(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleMeta = new DramaForeignMetaHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet
                    //singleMeta = new DramaForeignTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else if (this.category.equals(Prop.CATEGORY_NAME_MOVIE)) {
                // hoppin (V), naver (V), kmdb(V), tstore(V)
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    LOGGER.error("data source not defined yet: " + this.source);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                    singleMeta = new MovieMetaNaver(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_KMDB)) {
                    singleMeta = new MovieMetaKMDB(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleMeta = new MovieMetaHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    singleMeta = new MovieMetaTstore(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_MAXMOVIE)) {
                    singleMeta = new MovieMetaMaxmovie(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_SKB)) {
                    singleMeta = new MovieMetaSKB(singleItem);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else {
                LOGGER.error("unknown category type: " + this.category);
            }

            result.add(singleMeta);
        }
        long end = System.currentTimeMillis();
        LOGGER.info("meta data loaded : " + this.metaFilePath + " (" + result.size() + "), in " + (end - begin) + " msec.");

        return result;
    }

    /**
     * Sample Program
     * @param args no args needed
     */
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "service", true, "service name", true);
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("u", "source", true, "data source name", true);
        cli.addOption("v", "version", true, "version", true);
        cli.parseOptions(args);
        MetaLoader metaLoader = new MetaLoader(
                cli.getOption("s"),
                cli.getOption("c"),
                cli.getOption("u"),
                cli.getOption("v"));

        Collection<Meta> metaList = metaLoader.loadUnifiedMeta();

        /*
        long begin = System.currentTimeMillis();
        Collection<Meta> metaList = metaLoader.loadSourceMeta();
        long end = System.currentTimeMillis();

        for (Meta meta : metaList) {
            //System.out.println(meta.getId() + "\t" + meta.getTitle());
            System.out.println(meta.toIndentedString(2));
        }

        System.out.println("-----------------------");
        System.out.println(metaList.size() + " items are loaded in " + (end - begin) + " msec.");
        */
    }
}
