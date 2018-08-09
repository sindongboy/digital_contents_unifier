package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class MovieMetaNaver extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(MovieMetaNaver.class.getName());

    private static final String SERVICE = "naver";
    private static final String CATEGORY = "movie";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public MovieMetaNaver(final Config item) {
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
