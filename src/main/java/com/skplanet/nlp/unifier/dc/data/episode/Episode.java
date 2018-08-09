package com.skplanet.nlp.unifier.dc.data.episode;

import java.util.List;

/**
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/18/15
 */
public interface Episode {
    /**
     * Get ID
     * @return id
     */
    public String getId();

    /**
     * Get Service Name
     * @return service name
     */
    public String getService();

    /**
     * Get Category
     * @return category
     */
    public String getCategory();

    /**
     * Get Content
     * @return content
     */
    public List<AbsEpisode.Series> getContent();
}
