package com.skplanet.nlp.unifier.dc.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 11/5/15
 */
public class Test {
    private static final String baseDir = "/Users/sindongboy/Dropbox/Documents/workspace/naver-land-crawler/meta/ourtown";
    private static final String sentDir = "/Users/sindongboy/Dropbox/Documents/workspace/naver-land-crawler/meta/ourtown/sentiments";

    public static void main(String[] args) throws IOException {

        String line;
        File metaFile = new File(baseDir + "/ourtown.meta");
        Map<String, TempMeta> metaMap = new HashMap<String, TempMeta>();
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(metaFile));
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }

            String[] fields = line.split("\\t");
            if (fields.length != 3) {
                continue;
            }

            TempMeta meta = new TempMeta(fields[0], fields[1], fields[2]);
            metaMap.put(fields[0], meta);
        }
        reader.close();
        System.out.println("meta loaded: " + metaMap.size());

        // date to meta
    }


    static class TempMeta {
        private String id;
        private String place;
        private String date;

        public TempMeta(String id, String place, String date) {
            this.id = id;
            this.place = place;
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
