package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * Meta Object
 * <p/>
 * Category: Animation
 * Source: Naver Movie ( http://movie.naver.com )
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class AnimationMetaNaver extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(AnimationMetaNaver.class.getName());

    public static final String SERVICE = "naver";
    public static final String CATEGORY = "animation";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public AnimationMetaNaver(final Config item) {
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
