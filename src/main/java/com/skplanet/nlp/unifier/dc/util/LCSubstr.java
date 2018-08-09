package com.skplanet.nlp.unifier.dc.util;

import org.apache.log4j.Logger;

/**
 * Longest Common Substring Implementation
 * it supports following,
 * - get length of LCS between two string
 * - get common substring of LCS between two string
 * //-> get common substring ratio of LCS between two string
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/10/15
 */
public final class LCSubstr {
    private static final Logger LOGGER = Logger.getLogger(LCSubstr.class.getName());

    /**
     * Get Length of Longest Common Substring
     * @param first first string
     * @param second second string
     * @return length of common substring between strings
     */
    public static int getLCSLength(String first, String second) {
        if (first == null || second == null || first.length() == 0 || second.length() == 0) {
            return 0;
        }

        int maxLen = 0;
        int fl = first.length();
        int sl = second.length();
        int[][] table = new int[fl+1][sl+1];

        for(int s=0; s <= sl; s++)
            table[0][s] = 0;
        for(int f=0; f <= fl; f++)
            table[f][0] = 0;

        for (int i = 1; i <= fl; i++) {
            for (int j = 1; j <= sl; j++) {
                if (first.charAt(i-1) == second.charAt(j-1)) {
                    if (i == 1 || j == 1) {
                        table[i][j] = 1;
                    }
                    else {
                        table[i][j] = table[i - 1][j - 1] + 1;
                    }
                    if (table[i][j] > maxLen) {
                        maxLen = table[i][j];
                    }
                }
            }
        }
        return maxLen;
    }

    /**
     * Get Common Substring between two strings
     * @param first first string
     * @param second second string
     * @return common substring between two given strings
     */
    public static String getLCS(String first, String second) {
        int Start = 0;
        int Max = 0;
        for (int i = 0; i < first.length(); i++)
        {
            for (int j = 0; j < second.length(); j++)
            {
                int x = 0;
                while (first.charAt(i + x) == second.charAt(j + x))
                {
                    x++;
                    if (((i + x) >= first.length()) || ((j + x) >= second.length())) break;
                }
                if (x > Max)
                {
                    Max = x;
                    Start = i;
                }
            }
        }
        return first.substring(Start, (Start + Max));
    }

    /**
     * Get the ratio of common substring between two given strings
     * @param first first string
     * @param second second string
     * @return the ratio of common substring
     */
    public static double getLCSRatio(String first, String second) {
        int commonLen = getLCSLength(first, second);
        double maxLen = (double) Math.max(first.length(), second.length());
        return commonLen / maxLen;
    }

    /*
    public static void main(String[] args) {
        String first = "자하_하디드의_위대한_도전_(디지털)";
        String second = "반 고흐:_위대한_유산_(디지털)";
        System.out.println(LCSubstr.getLCS(first, second) + "\t" + LCSubstr.getLCSLength(first, second));

    }
    */
}
