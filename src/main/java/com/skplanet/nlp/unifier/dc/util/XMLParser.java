package com.skplanet.nlp.unifier.dc.util;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 4/10/15
 */
public class XMLParser {

    static final String IDFORMAT = "U%08d";
    // movie info
    static final String ID = "M_ID";
    static final String TITLE = "NAME";
    static final String OTITLE = "ORIGINAL";
    static final String SYNOPSIS = "SYNOPSIS";
    static final String STORY = "STORY";
    static final String GENRE = "GENRENAME";
    static final String RATE = "GRADE_NAME";
    static final String NATIONAL = "COUNTRY";
    static final String DATE = "OPENDATE";

    // movie score
    static final String SCORE = "MARK";
    static final String COUNT = "VOTE_CNT";

    // movie people
    static final String PEOPLE = "NAME";
    static final String PEOPLE_TYPE = "DIRECTOR_FLAG"; // either '감독' or '배우'

    static class Score {
        String id = null;
        double score = 0.0;
        int count = 0;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    static class People {
        String id = null;
        List<String> directorList = new ArrayList<String>();
        List<String> actorList = new ArrayList<String>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getDirectorList() {
            return directorList;
        }

        public void addDirector(String director) {
            if (director.trim().length() == 0) {
                this.directorList.add("null");
            } else {
                this.directorList.add(director);
            }
        }

        public void setDirectorList(List<String> directorList) {
            this.directorList = directorList;
        }

        public List<String> getActorList() {
            return actorList;
        }

        public void addActor(String actor) {
            if (actor.trim().length() == 0) {
                this.actorList.add("null");
            } else {
                this.actorList.add(actor);
            }
        }

        public void setActorList(List<String> actorList) {
            this.actorList = actorList;
        }
    }

    static class Info {
        String id = null;
        String title = null;
        String otitle = null;
        String synop = null;
        String story = null;
        String date = null;
        List<String> genreList = new ArrayList<String>();
        String rate = null;
        List<String> nationalList = new ArrayList<String>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOtitle() {
            return otitle;
        }

        public void setOtitle(String otitle) {
            this.otitle = otitle;
        }

        public String getSynop() {
            return synop;
        }

        public void setSynop(String synop) {
            this.synop = synop.replaceAll("[\n\t\r][\n\t\r]*", " ").replaceAll("\\t", " ").replaceAll("[ ][ ]*", " ");
        }

        public String getStory() {
            return story;
        }

        public void setStory(String story) {
            this.story = story.replaceAll("[\n\t\r][\n\t\r]*", " ").replaceAll("[ ][ ]*", " ");
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            if (date.trim().length() == 0) {
                this.date = "null";
            } else {
                this.date = date;
            }
        }

        public List<String> getGenreList() {
            return genreList;
        }

        public void addGenre(String genre) {
            if (genre.trim().length() == 0) {
                this.genreList.add("null");
            } else {
                this.genreList.add(genre);
            }
        }

        public void setGenreList(List<String> genreList) {
            this.genreList = genreList;
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public List<String> getNationalList() {
            return nationalList;
        }

        public void addNational(String nation) {
            if (nation.trim().length() == 0 || nation.trim().equals("-")) {
                this.nationalList.add("null");
            } else {
                this.nationalList.add(nation.trim());
            }
        }

        public void setNationalList(List<String> nationalList) {
            this.nationalList = nationalList;
        }
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {


        // Info loading
        System.out.println("info file loading ...");
        Map<String, Info> movieInfo = new HashMap<String, Info>();
        File file = new File(args[0]);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);

        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("ITEM");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            StringBuilder sb = new StringBuilder();
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Info info = new Info();
                Element eElement = (Element) nNode;
                info.setId(eElement.getElementsByTagName(ID).item(0).getTextContent());
                info.setTitle(eElement.getElementsByTagName(TITLE).item(0).getTextContent());
                info.setOtitle(eElement.getElementsByTagName(OTITLE).item(0).getTextContent());
                info.setStory(eElement.getElementsByTagName(STORY).item(0).getTextContent());
                info.setSynop(eElement.getElementsByTagName(SYNOPSIS).item(0).getTextContent());
                String genreList = eElement.getElementsByTagName(GENRE).item(0).getTextContent();
                for (String genre : genreList.split(", ")) {
                    info.addGenre(genre);
                }
                info.setDate(eElement.getElementsByTagName(DATE).item(0).getTextContent());
                info.setRate(eElement.getElementsByTagName(RATE).item(0).getTextContent());
                String nationList = eElement.getElementsByTagName(NATIONAL).item(0).getTextContent();
                for (String nation : nationList.replace(", ", ",").replace("/", ",").split(",")) {
                    info.addNational(nation);
                }

                movieInfo.put(info.getId(), info);
            }
        }
        System.out.println("done");

        // people loading
        System.out.println("people file loading ...");
        Map<String, People> peopleInfo = new HashMap<String, People>();
        file = new File(args[1]);

        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);

        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("ITEM");

        MultiMap directorMap = new MultiValueMap();
        MultiMap actorMap = new MultiValueMap();
        List<String> idList = new ArrayList<String>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String id = eElement.getElementsByTagName(ID).item(0).getTextContent();
                String name = eElement.getElementsByTagName(PEOPLE).item(0).getTextContent();
                String type = eElement.getElementsByTagName(PEOPLE_TYPE).item(0).getTextContent();
                idList.add(id);
                if ("감독".equals(type)) {
                    directorMap.put(id, name);
                } else {
                    actorMap.put(id, name);
                }
            }
        }

        for (String id : idList) {
            People people = new People();
            people.setId(id);
            List actorList = (List) actorMap.get(id);
            List directorList = (List) directorMap.get(id);
            if (actorList != null) {
                for (Object actor : actorList) {
                    people.addActor((String) actor);
                }
            }
            if (directorList != null) {
                for (Object director : directorList) {
                    people.addDirector((String) director);
                }
            }

            peopleInfo.put(people.getId(), people);
        }
        System.out.println("done");

        //score loading
        Map<String, Score> scoreInfo = new HashMap<String, Score>();
        file = new File(args[2]);
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(file);

        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("ITEM");

        System.out.println("parse score file ...");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Score score = new Score();
                Element eElement = (Element) nNode;
                score.setId(eElement.getElementsByTagName(ID).item(0).getTextContent());
                score.setScore(Double.parseDouble(eElement.getElementsByTagName(SCORE).item(0).getTextContent()));
                score.setCount(Integer.parseInt(eElement.getElementsByTagName(COUNT).item(0).getTextContent()));
                scoreInfo.put(score.getId(), score);
            }
        }
        System.out.println("done");

        Set<String> idSet = movieInfo.keySet();

        StringBuilder sb = new StringBuilder();
        for (String id : idSet) {

            // 1. id
            sb.append(id).append("\t");
            // 2. title
            sb.append(movieInfo.get(id).getTitle()).append("\t");
            // 3. otitle
            if (movieInfo.get(id).getOtitle().trim().length() == 0) {
                sb.append("null\t");
            } else {
                sb.append(movieInfo.get(id).getOtitle()).append("\t");
            }
            // 4. synopsis
            if (movieInfo.get(id).getSynop().trim().length() == 0) {
                sb.append("null\t");
            } else {
                sb.append(movieInfo.get(id).getSynop()).append("\t");
            }
            // 5. date
            sb.append(movieInfo.get(id).getDate()).append("\t");
            // 6. rate
            sb.append(movieInfo.get(id).getRate()).append("\t");
            // 7. genre
            if (movieInfo.get(id).getGenreList().size() == 0) {
                sb.append("null\t");
            } else {
                for (int i = 0; i < movieInfo.get(id).getGenreList().size(); i++) {
                    if (i == movieInfo.get(id).getGenreList().size() - 1) {
                        sb.append(movieInfo.get(id).getGenreList().get(i)).append("\t");
                    } else {
                        sb.append(movieInfo.get(id).getGenreList().get(i)).append("^");
                    }
                }
            }
            if (movieInfo.get(id).getTitle().equals("오리노블레스영화제-벨벳 골드마인")) {
                System.out.println();
            }
            // 8. director
            if (peopleInfo.containsKey(id)) {
                if (peopleInfo.get(id).getDirectorList().size() != 0) {
                    for (int i = 0; i < peopleInfo.get(id).getDirectorList().size(); i++) {
                        if (i == peopleInfo.get(id).getDirectorList().size() - 1) {
                            sb.append(peopleInfo.get(id).getDirectorList().get(i)).append("\t");
                        } else {
                            sb.append(peopleInfo.get(id).getDirectorList().get(i)).append("^");
                        }
                    }
                } else {
                    sb.append("null\t");
                }
            } else {
                sb.append("null\t");
            }


            // 9. actor
            if (peopleInfo.containsKey(id)) {
                if (peopleInfo.get(id).getActorList().size() != 0) {
                    for (int i = 0; i < peopleInfo.get(id).getActorList().size(); i++) {
                        if (i == peopleInfo.get(id).getActorList().size() - 1) {
                            sb.append(peopleInfo.get(id).getActorList().get(i)).append("\t");
                        } else {
                            sb.append(peopleInfo.get(id).getActorList().get(i)).append("^");
                        }
                    }
                } else {
                    sb.append("null\t");
                }
            } else {
                sb.append("null\t");
            }
            DecimalFormat df = new DecimalFormat("0.00");
            if (scoreInfo.containsKey(id)) {
                // 10. score
                sb.append(df.format(scoreInfo.get(id).getScore())).append("\t");
                // 11. score count
                sb.append(scoreInfo.get(id).getCount()).append("\t");
            } else {
                sb.append("null").append("\t");
                sb.append("null").append("\t");
            }

            // 12. nation
            for (int i = 0; i < movieInfo.get(id).getNationalList().size(); i++) {
                if (i == movieInfo.get(id).getNationalList().size() - 1) {
                    sb.append(movieInfo.get(id).getNationalList().get(i)).append("\n");
                } else {
                    sb.append(movieInfo.get(id).getNationalList().get(i)).append("^");
                }
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[3])));
        writer.write(sb.toString());
        writer.close();

        // add titles
        /*
        Utilities util = new Utilities(Prop.SERVICE_NAME_HOPPIN, Prop.CATEGORY_NAME_MOVIE);
        BufferedReader reader = new BufferedReader(new FileReader(new File(args[3])));
        String line;
        List<String> titleList = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }
            String[] fields = line.split("\\t");
            titleList.add(fields[1]);
        }
        reader.close();

        Map<String, Double> resultMap = new HashMap<String, Double>();
        for (int i = 0; i < titleList.size(); i++) {
            System.out.println(titleList.get(i));
            //String atitle = util.getNormalizedTitleWithStopword(titleList.get(i));
            String atitle = util.getNormalizedTitle(titleList.get(i));
            for (int j = 0; j < titleList.size(); j++) {
                if (i == j) {
                    continue;
                }
                //String btitle = util.getNormalizedTitleWithStopword(titleList.get(j));
                String btitle = util.getNormalizedTitle(titleList.get(j));
                resultMap.put(titleList.get(j), LCSequence.getLCSRatio(atitle, btitle));
                resultMap = MapUtil.sortByValue(resultMap, MapUtil.SORT_DESCENDING);
            }

            Set<String> keyset = resultMap.keySet();
            int count = 0;
            for (String k : keyset) {
                count++;
                if (count > 10) {
                    break;
                }
                System.out.println("\t" + resultMap.get(k) + "\t" + k + "\t(" + LCSequence.getLCS(atitle, k) + ")");

            }
        }*/
    }

}
