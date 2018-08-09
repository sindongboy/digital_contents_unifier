package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

/**
 * WARNING:SERVICE NOT YET DEFINED
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public class DramaDomesticMetaSKB extends AbsMeta {
    private static final Logger LOGGER = Logger.getLogger(DramaDomesticMetaSKB.class.getName());

    private static final String SERVICE = "skb";
    private static final String CATEGORY = "dramak";

    /**
     * Value Overriding Constructor
     *
     * @param item item to be overriding
     */
    public DramaDomesticMetaSKB(final Config item) {
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
