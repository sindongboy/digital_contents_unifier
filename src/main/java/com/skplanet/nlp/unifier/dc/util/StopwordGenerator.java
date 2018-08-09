package com.skplanet.nlp.unifier.dc.util;

import com.skplanet.nlp.unifier.dc.data.meta.Meta;
import com.skplanet.nlp.unifier.dc.data.meta.MetaLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Generates a set of stopword candidates
 *
 * it primarily ordered frequency of longest common substring and its ratio
 * refer to {@link LCSubstr}
 *
 * *** currently, Maxmovie Meta uses this logic for normalizing their content title ***
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/21/15
 */
public class StopwordGenerator {
    private static final Logger LOGGER = Logger.getLogger(StopwordGenerator.class.getName());

    private final String service;
    private final String category;
    private final String source;
    private final String version;

    private MetaLoader metaLoader = null;
    private Utilities util = null;

    private final Double COMMON_RATIO;

    /**
     * Constructor that initialize its service, category and version
     *
     * @param service service name
     * @param category category name
     * @param version version name
     */
    public StopwordGenerator(String service, String category, String source, String version) {
        Config config = ConfigFactory.load("main.conf");

        COMMON_RATIO = config.getDouble("util.common-substr-ratio");
        LOGGER.debug("minimum common substring ratio set to be " + COMMON_RATIO);

        // env.
        this.service = service;
        this.category = category;
        this.source = source;
        this.version = version;

        // meta loader
        this.metaLoader = new MetaLoader(
                this.service,
                this.category,
                this.source,
                this.version
        );

        // utilities
        this.util = new Utilities(this.service, this.category);
    }

    /**
     * Generate a set of stopword candidates
     * @return set of stopwords
     */
    public Set<String> generate() {

        List<Meta> metaCollection = (ArrayList<Meta>) metaLoader.loadSourceMeta();
        Map<String, Integer> subStringMap = new HashMap<String, Integer>();
        int count = 0;
        for (int i = 0; i < metaCollection.size(); i++) {
            if (count % 100 == 0) {
                LOGGER.info("generate stopword candidates: " + count);
            }
            count++;
            for (int j = 0; j < metaCollection.size(); j++) {
                if (i == j) {
                    continue;
                }

                String first = util.getNormalizedTitleWithStopword(metaCollection.get(i).getTitle());
                String second = util.getNormalizedTitleWithStopword(metaCollection.get(j).getTitle());

                double simSequence = LCSequence.getLCSRatio(
                        first,
                        second
                );

                double simSubstring = LCSubstr.getLCSRatio(
                        first,
                        second
                );

                if (simSubstring < COMMON_RATIO) {
                    continue;
                }

                if (simSequence <= simSubstring) {
                    continue;
                }

                String commonSequence = LCSequence.getLCS(
                        first,
                        second
                );
                String commonString = LCSubstr.getLCS(
                        first,
                        second
                );
                LOGGER.debug(first + " : " + second + " ( " + commonString + ":" + simSubstring + ", " + commonSequence + ":" + simSequence);

                if (subStringMap.containsKey(commonString)) {
                    int freq = subStringMap.get(commonString);
                    subStringMap.remove(commonString);
                    subStringMap.put(commonString, freq + 1);
                } else {
                    subStringMap.put(commonString, 1);
                }
            }
        }

       return Utilities.sortByValue(subStringMap, Utilities.SORT_DESCENDING).keySet();
    }
}
