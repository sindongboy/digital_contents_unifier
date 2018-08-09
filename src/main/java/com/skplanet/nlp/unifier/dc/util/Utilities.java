package com.skplanet.nlp.unifier.dc.util;

import com.skplanet.nlp.unifier.dc.config.Prop;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * User Modeling Utility Class
 * <br>
 * <br>
 * Created by Donghun Shin
 * <br>
 * Contact: donghun.shin@sk.com, sindongboy@gmail.com
 * <br>
 * Date: 10/29/13
 * <br>
 */
@SuppressWarnings("unused")
public final class Utilities {
    private static final Logger LOGGER = Logger.getLogger(Utilities.class.getName());

    // configurations
    private Config stopwordConfig;
    private Config stopwordPatternConfig;

    private List<String> stopwordList;
    private List<String> stopwordExceptList;
    private List<String> stopwordPatternList;
    private List<String> stopwordPatternExceptList;

    public static final int SORT_DESCENDING = 0;
    public static final int SORT_ASCENDING = 1;


    public Utilities() {
        this.stopwordList = new ArrayList<String>();
        this.stopwordExceptList = new ArrayList<String>();

        // ---------------------------------------------------------------- //
        // load stopword and stopword patter using typesafe configuration
        // ---------------------------------------------------------------- //
        // stopword dictionary loading
        this.stopwordConfig = ConfigFactory.load(Prop.STOPWORD_DICT_NAME);
        List<Config> subItems = new ArrayList<Config>(this.stopwordConfig.getConfigList("dict"));
        for (Config item : subItems) {
            List<String> subList;
            String configService = item.getString("service");
            String configCategory = item.getString("category");
            if ("common".equals(configService)) {
                // add stopword
                subList = item.getStringList("stopword");
                if (subList.isEmpty()) {
                    continue;
                }
                for (String stopword : subList) {
                    if (!this.stopwordList.contains(stopword)) {
                        this.stopwordList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() == 0) {
                    continue;
                }
                for (String stopword : subList) {
                    if (!this.stopwordExceptList.contains(stopword)) {
                        this.stopwordExceptList.add(stopword);
                    }
                }
            } else {
                LOGGER.debug("no common in the stopword dictionary");
            }
        }

        // stopword pattern dictionary loading
        this.stopwordPatternConfig = ConfigFactory.load(Prop.STOPWORD_PATTERN_DICT_NAME);
        this.stopwordPatternList = new ArrayList<String>();
        this.stopwordPatternExceptList = new ArrayList<String>();
        subItems = new ArrayList<Config>(this.stopwordPatternConfig.getConfigList("dict"));
        for (Config item : subItems) {
            List<String> subList;
            String configService = item.getString("service");
            String configCategory = item.getString("category");
            if ("common".equals(configService)) {
                // add stop pattern
                subList = item.getStringList("pattern");
                if (subList.size() == 0) {
                    continue;
                }
                for (String stopword : subList) {
                    if (this.stopwordPatternList.contains(stopword)) {
                        this.stopwordPatternList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() == 0) {
                    continue;
                }
                for (String stopword : subList) {
                    if (this.stopwordPatternExceptList.contains(stopword)) {
                        this.stopwordPatternExceptList.add(stopword);
                    }
                }
            } else {
                LOGGER.debug("no common defined in stop pattern dictionary");
            }
        }

    }

    /**
     * Utilities initialization for specific setting given by the service and category
     * @param service service name
     * @param category category name
     */
    public Utilities(String service, String category) {
        this.stopwordList = new ArrayList<String>();
        this.stopwordExceptList = new ArrayList<String>();

        // ---------------------------------------------------------------- //
        // load stopword and stopword patter using typesafe configuration
        // ---------------------------------------------------------------- //
        // stopword dictionary loading
        this.stopwordConfig = ConfigFactory.load(Prop.STOPWORD_DICT_NAME);
        List<Config> subItems = new ArrayList<Config>(this.stopwordConfig.getConfigList("dict"));
        for (Config item : subItems) {
            List<String> subList;
            String configService = item.getString("service");
            String configCategory = item.getString("category");
            if ("common".equals(configService)) {
                // add stopword
                subList = item.getStringList("stopword");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordExceptList.add(stopword);
                    }
                }
                continue;
            }

            if (service.equals(configService) && category.equals(configCategory)) {
                // add stopword
                subList = item.getStringList("stopword");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordExceptList.add(stopword);
                    }
                }
            }
        }

        // stopword pattern dictionary loading
        this.stopwordPatternConfig = ConfigFactory.load(Prop.STOPWORD_PATTERN_DICT_NAME);
        this.stopwordPatternList = new ArrayList<String>();
        this.stopwordPatternExceptList = new ArrayList<String>();
        subItems = new ArrayList<Config>(this.stopwordPatternConfig.getConfigList("dict"));
        for (Config item : subItems) {
            List<String> subList;
            String configService = item.getString("service");
            String configCategory = item.getString("category");
            if ("common".equals(configService)) {
                // add stop pattern
                subList = item.getStringList("pattern");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordPatternList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordPatternExceptList.add(stopword);
                    }
                }
                continue;
            }

            if (service.equals(configService) && category.equals(configCategory)) {
                // add stop pattern
                subList = item.getStringList("pattern");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordPatternList.add(stopword);
                    }
                }
                // add exception
                subList = item.getStringList("except");
                if (subList.size() > 0) {
                    for (String stopword : subList) {
                        this.stopwordPatternExceptList.add(stopword);
                    }
                }
            }
        }

    }

    /**
     * Pattern-based Title Normalization
     * @param orgTitle title text to be normalized
     * @return normalized title text
     */
    @Deprecated
    public String getNormalizedTitle(String orgTitle) {
        //return orgTitle.toLowerCase().replaceAll(TITLE_SYMBOL, "");
        return null;
    }

    /**
     * Pattern-based + Dictionary-based Normalization
     * @param orgTitle title text to be normalized
     * @return normalized title text
     */
    public String getNormalizedTitleWithStopword(String orgTitle) {
        if (this.stopwordExceptList.contains(orgTitle)
                || this.stopwordPatternExceptList.contains(orgTitle)) {
            return orgTitle.toLowerCase();
        }

        String newTitle = orgTitle.toLowerCase();
        // apply stopwords to the title
        for (String sw : this.stopwordList) {
            newTitle = newTitle.replace(sw, "");
        }

        for (String pattern : this.stopwordPatternList) {
            newTitle = newTitle.replaceAll(pattern, "");
        }

        return newTitle;
    }

    /**
     * Check if there exists overlap between two arrays
     * @param arrayA array 1
     * @param arrayB array 2
     * @return true if there exists overlap between two arrays
     */
    public static boolean arrayOverlapCheck(String[] arrayA, String[] arrayB) {
        for (String a : arrayA) {
            for (String b : arrayB) {
                if (a.contains(b) || b.contains(a)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if there exists overlap between two list
     * @param listA list 1
     * @param listB list 2
     * @return true if there exists overlap between two list
     */
    public static boolean ListOverlapCheck(List<String> listA, List<String> listB) {
        for (String a : listA) {
            for (String b : listB) {
                if (a.contains(b) || b.contains(a)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * string array to single line string
     * @param array string array to be converted
     * @param delim delimiter
     * @return string converted array
     */
    public static String arrayToString(String[] array, String delim) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            if (i == array.length - 1) {
                result += array[i];
                break;
            }
            result += array[i] + delim;
        }
        return result;
    }

    public static String arrayToString(List<String> array, String delim) {
        String result = "";
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).trim().length() == 0) {
                continue;
            }
            if (array.get(i).equals("null")) {
                continue;
            }
            if (i == array.size() - 1) {
                result += array.get(i);
                break;
            }
            result += array.get(i) + delim;
        }
        return result;
    }

    /**
     * string array to single line string
     * @param array string array to be converted
     * @param delim delimiter
     * @return string converted array
     */
    public static String listToString(List<String> array, String delim) {
        String result = "";
        for (int i = 0; i < array.size(); i++) {
            if (i == array.size() - 1) {
                result += array.get(i);
                break;
            }
            result += array.get(i) + delim;
        }
        return result;
    }

    public static boolean isEqualDates(Calendar[] a, Calendar[] b) {
        return false;
    }

    /**
     * Check if both Dates array has common.
     * @param a first date array
     * @param b second date array
     * @param field Calendar field
     * @return true if matched
     */
    public static boolean isEqualDates(Calendar[] a, Calendar[] b, int field) {
        for (Calendar ac : a) {
            for (Calendar bc : b) {
                if (ac.get(field) == bc.get(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get weighted score from multiple score based on the score participant
     * @param score scores
     * @param count number of participant for each score
     * @return weighted score
     */
    public static double weightedScore(double[] score, int[] count) {
        double result = -1.0;
        int countTotal = 0;
        double[] weights = new double[count.length];

        for (int c : count) {
            countTotal += c;
        }
        int wIndex = 0;
        for (int c : count) {
            weights[wIndex++] = (double) c / countTotal;
        }

        wIndex = 0;
        for (double s : score) { result += s * weights[wIndex++]; }
        return result;
    }

    public static double weightedScore(List<Double> score, List<Integer> count) {
        double result = 0.0;
        int countTotal = 0;
        double[] weights = new double[count.size()];

        for (int c : count) {
            countTotal += c;
        }

        if (countTotal == 0) {
            return 0.0;
        }

        int wIndex = 0;
        for (int c : count) {
            weights[wIndex++] = (double) c / countTotal;
        }

        wIndex = 0;
        for (double s : score) {
            result += s * weights[wIndex++];
        }
        return result;
    }


    /**
     * Sort the given {@link Map} by its value
     *
     * @param map map instance to be sorted
     * @param <K> key type
     * @param <V> value type
     * @return map object sorted by value
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final int order) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (order == SORT_DESCENDING) {
                    /* for descending order */
                    return (o1.getValue()).compareTo(o2.getValue()) * -1;
                } else {
                    /* for ascending order */
                    return (o1.getValue()).compareTo(o2.getValue());
                }
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

