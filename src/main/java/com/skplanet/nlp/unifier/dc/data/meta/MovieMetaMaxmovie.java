package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * for Maxmovie ( http://www.maxmovie.com ) Meta Data
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/11/15
 */
public class MovieMetaMaxmovie extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(MovieMetaMaxmovie.class.getName());

    public static final String SERVICE = "maxmovie";
    public static final String CATEGORY = "movie";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public MovieMetaMaxmovie(final Config item) {
        super(item);
    }

    /**
     * Get Service Name
     *
     * @return service name
     */
    @Override
    public String getService() {
        return this.SERVICE;
    }

    /**
     * Get Category Name
     *
     * @return category name
     */
    @Override
    public String getCategory() {
        return this.CATEGORY;
    }
}
