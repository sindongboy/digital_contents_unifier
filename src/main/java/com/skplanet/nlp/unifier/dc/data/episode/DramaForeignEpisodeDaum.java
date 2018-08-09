package com.skplanet.nlp.unifier.dc.data.episode;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Episode Object
 * category : Foreign TV Drama
 * source : Daum ( http://movie.daum.net )
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/18/15
 */
public class DramaForeignEpisodeDaum extends AbsEpisode {
    private static final Logger LOGGER = Logger.getLogger(DramaForeignEpisodeDaum.class.getName());

    private static final String SERVICE = "daum";
    private static final String CATEGORY = "dramaf";

    /**
     * Value overriding constructor
     * @param item item information
     */
    public DramaForeignEpisodeDaum(final Config item) {
        this.id = item.getString("id");
        if (this.content == null) {
            this.content = new ArrayList<Series>();
        }
        List<Config> subItemList = new ArrayList<Config>(item.getConfigList("content-list"));
        for (Config subItem : subItemList) {
            Series series = new Series();
            series.setSeriesName(subItem.getString("title"));
            series.setSeriesContents(subItem.getString("content"));
            this.content.add(series);
        }
    }


    /**
     * Get Service Name
     *
     * @return service name
     */
    @Override
    public String getService() {
        return null;
    }

    /**
     * Get Category
     *
     * @return category
     */
    @Override
    public String getCategory() {
        return null;
    }

    /**
     * Sample Program
     * @param args no args
     */
    public static void main(String[] args) {

    }
}
