package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Meta Class for KMDB
 *
 * it has special field 'keywords'
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public final class MovieMetaKMDB extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(MovieMetaKMDB.class.getName());

    private static final String SERVICE = "kmdb";
    private static final String CATEGORY = "movie";

    // KMDB Meta has Contents Keywords List
    private List<String> keywords;

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public MovieMetaKMDB(final Config item) {
        super(item);
        this.keywords = item.getStringList("keywords");
    }

        /**
     * Get Service Name
     *
     * @return service name
     */
    @Override
    public String getService() {
        return SERVICE;
    }

    /**
     * Get Category Name
     *
     * @return category name
     */
    @Override
    public String getCategory() {
        return CATEGORY;
    }

    /**
     * Get Contents Keywords
     * @return content keyword list
     */
    public List<String> getKeywords() {
        if (this.keywords == null) {
            this.keywords = new ArrayList<String>();
        }
        return this.keywords;
    }
}
