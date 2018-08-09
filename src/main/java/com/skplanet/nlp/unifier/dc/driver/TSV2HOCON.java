package com.skplanet.nlp.unifier.dc.driver;

import com.skplanet.nlp.cli.CommandLineInterface;
import com.skplanet.nlp.config.Configuration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * TSV 형식의 Meta Data 를 HOCON 형식으로 변환하는 드라이버
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 7/14/15
 */
public class TSV2HOCON {
    // logger
    private static Logger LOGGER = Logger.getLogger(TSV2HOCON.class.getName());

    static Config preprocessConfig = ConfigFactory.load("preprocess.conf");
    static Configuration fileResource = Configuration.getInstance();

    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.addOption("s", "svc", true, "service name", true);
        cli.addOption("u", "source", true, "data source name", true);
        cli.addOption("c", "category", true, "category name", true);
        cli.addOption("t", "type", true, "file type", true);
        cli.addOption("v", "version", true, "version", true);
        cli.parseOptions(args);

        String service = cli.getOption("s");
        String source = cli.getOption("u");
        String category = cli.getOption("c");
        String type = cli.getOption("t");
        String version = cli.getOption("v");

        String tsvName;
        if (service.equals(source)) {
            tsvName = service + "-" + category + "." + type + "-" + version;
        } else {
            tsvName = service + "-" + category + "-" + source + "." + type + "-" + version;
        }

        URL tsvFilePath = fileResource.getResource(tsvName);
        LOGGER.info("input TSV file: " + tsvName);
        if (tsvFilePath == null) {
            LOGGER.info("the given TSV formatted file doesn't exist: " + tsvName);
            System.exit(1);
        }

        File tsvFile = new File(tsvFilePath.getFile());
        String line;

        BufferedReader reader;
        if (type.equals("meta")) {
            Config metaFieldInfoConfig = preprocessConfig.getConfig("meta-field-info." + category);

            int idField = metaFieldInfoConfig.getConfig("id").getInt(source);
            int titleField = metaFieldInfoConfig.getConfig("title").getInt(source);
            int otitleField = metaFieldInfoConfig.getConfig("otitle").getInt(source);
            int dateField = metaFieldInfoConfig.getConfig("date").getInt(source);
            int synopField = metaFieldInfoConfig.getConfig("synopsis").getInt(source);
            int rateField = metaFieldInfoConfig.getConfig("rate").getInt(source);
            int genreField = metaFieldInfoConfig.getConfig("genres").getInt(source);
            int directorField = metaFieldInfoConfig.getConfig("directors").getInt(source);
            int actorField = metaFieldInfoConfig.getConfig("actors").getInt(source);
            int keywordField = metaFieldInfoConfig.getConfig("keywords").getInt(source);
            int scoreField = metaFieldInfoConfig.getConfig("scores").getInt(source);
            int scoreCountField = metaFieldInfoConfig.getConfig("score-counts").getInt(source);
            int purchaseField = metaFieldInfoConfig.getConfig("purchase").getInt(source);
            int nationField = metaFieldInfoConfig.getConfig("nations").getInt(source);


            StringBuffer output = new StringBuffer();
            output.append("service.name = \"" + service).append("\"\n");
            output.append("category.name = \"" + category).append("\"\n\n");
            output.append("meta = [").append("\n");
            try {
                reader = new BufferedReader(new FileReader(tsvFile));
                int count = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        continue;
                    }

                    if (count % 100 == 0) {
                        LOGGER.info("TSV 2 HOCON processing : " + count);
                    }

                    String[] fields = line.split("\\t");

                    String item;
                    item = getMetaItem(
                            fields,
                            idField,
                            titleField,
                            otitleField,
                            dateField,
                            synopField,
                            rateField,
                            genreField,
                            directorField,
                            actorField,
                            keywordField,
                            scoreField,
                            scoreCountField,
                            purchaseField,
                            nationField
                    );
                    output.append(item).append("\n");
                    count++;
                }
                output.append("]").append("\n");
                reader.close();
            } catch (FileNotFoundException e) {
                LOGGER.error("tsv file not found", e);
            } catch (IOException e) {
                LOGGER.error("failed to read tsv file", e);
            }
            System.out.println(output.toString());
        } else if (type.equals("episode")) {
            Config episodeFieldInfoConfig = preprocessConfig.getConfig("episode-field-info." + category);

            int idField = episodeFieldInfoConfig.getConfig("id").getInt(source);
            int titleField = episodeFieldInfoConfig.getConfig("title").getInt(source);
            int contentsField = episodeFieldInfoConfig.getConfig("contents").getInt(source);

            MultiMap episodeMap = new MultiValueMap();
            try {
                reader = new BufferedReader(new FileReader(tsvFile));
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        continue;
                    }

                    String[] fields = line.split("\\t");

                    String id = fields[idField - 1];
                    String title = fields[titleField - 1].replaceAll("[\':\"\\^]", "").replace("\\","");
                    String contents = fields[contentsField - 1].replaceAll("[\':\"\\^]", "").replace("\\", "");

                    episodeMap.put(id, title + "\t" + contents);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                LOGGER.error("tsv file not found", e);
            } catch (IOException e) {
                LOGGER.error("failed to read tsv file", e);
            }

            StringBuffer output = new StringBuffer();
            output.append("service.name = \"" + service).append("\"\n");
            output.append("category.name = \"" + category).append("\"\n\n");
            output.append("episode = [").append("\n");
            for (Object itemKey : episodeMap.keySet()) {
                output.append("\t{\n");
                output.append("\t\tid = \"").append((String) itemKey).append("\"\n");
                output.append("\t\tcontent-list = [\n");
                List<String> contentList = (ArrayList) episodeMap.get(itemKey);
                for (String contentItem : contentList) {
                    output.append("\t\t\t{\n");
                    String[] contentField = contentItem.split("\\t");
                    String title = contentField[0].replaceAll("[\':\"\\^]", "").replace("\\", "");
                    String content = contentField[1].replaceAll("[\':\"\\^]", "").replace("\\", "");
                    output.append("\t\t\t\ttitle = \"").append(title).append("\"\n");
                    output.append("\t\t\t\tcontent = \"").append(content).append("\"\n");
                    output.append("\t\t\t}\n");
                }
                output.append("\t\t]\n");
                output.append("\t}\n");
            }
            output.append("]").append("\n");
            System.out.println(output.toString());
        }
    }

    /**
     * Generate HOCON Type Meta Item
     * @param fields tab separated single line
     * @param idField pid
     * @param titleField title
     * @param oTitleField original title
     * @param dateField release date
     * @param synopsisField synopsis
     * @param rateField rate
     * @param genresField genre list
     * @param directorsField directors
     * @param actorsField actors
     * @param keywordsField keywords
     * @param scoresField scores
     * @param scoreCountsField score counts
     * @param purchaseField purchase counts
     * @param nationsField nations
     * @return Hocon type meta item
     */
    static String getMetaItem(
            String [] fields,
            int idField,
            int titleField,
            int oTitleField,
            int dateField,
            int synopsisField,
            int rateField,
            int genresField,
            int directorsField,
            int actorsField,
            int keywordsField,
            int scoresField,
            int scoreCountsField,
            int purchaseField,
            int nationsField
    ) {

        // req.
        String id = fields[idField - 1];
        // req.
        String title = fields[titleField - 1].replaceAll("[\':\"\\^]", "").replace("\\","");
        // not req.
        String otitle;
        if (oTitleField > 0) {
            otitle = fields[oTitleField - 1].replaceAll("[\':\"\\^]", "").replace("\\","");
        } else {
            otitle = "null";
        }
        // not req.
        String date;
        if (dateField > 0) {
            date = fields[dateField - 1];
        } else {
            date = "null";
        }
        // not req.
        String synopsis;
        if (synopsisField > 0) {
            synopsis = fields[synopsisField - 1].replaceAll("[\':\"\\^]", "").replace("\\","");
        } else {
            synopsis = "null";
        }
        // not req.
        String rate;
        if (rateField > 0) {
            rate = fields[rateField - 1];
        } else {
            rate = "null";
        }
        /// not req.
        String[] genres;
        if (genresField > 0) {
            genres = fields[genresField - 1].split("\\^");
        } else {
            genres = new String[]{"null"};
        }
        String[] directors;
        if (directorsField > 0) {
            directors = fields[directorsField - 1].replaceAll("\\\\&[a-z][a-z]*;", "").split("[\\^,]");
        } else {
            directors = new String[]{"null"};
        }
        String[] actors;
        if (actorsField > 0) {
            actors = fields[actorsField - 1].replaceAll("\\\\&[a-z][a-z]*;", "").split("[\\^,]");
        } else {
            actors = new String[]{"null"};
        }
        String[] keywords;
        if (keywordsField > 0) {
            keywords = fields[keywordsField - 1].replaceAll("\\\\&[a-z][a-z]*;", "").split("[\\^,]");
        } else {
            keywords = new String[]{"null"};
        }
        String[] scores;
        if (scoresField > 0) {
            scores = fields[scoresField - 1].split("\\^");
        } else {
            scores = new String[]{"null"};
        }
        String[] scoreCounts;
        if (scoreCountsField > 0) {
            scoreCounts = fields[scoreCountsField - 1].split("\\^");
        } else {
            scoreCounts = new String[]{"null"};
        }
        String[] purchase;
        if (purchaseField > 0) {
            purchase = fields[purchaseField - 1].split("\\^");
        } else {
            purchase = new String[]{"null"};
        }
        String[] nations;
        if (nationsField > 0) {
            nations = fields[nationsField - 1].split("\\^");
        } else {
            nations = new String[]{"null"};
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{").append("\n");
        sb.append("\tid = \"").append(id).append("\"\n");
        sb.append("\ttitle = \"").append(title).append("\"\n");
        sb.append("\torg-title = \"").append(otitle).append("\"\n");
        sb.append("\tdate = \"").append(date).append("\"\n");
        sb.append("\tsynopsis = \"").append(synopsis).append("\"\n");
        sb.append("\trate = \"").append(rate).append("\"\n");
        sb.append("\tgenres = [").append("\n");
        if (genres.length > 0) {
            for (String genre : genres) {
                sb.append("\t\t\"").append(genre).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tdirectors = [").append("\n");
        if (directors.length > 0) {
            for (String director : directors) {
                sb.append("\t\t\"").append(director).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tactors = [").append("\n");
        if (actors.length > 0) {
            for (String actor : actors) {
                sb.append("\t\t\"").append(actor).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tkeywords = [").append("\n");
        if (keywords.length > 0) {
            for (String keyword : keywords) {
                sb.append("\t\t\"").append(keyword).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tscores = [").append("\n");
        if (scores.length > 0) {
            for (String score : scores) {
                sb.append("\t\t\"").append(score).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tscore-counts = [").append("\n");
        if (scoreCounts.length > 0) {
            for (String scoreCount : scoreCounts) {
                sb.append("\t\t\"").append(scoreCount).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tpurchase = [").append("\n");
        if (purchase.length > 0) {
            for (String pur : purchase) {
                sb.append("\t\t\"").append(pur).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("\tnations = [").append("\n");
        if (nations.length > 0) {
            for (String nation : nations) {
                sb.append("\t\t\"").append(nation).append("\",\n");
            }
        }
        sb.append("\t]").append("\n");

        sb.append("}");
        return sb.toString();
    }
}
