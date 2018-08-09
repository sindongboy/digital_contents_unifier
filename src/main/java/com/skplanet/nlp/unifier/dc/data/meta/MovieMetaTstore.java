package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class MovieMetaTstore extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(MovieMetaTstore.class.getName());

    private static final String SERVICE = "tstore";
    private static final String CATEGORY = "movie";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public MovieMetaTstore(final Config item) {
        super(item);
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
}
