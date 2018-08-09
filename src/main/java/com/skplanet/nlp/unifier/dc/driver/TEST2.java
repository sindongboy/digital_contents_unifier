package com.skplanet.nlp.unifier.dc.driver;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 11/8/15
 */
public class TEST2 {

    // 감성 분석 결과 10일 단위로

    private static final String base = "/Users/sindongboy/Dropbox/Documents/workspace/naver-land-crawler/meta/ourtown";
    public static void main(String[] args) throws IOException {
        //File metaFile = new File(base + "/id2poi.tsv");
        File metaFile = new File(base + "/id2date.tsv");
        File filterFile = new File(base + "/filter");

        Set<String> filter = new HashSet();

        BufferedReader reader;
        String line;
        reader = new BufferedReader(new FileReader(filterFile));
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }
            filter.add(line.trim().split("\\t")[0]);
        }

        reader.close();


        Map<String, Integer> docCountMap = new HashMap<String, Integer>();
        reader = new BufferedReader(new FileReader(metaFile));
        Map<String, String> id2date = new HashMap<String, String>();
        MultiMap posCountMap = new MultiValueMap();
        MultiMap negCountMap = new MultiValueMap();
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }

            String[] fields = line.trim().split("\\t");

            if (fields.length != 2) {
                continue;
            }
            if (!filter.contains(fields[0])) {
                continue;
            }
            id2date.put(fields[0], fields[1]);

            if (docCountMap.containsKey(fields[1])) {
                int newCount = docCountMap.get(fields[1]) + 1;
                docCountMap.remove(fields[1]);
                docCountMap.put(fields[1], newCount);
            } else {
                docCountMap.put(fields[1], 1);
            }
        }
        reader.close();


        for (String id : id2date.keySet()) {
            File doc = new File(base + "/sentiments/" + id);

            posCountMap.put(id2date.get(id), getPos(new File(base + "/sentiments/" + id)));
            negCountMap.put(id2date.get(id), getNeg(new File(base + "/sentiments/" + id)));
        }

        int posTotal = 0;
        int negTotal = 0;
        Map<String, Integer> finalPosMap = new HashMap<String, Integer>();
        Map<String, Integer> finalNegMap = new HashMap<String, Integer>();
        for (Object date : posCountMap.keySet()) {
            for (Integer counts : (List<Integer>) posCountMap.get(date)) {
                posTotal += counts;
            }
            finalPosMap.put((String) date, posTotal);
            posTotal = 0;

            for (Integer counts : (List<Integer>) negCountMap.get(date)) {
                negTotal += counts;
            }
            finalNegMap.put((String) date, negTotal);
            negTotal = 0;
        }

        for (String date : finalPosMap.keySet()) {
            System.out.println(date + "\t" + docCountMap.get(date) + "\t" +
                    finalPosMap.get(date) + "\t" + finalNegMap.get(date) + "\t" + (finalPosMap.get(date) - finalNegMap.get(date)));
        }

    }

    static int getPos(File doc) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(doc));
        String line;
        int count=0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("SRP")) {
                count++;
            }
        }
        reader.close();

        return count;
    }

    static int getNeg(File doc) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(doc));
        String line;
        int count=0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("SRN")) {
                count++;
            }
        }
        reader.close();

        return count;
    }
}
