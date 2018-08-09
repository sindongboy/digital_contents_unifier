package com.skplanet.nlp.unifier.dc.data.episode;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Episode Loader
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/18/15
 */
public class EpisodeLoader {
    private static final Logger LOGGER = Logger.getLogger(EpisodeLoader.class.getName());

    private static final String DATA_TYPE = "episode";
    private Config mainConfig = null;

    private final String service;
    private final String category;
    private final String source;
    private final String version;
    private final String metaFilePath;

    public EpisodeLoader(final String service, final String category, final String source, final String version) {
        this.service = service;
        this.category = category;
        this.source = source;
        this.version = version;
        this.metaFilePath = service + "-" + category + "-" + source + "-" + DATA_TYPE + "-" + version;
        this.mainConfig = ConfigFactory.load(this.metaFilePath);
    }

    /**
     * Load Meta specified by the given service, category, version
     * @return collection of meta data loaded
     */
    public Collection<Episode> load() {
        List<Config> subItems = new ArrayList<Config>(mainConfig.getConfigList(DATA_TYPE));
        Collection<Episode> result = new ArrayList<Episode>();
        long begin = System.currentTimeMillis();
        for (Config singleItem : subItems) {
            Episode singleEpisode = null;
            if (this.category.equals(Prop.CATEGORY_NAME_ANIMATION)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleEpisode = new AnimationEpisodeDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleEpisode = new AnimationEpisodeHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet.
                    //singleEpisode = new AnimationMetaTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAK)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleEpisode = new DramaDomesticEpisodeDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleEpisode = new DramaDomesticEpisodeHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_SKB)) {
                    singleEpisode = new DramaDomesticEpisodeSKB(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet
                    //singleEpisode = new DramaDomesticTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else if (this.category.equals(Prop.CATEGORY_NAME_DRAMAF)) {
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    singleEpisode = new DramaForeignEpisodeDaum(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    singleEpisode = new DramaForeignEpisodeHoppin(singleItem);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    // service not defined yet
                    //singleEpisode = new DramaForeignTstore(singleItem);
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else if (this.category.equals(Prop.CATEGORY_NAME_MOVIE)) {
                // TODO: Movie Category Must be handled soon. 20150317 by Donghun Shin
                if (this.source.equals(Prop.DATA_SOURCE_NAME_DAUM)) {
                    LOGGER.error("data source not defined yet: " + this.source);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_NAVER)) {
                    LOGGER.error("data source not defined yet: " + this.source);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_HOPPIN)) {
                    LOGGER.error("data source not defined yet: " + this.source);
                } else if (this.source.equals(Prop.DATA_SOURCE_NAME_TSTORE)) {
                    LOGGER.error("data source not defined yet: " + this.source);
                } else {
                    LOGGER.error("unknown data source type: " + this.source);
                }
            } else {
                LOGGER.error("unknown category type: " + this.category);
            }

            result.add(singleEpisode);
        }
        long end = System.currentTimeMillis();
        LOGGER.info("episode data loaded : " + this.metaFilePath + " (" + result.size() + "), in " + (end - begin) + " msec.");

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
        EpisodeLoader episodeLoader = new EpisodeLoader(
                cli.getOption("s"),
                cli.getOption("c"),
                cli.getOption("u"),
                cli.getOption("v"));

        long begin = System.currentTimeMillis();
        Collection<Episode> metaList = episodeLoader.load();
        for (Episode episode : metaList) {
            if (episode.getContent().size() == 0) {
                System.out.println(episode.getId());
                break;
            }
            System.out.println(episode.getId() + " : " + episode.getContent().get(0).getSeriesName() + " : " + episode.getContent().get(0).getSeriesContents());

        }
        long end = System.currentTimeMillis();

        System.out.println("loaded in " + (end - begin) + " msec.");
    }
}
