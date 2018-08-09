package com.skplanet.nlp.unifier.dc.data.comment;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.unifier.dc.config.Prop;
import com.typesafe.config.Config;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Comments/Review Container
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 5/6/15
 */
public class Comments {
    private static final Logger LOGGER = Logger.getLogger(Comments.class.getName());

    // service name
    private final String service;
    // category name
    private final String category;
    // data source name
    private final String source;
    // version
    private final String version;


    private MultiMap commentsMap = null;

    /**
     * Constructor for Comments
     * @param service service name
     * @param category category name
     * @param source data source name
     */
    public Comments(final String service, final String category, final String source, final String version) {
        this.service = service;
        this.category = category;
        this.source = source;
        this.version = version;
    }

    /**
     * Load comments using serialized data format (HOCON)
     * @param config config item
     */
    public void load(final Config config) {
        /*
        String commentFileName = this.service + "-" + this.category + "-" + this.source + ".comment-" + this.version;
        config = ConfigFactory.load(commentFileName);
        */
        List<Config> subItems = new ArrayList<Config>(config.getConfigList(Prop.SOURCE_COMMENT_EXT));

        long begin = System.currentTimeMillis();
        int count = 0;
        for (Config item : subItems) {
            if (count % 100 == 0) {
                LOGGER.info("comment loading: " + count);
            }
            count++;
            String id = item.getString("id");
            List<String> commentList = item.getStringList("content");
            for (String comment : commentList) {
                this.commentsMap.put(id, comment);
            }
        }
        long end = System.currentTimeMillis();
        LOGGER.info(
                this.service + "-" +
                        this.category + "-" +
                        this.source +
                        " comments loaded (" + this.commentsMap.size() + ") in " +
                        (end - begin) + " msec."
        );
    }

    /**
     * Load comments file
     */
    public int load() {
        Configuration config = Configuration.getInstance();
        String commentFileName = this.service + "-" + this.category + "-" + this.source + "-comment-" + this.version + ".conf";
        if (config.getResource(commentFileName) == null) {
            return -1;
        }
        File commentFile = new File(config.getResource(commentFileName).getFile());

        this.commentsMap = new MultiValueMap();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(commentFile));
            String line;
            int count = 0;
            long begin, end;
            begin = System.currentTimeMillis();
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (count % 100 == 0) {
                    LOGGER.debug("comment loading : " + count);
                }
                count++;
                String[] fields = line.split("\\t");

                if (fields.length < 2) {
                    LOGGER.warn("comment field number mismatch: " + fields.length + " : " + line);
                    continue;
                }
                String id = fields[0];
                String comment = fields[1];

                this.commentsMap.put(id, comment);
            }
            reader.close();
            end = System.currentTimeMillis();
            LOGGER.info(
                    this.service + "-"  +
                            this.category + "-" +
                            this.source +
                            " comments loaded (" + this.commentsMap.size() + "), in " + (end - begin) + " msec.");
        } catch (FileNotFoundException e) {
            LOGGER.error("comment file not found: " + commentFileName, e);
        } catch (IOException e) {
            LOGGER.error("failed to read comment file: " + commentFileName, e);
        }

        return 0;
    }

    /**
     * Get Comments for the given id
     * @param id id for comments
     * @return comments list
     */
    public List<String> getComments(String id) {
        if (!this.commentsMap.containsKey(id)) {
            LOGGER.debug("comments doesn't exist for id : " + id);
            return null;
        }

        return (ArrayList<String>) this.commentsMap.get(id);
    }

    /**
     * Get Id List
     * @return id list
     */
    public Set getIdList() {
        return this.commentsMap.keySet();
    }

    @Override
    public String toString() {

        if (this.commentsMap == null) {
            return null;
        }

        if (this.commentsMap.size() == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        sb.append("service.name = " + this.service).append("\n");
        sb.append("category.name = " + this.category).append("\n");

        sb.append("comment = [").append("\n");

        for (Object id : this.commentsMap.keySet()) {
            sb.append("\t{\n");
            sb.append("\t\t").append("id = \"").append(id).append("\"\n");
            sb.append("\t\tcontent = [\n");
            List<String> commentList = (List<String>) this.commentsMap.get(id);
            for (String comment : commentList) {
                sb.append("\t\t\t\"").append(comment.replaceAll("\"", "\'")).append("\",\n");
            }
            sb.append("\t\t]\n");
            sb.append("\t}\n");
        }

        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        Comments comments = new Comments("hoppin", "dramak", "daum", "20150505");
        /*
        String commentFileName = "hoppin" + "-" + "animation" + "-" + "daum" + ".comment-" + "20150505";
        Config config = ConfigFactory.load(commentFileName);
        */
        comments.load();

        System.out.println(comments.toString());

    }
}
