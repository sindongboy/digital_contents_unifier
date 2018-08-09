package com.skplanet.nlp.unifier.dc.data.episode;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/18/15
 */
public abstract class AbsEpisode implements Episode {
    private static final Logger LOGGER = Logger.getLogger(AbsEpisode.class.getName());

    // Series ID
    protected String id;

    // Series Content List
    protected List<Series> content;

    /**
     * Series Information
     */
    public class Series {
        // series title
        protected String seriesName;
        // series content
        protected String seriesContents;

        /**
         * Get Series Name
         * @return series name
         */
        public String getSeriesName() {
            return seriesName;
        }

        /**
         * Set Series Name
         * @param seriesName series name to be set
         */
        public void setSeriesName(String seriesName) {
            this.seriesName = seriesName;
        }

        /**
         * Get Series Content
         * @return series content
         */
        public String getSeriesContents() {
            return seriesContents;
        }

        /**
         * Set Series Content
         * @param seriesContents series content to be set
         */
        public void setSeriesContents(String seriesContents) {
            this.seriesContents = seriesContents;
        }
    }

    /**
     * Get Series ID
     * @return series id
     */
    public String getId() {
        return id;
    }

    /**
     * Get Series Content
     * @return series content
     */
    public List<Series> getContent() {
        return content;
    }
}

