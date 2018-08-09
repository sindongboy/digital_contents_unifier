package com.skplanet.nlp.unifier.dc.data.meta;

import java.util.List;

/**
 * Meta Object Interfaces
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/17/15
 */
public interface Meta {
    /**
     * Get Service Name
     * @return service name
     */
    public String getService();

    /**
     * Get Category Name
     * @return category name
     */
    public String getCategory();

    /**
     * Get Content Id
     * @return contents id
     */
    public String getId();

    /**
     * Set Content Id
     * @param id content id
     */
    public void setId(String id);

    /**
     * Get Content Title
     * @return content title
     */
    public String getTitle();

    /**
     * Set Content Title
     * @param title content title
     */
    public void setTitle(String title);

    /**
     * Get Content Original Title
     * @return content original title
     */
    public String getOrgTitle();

    /**
     * Set Content Original Title
     * @param orgTitle content original title
     */
    public void setOrgTitle(String orgTitle);

    /**
     * Get Date
     * @return date
     */
    public String getDate();

    /**
     * Set Date
     * @param date date
     */
    public void setDate(String date);

    /**
     * Get Synopsis
     * @return synopsis
     */
    public String getSynopsis();

    /**
     * Set Synopsis
     * @param synopsis synopsis
     */
    public void setSynopsis(String synopsis);

    /**
     * Get Rate
     * @return rate
     */
    public String getRate();

    /**
     * Set Rate
     * @param rate rate
     */
    public void setRate(String rate);

    /**
     * Get Genres
     * @return genre list
     */
    public List<String> getGenres();

    /**
     * Set Genres
     * @param genres genres
     */
    public void setGenres(List<String> genres);

    /**
     * Get Directors
     * @return director list
     */
    public List<String> getDirectors();

    /**
     * Set Directors
     * @param directors directors
     */
    public void setDirectors(List<String> directors);

    /**
     * Get Actors
     * @return actor list
     */
    public List<String> getActors();

    /**
     * Set Actors
     * @param actors actors
     */
    public void setActors(List<String> actors);

    /**
     * Get Score
     * @return score
     */
    public List<String> getScore();

    /**
     * Set Score
     * @param score score
     */
    public void setScore(List<String> score);

    /**
     * Get Score Count
     * @return score count
     */
    public List<String> getScoreCount();

    /**
     * Set Score Count
     * @param scoreCount score count
     */
    public void setScoreCount(List<String> scoreCount);

    /**
     * Get Purchase Count
     * @return purchase count
     */
    public List<String> getPurchaseCount();

    /**
     * Set Purchase Count
     * @param purchaseCount purchase count
     */
    public void setPurchaseCount(List<String> purchaseCount);
    /**
     * Get Nations
     * @return nation list
     */
    public List<String> getNations();

    /**
     * Set Nations
     * @param nations nations
     */
    public void setNations(List<String> nations);

    /**
     * Extended toString Method
     * it specifies indentation
     * @param indent indent count
     * @return serialized object string
     */
    public String toIndentedString(int indent);
}
