package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * Meta Object
 * <p/>
 * Category: Foreign TV Drama
 * Source: Hoppin Movie ( http://www.hoppin.nate.com )
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class DramaForeignMetaHoppin extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(DramaForeignMetaHoppin.class.getName());

    public static final String SERVICE = "hoppin";
    public static final String CATEGORY = "dramaf";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public DramaForeignMetaHoppin(final Config item) {
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
