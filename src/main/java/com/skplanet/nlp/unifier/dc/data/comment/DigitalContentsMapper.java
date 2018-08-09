package com.skplanet.nlp.unifier.dc.data.comment;

import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import org.apache.log4j.Logger;

/**
 * Digital Contents Mapper
 *
 * it loads unified meta already exist, and then unified newly added meta collections
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/7/15
 */
public class DigitalContentsMapper {
    private static final Logger LOGGER = Logger.getLogger(DigitalContentsMapper.class.getName());

    // service name
    private final String service;
    // category name
    private final String category;
    // version
    private final String version;

    // unified meta loader
    private MetaLoader unifiedMetaLoader = null;

    /**
     * Constructor
     * @param service service name
     * @param category category name
     * @param version version
     */
    public DigitalContentsMapper(
            final String service,
            final String category,
            final String version) {
        this.service = service;
        this.category = category;
        this.version = version;

        this.unifiedMetaLoader = new MetaLoader(
                this.service,
                this.category,
                null,
                this.version
        );
    }

}
