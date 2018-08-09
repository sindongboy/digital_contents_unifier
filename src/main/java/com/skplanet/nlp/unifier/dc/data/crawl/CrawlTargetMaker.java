package com.skplanet.nlp.unifier.dc.data.crawl;

import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.skplanet.nlp.unifier.dc.util.Utilities;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 수집 대상 데이타를 만들어 주는 클래스
 * 주어진 메타데이타의 제목을 통합 검색이 용이한 형태로 정규화함.
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/13/15
 */
public class CrawlTargetMaker {
    private static final Logger LOGGER = Logger.getLogger(CrawlTargetMaker.class.getName());

    // service list
    private List<String> serviceList = null;
    // meta list
    private List<Meta> metaList = null;
    // category
    private String category = null;
    // version
    private String version = null;

    private MetaLoader metaLoader = null;

    /**
     * Constructor
     */
    public CrawlTargetMaker() {

    }

    /**
     * Load meta data list given by the service name and category
     * @param service service list
     * @param category category
     * @param version version
     */
    public MultiMap request(List<String> service, String category, String version) {
        this.serviceList = service;
        this.category = category;
        this.version = version;

        // load meta collection
        this.metaList = new ArrayList<Meta>();
        for (String serviceName : this.serviceList) {
            this.metaLoader = new MetaLoader(serviceName, this.category, null, this.version);
            Collection<Meta> metaCollectionTemp = this.metaLoader.loadSourceMeta();
            for (Meta meta : metaCollectionTemp) {
                this.metaList.add(meta);
            }
        }

        // normalize and merge
        MultiMap normalizedMap = new MultiValueMap();
        for (Meta meta : this.metaList) {
            String normalizedTitle = meta.getTitle();
            Utilities util = new Utilities(meta.getService(), meta.getCategory());
            normalizedTitle = util.getNormalizedTitleWithStopword(normalizedTitle);
            if (normalizedTitle.length() == 0) {
                normalizedTitle = meta.getTitle();
            }
            normalizedMap.put(normalizedTitle, meta);
        }

        return normalizedMap;
    }

}
