package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * Meta Object
 * <p/>
 * Category: Foreign TV Drama
 * Source: Naver Movie ( http://movie.naver.com )
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class DramaForeignMetaNaver extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(DramaForeignMetaNaver.class.getName());

    public static final String SERVICE = "naver";
    public static final String CATEGORY = "dramak";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public DramaForeignMetaNaver(final Config item) {
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
