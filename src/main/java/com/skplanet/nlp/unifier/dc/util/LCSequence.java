package com.skplanet.nlp.unifier.dc.util;

import org.apache.log4j.Logger;

/**
 * Longest Common Sequence Implementation
 * it supports following,
 * - get length of LCS between two string
 * - get string sequence of LCS between two string
 * - get common sequence ratio of LCS between two string
 *
 * @author donghun.shin@sk.com, sindongboy@gmail.com
 * @date 11/2/13
 */
@SuppressWarnings("unused")
public final class LCSequence {
    private static final Logger LOGGER = Logger.getLogger(LCSequence.class.getName());

    /**
     * Compute length of longest common sequence between both strings
     * @param inputA first string input
     * @param inputB second string input
     * @return length of longest common sequence
     */
    public static int getLCSLength(String inputA, String inputB) {

        String x = inputA.trim().toLowerCase();
        String y = inputB.trim().toLowerCase();

        int m = x.length();
        int n = y.length();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[m + 1][n + 1];

        // compute length of LCS
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        // recover LCS itself
        StringBuilder commonSequence = new StringBuilder();
        int length = 0;
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (x.charAt(i) == y.charAt(j)) {
                commonSequence.append(x.charAt(i));
                length++;
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {

                j++;
            }
        }
        //LOGGER.debug("longest common sequence : " + commonSequence.toString());
        return length;
    }

    /**
     * Get Common Subsequence between two given strings
     * @param first first string
     * @param second second string
     * @return common subsequence between two given strings
     */
    public static String getLCS(String first, String second) {
        String x = first.trim().toLowerCase();
        String y = second.trim().toLowerCase();

        int m = x.length();
        int n = y.length();

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[m + 1][n + 1];

        // compute length of LCS
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (x.charAt(i) == y.charAt(j)) {
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                } else {
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
                }
            }
        }

        // recover LCS itself
        StringBuilder commonSequence = new StringBuilder();
        int length = 0;
        int i = 0, j = 0;
        while (i < m && j < n) {
            if (x.charAt(i) == y.charAt(j)) {
                commonSequence.append(x.charAt(i));
                length++;
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                i++;
            } else {

                j++;
            }
        }

        return commonSequence.toString();
    }

    /**
     * Get the ratio of common subsequence between two given strings
     * @param first first string
     * @param second  second string
     * @return the ratio of common subsequence between two given strings
     */
    public static double getLCSRatio(String first, String second) {
        int commonLen = getLCSLength(first, second);
        double maxLen = (double) Math.max(first.length(), second.length());
        return commonLen / maxLen;
    }


    public static void main(String[] args) {
        Utilities util = new Utilities("hoppin", "dramaf");
        String a = "크리미널 마인드 10";
        String b = "크리미널 마인드 시즌9";

        System.out.println(util.getNormalizedTitleWithStopword(a));
        System.out.println(util.getNormalizedTitleWithStopword(b));
        System.out.println(LCSequence.getLCSRatio(
                util.getNormalizedTitleWithStopword(a),
                util.getNormalizedTitleWithStopword(b)));
    }

}
