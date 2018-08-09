package com.skplanet.nlp.unifier.dc.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Properties Definitions and Management
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public final class Prop {
    private static final Logger LOGGER = Logger.getLogger(Prop.class.getName());

    // CONFIG
    public static final Config rateConfig = ConfigFactory.load("rate.conf");
    public static final String MAIN_CONFIG_NAME = "main.conf";
    public static final String HADOOP_CONFIG_NAME = "dcu-hadoop.conf";
    public static final String UNIFICATION_CONFIG_NAME = "unification.conf";

    // FILE NAMES
    public static final String STOPWORD_DICT_NAME = "stopword.conf";
    public static final String STOPWORD_PATTERN_DICT_NAME = "stopword-pattern.conf";

    // DATA TYPE
    public static final String SOURCE_META_EXT = "meta";
    public static final String SOURCE_EPISODE_EXT = "episode";
    public static final String SOURCE_COMMENT_EXT = "comment";
    public static final String UNIFIED_META_EXT = "umeta";
    public static final String UNIFIED_EPISODE_EXT = "uepisode";
    public static final String UNIFIED_COMMENT_EXT = "ucomment";

    // SERVICE NAME
    public static final String SERVICE_NAME_HOPPIN = "hoppin";
    public static final String SERVICE_CODE_HOPPIN = "HP";

    public static final String SERVICE_NAME_TSTORE = "tstore";
    public static final String SERVICE_CODE_TSTORE = "TS";

    public static final String SERVICE_NAME_XLIFE = "xlife";
    public static final String SERVICE_CODE_XLIFE = "XL";

    public static final String SERVICE_NAME_MAXMOVIE = "maxmovie";
    public static final String SERVICE_CODE_MAXMOVIE = "MX";

    public static final String SERVICE_NAME_SKB = "skb";
    public static final String SERVICE_CODE_SKB = "SB";

    // DATA SOURCE NAME
    // for Hoppin
    public static final String DATA_SOURCE_NAME_HOPPIN = "hoppin";
    public static final String DATA_SOURCE_CODE_HOPPIN = "HP";
    // for Daum
    public static final String DATA_SOURCE_NAME_DAUM = "daum";
    public static final String DATA_SOURCE_CODE_DAUM = "DM";
    // for naver
    public static final String DATA_SOURCE_NAME_NAVER = "naver";
    public static final String DATA_SOURCE_CODE_NAVER = "NV";
    // for tstore
    public static final String DATA_SOURCE_NAME_TSTORE = "tstore";
    public static final String DATA_SOURCE_CODE_TSTORE = "TS";
    // for kmdb
    public static final String DATA_SOURCE_NAME_KMDB = "kmdb";
    public static final String DATA_SOURCE_CODE_KMDB = "KM";
    // maxmovie
    public static final String DATA_SOURCE_NAME_MAXMOVIE = "maxmovie";
    public static final String DATA_SOURCE_CODE_MAXMOVIE = "MX";
    // xlife
    public static final String DATA_SOURCE_NAME_XLIFE = "xlife";
    public static final String DATA_SOURCE_CODE_XLIFE = "XL";
    // skb
    public static final String DATA_SOURCE_NAME_SKB = "skb";
    public static final String DATA_SOURCE_CODE_SKB = "SB";


    // CATEGORY NAME
    public static final String CATEGORY_NAME_ANIMATION = "animation";
    public static final String CATEGORY_NAME_DRAMAK = "dramak";
    public static final String CATEGORY_NAME_DRAMAF = "dramaf";
    public static final String CATEGORY_NAME_MOVIE = "movie";

    public static final String CATEGORY_CODE_ANIMATION = "AN";
    public static final String CATEGORY_CODE_DRAMAK = "DK";
    public static final String CATEGORY_CODE_DRAMAF = "DF";
    public static final String CATEGORY_CODE_MOVIE = "MV";


    // HDFS
    public static final String UNIFIED_META_ANIMATION_HDFS_PATH = "unified-meta-animation-hdfs-path";
    public static final String UNIFIED_META_DRAMAK_HDFS_PATH = "unified-meta-dramak-hdfs-path";
    public static final String UNIFIED_META_DRAMAF_HDFS_PATH = "unified-meta-dramaf-hdfs-path";
    public static final String UNIFIED_META_MOVIE_HDFS_PATH = "unified-meta-movie-hdfs-path";

    /**
     * Get Service Code from Service Name
     * @param serviceName service name
     * @return service code
     */
    public static String getServiceCodeFromServiceName(String serviceName) {
        if (serviceName.equals(SERVICE_NAME_HOPPIN)) {
            return SERVICE_CODE_HOPPIN;
        } else if (serviceName.equals(SERVICE_NAME_TSTORE)) {
            return SERVICE_CODE_TSTORE;
        } else if (serviceName.equals(SERVICE_NAME_XLIFE)) {
            return SERVICE_CODE_XLIFE;
        } else if (serviceName.equals(SERVICE_NAME_MAXMOVIE)) {
            return SERVICE_CODE_MAXMOVIE;
        } else if (serviceName.equals(SERVICE_NAME_SKB)) {
            return SERVICE_CODE_SKB;
        } else {
            return null;
        }
    }

    /**
     * Get Service Name from Service Code
     * @param serviceCode service code
     * @return service name
     */
    public static String getServiceNameFromServiceCode(String serviceCode) {
        if (serviceCode.equals(SERVICE_CODE_HOPPIN)) {
            return SERVICE_NAME_HOPPIN;
        } else if (serviceCode.equals(SERVICE_CODE_TSTORE)) {
            return SERVICE_NAME_TSTORE;
        } else if (serviceCode.equals(SERVICE_CODE_XLIFE)) {
            return SERVICE_NAME_XLIFE;
        } else if (serviceCode.equals(SERVICE_CODE_MAXMOVIE)) {
            return SERVICE_NAME_MAXMOVIE;
        } else if (serviceCode.equals(SERVICE_CODE_SKB)) {
            return SERVICE_NAME_SKB;
        } else {
            return null;
        }
    }

    /**
     * Get Data Source Code from Data Source Name
     * @param dataSourceName data source name
     * @return data source code
     */
    public static String getDataSourceCodeFromDataSourceName(String dataSourceName) {
        if (dataSourceName.equals(DATA_SOURCE_NAME_HOPPIN)) {
            return DATA_SOURCE_CODE_HOPPIN;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_TSTORE)) {
            return DATA_SOURCE_CODE_TSTORE;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_NAVER)) {
            return DATA_SOURCE_CODE_NAVER;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_DAUM)) {
            return DATA_SOURCE_CODE_DAUM;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_XLIFE)) {
            return DATA_SOURCE_CODE_XLIFE;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_MAXMOVIE)) {
            return DATA_SOURCE_CODE_MAXMOVIE;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_KMDB)) {
            return DATA_SOURCE_CODE_KMDB;
        } else if (dataSourceName.equals(DATA_SOURCE_NAME_SKB)) {
            return DATA_SOURCE_CODE_SKB;
        } else {
            return null;
        }
    }

    /**
     * Get Category Code from Category Name
     * @param categoryName category name
     * @return category code
     */
    public static String getCategoryCodeFromCategoryName(String categoryName) {
        if (categoryName.equals(CATEGORY_NAME_ANIMATION)) {
            return CATEGORY_CODE_ANIMATION;
        } else if (categoryName.equals(CATEGORY_NAME_DRAMAK)) {
            return CATEGORY_CODE_DRAMAK;
        } else if (categoryName.equals(CATEGORY_NAME_DRAMAF)) {
            return CATEGORY_CODE_DRAMAF;
        } else if (categoryName.equals(CATEGORY_NAME_MOVIE)) {
            return CATEGORY_CODE_MOVIE;
        } else {
            return null;
        }
    }

    /**
     * Get Category Name from Category Code
     * @param categoryCode category code
     * @return category code
     */
    public static String getCategoryNameFromCategoryCode(String categoryCode) {
        if (categoryCode.equals(CATEGORY_CODE_ANIMATION)) {
            return CATEGORY_NAME_ANIMATION;
        } else if (categoryCode.equals(CATEGORY_CODE_DRAMAK)) {
            return CATEGORY_NAME_DRAMAK;
        } else if (categoryCode.equals(CATEGORY_CODE_DRAMAF)) {
            return CATEGORY_NAME_DRAMAF;
        } else if (categoryCode.equals(CATEGORY_CODE_MOVIE)) {
            return CATEGORY_NAME_MOVIE;
        } else {
            return null;
        }
    }

    /**
     * Get Standard Rate code name
     * @param rateName rate code name to be looked up
     * @return standard rate code
     */
    public static String getStandardRateName(String rateName) {
        Map<String, String> repMap = new HashMap<String, String>();
        List<Config> items = new ArrayList<Config>(rateConfig.getConfigList("rate"));
        for (Config item : items) {
            List<String> expList = item.getStringList("set");
            String rep = item.getString("rep");
            for (String exp : expList) {
                repMap.put(exp, rep);
            }
        }

        return repMap.get(rateName);
    }

}
