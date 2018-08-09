package com.skplanet.nlp.unifier.dc.data.meta;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Meta Object
 *
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public abstract class AbsMeta implements Meta {
    private static final Logger LOGGER = Logger.getLogger(AbsMeta.class.getName());

    // abstract members
    protected String id;
    protected String title;
    protected String orgTitle;
    protected String date;
    protected String synopsis;
    protected String rate;
    protected List<String> genres;
    protected List<String> directors;
    protected List<String> actors;
    protected List<String> keywords;
    protected List<String> scores;
    protected List<String> scoreCounts;
    protected List<String> purchaseCount;
    protected List<String> nations;

    public AbsMeta(final Config item) {
        this.id = item.getString("id");
        this.title = item.getString("title");
        this.orgTitle = item.getString("org-title");
        this.date = item.getString("date");
        this.synopsis = item.getString("synopsis");
        this.rate = item.getString("rate");
        this.genres = item.getStringList("genres");
        this.directors = item.getStringList("directors");
        this.actors = item.getStringList("actors");
        List<String> tmpScores = item.getStringList("scores");
        this.scores = new ArrayList<String>();
        for (int i = 0; i < tmpScores.size(); i++) {
            if (tmpScores.get(i).equals("null")) {
                this.scores.add("0.00");
            } else {
                this.scores.add(tmpScores.get(i));
            }
        }
        List<String> tmpScoreCounts = item.getStringList("score-counts");
        this.scoreCounts = new ArrayList<String>();
        for (int i = 0; i < tmpScoreCounts.size(); i++) {
            if (tmpScoreCounts.get(i).equals("null")) {
                this.scoreCounts.add("0");
            } else {
                this.scoreCounts.add(tmpScoreCounts.get(i));
            }
        }

        this.purchaseCount = item.getStringList("purchase");
        this.nations = item.getStringList("nations");
    }

    /**
     * Get Content Id
     *
     * @return contents id
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Set Content Id
     *
     * @param id content id
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get Content Title
     *
     * @return content title
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Set Content Title
     *
     * @param title content title
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get Content Original Title
     *
     * @return content original title
     */
    @Override
    public String getOrgTitle() {
        return this.orgTitle;
    }

    /**
     * Set Content Original Title
     *
     * @param orgTitle content original title
     */
    @Override
    public void setOrgTitle(String orgTitle) {
        this.orgTitle = orgTitle;
    }

    /**
     * Get Date
     *
     * @return date
     */
    @Override
    public String getDate() {
        return this.date;
    }

    /**
     * Set Date
     *
     * @param date date
     */
    @Override
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get Synopsis
     *
     * @return synopsis
     */
    @Override
    public String getSynopsis() {
        return this.synopsis;
    }

    /**
     * Set Synopsis
     *
     * @param synopsis synopsis
     */
    @Override
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * Get Rate
     *
     * @return rate
     */
    @Override
    public String getRate() {
        return this.rate;
    }

    /**
     * Set Rate
     *
     * @param rate rate
     */
    @Override
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * Get Genres
     *
     * @return genre list
     */
    @Override
    public List<String> getGenres() {
        return this.genres;
    }

    /**
     * Set Genres
     *
     * @param genres genres
     */
    @Override
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    /**
     * Get Directors
     *
     * @return director list
     */
    @Override
    public List<String> getDirectors() {
        return this.directors;
    }

    /**
     * Set Directors
     *
     * @param directors directors
     */
    @Override
    public void setDirectors(List<String> directors) {
        this.directors = directors;
    }

    /**
     * Get Actors
     *
     * @return actor list
     */
    @Override
    public List<String> getActors() {
        return this.actors;
    }

    /**
     * Set Actors
     *
     * @param actors actors
     */
    @Override
    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    /**
     * Get Score
     *
     * @return score
     */
    @Override
    public List<String> getScore() {
        return this.scores;
    }

    /**
     * Set Score
     *
     * @param scores score
     */
    @Override
    public void setScore(List<String> scores) {
        this.scores = scores;
    }

    /**
     * Get Score Count
     *
     * @return score count
     */
    @Override
    public List<String> getScoreCount() {
        return this.scoreCounts;
    }

    /**
     * Set Score Count
     *
     * @param scoreCounts score count
     */
    @Override
    public void setScoreCount(List<String> scoreCounts) {
        this.scoreCounts = scoreCounts;
    }

    /**
     * Get Purchase Count
     *
     * @return purchase count
     */
    @Override
    public List<String> getPurchaseCount() {
        return this.purchaseCount;
    }

    /**
     * Set Purchase Count
     *
     * @param purchaseCount purchase count
     */
    @Override
    public void setPurchaseCount(List<String> purchaseCount) {
        this.purchaseCount = purchaseCount;
    }

    /**
     * Get Nations
     *
     * @return nation list
     */
    @Override
    public List<String> getNations() {
        return this.nations;
    }

    /**
     * Set Nations
     *
     * @param nations nations
     */
    @Override
    public void setNations(List<String> nations) {
        this.nations = nations;
    }

    public String toIndentedString(int indent) {

        StringBuffer sb = new StringBuffer();
        StringBuffer tab = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            tab.append("\t");
        }
        sb.append(tab).append("{\n");
        sb.append(tab).append("\tid = \"").append(this.id).append("\"\n");
        sb.append(tab).append("\ttitle = \"").append(this.title).append("\"\n");
        sb.append(tab).append("\torg-title = \"").append(this.orgTitle).append("\"\n");
        sb.append(tab).append("\tdate = \"").append(this.date).append("\"\n");
        sb.append(tab).append("\tsynopsis = \"").append(this.synopsis).append("\"\n");
        sb.append(tab).append("\trate = \"").append(this.rate).append("\"\n");
        sb.append(tab).append("\tgenres = [\n");
        for (String genre : this.genres) {
            sb.append(tab).append("\t\t\"").append(genre).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tdirectors = [\n");
        for (String director : this.directors) {
            sb.append(tab).append("\t\t\"").append(director).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tactors = [\n");
        for (String actor : this.actors) {
            sb.append(tab).append("\t\t\"").append(actor).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tscores = [\n");
        for (String score : this.scores) {
            sb.append(tab).append("\t\t\"").append(score).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tscore-counts = [\n");
        for (String scoreCount : this.scoreCounts) {
            sb.append(tab).append("\t\t\"").append(scoreCount).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("\tpurchase = ").append(this.purchaseCount).append("\n");
        sb.append(tab).append("\tnations = [\n");
        for (String nation : this.nations) {
            sb.append(tab).append("\t\t\"").append(nation).append("\",\n");
        }
        sb.append(tab).append("\t]\n");
        sb.append(tab).append("}");

        return sb.toString();
    }
}
