package com.skplanet.nlp.unifier.dc.data.episode;

import com.typesafe.config.Config;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Episode Object
 * category : Domestic TV Drama
 * source : skb ( http://www.skbroadband.com )
 * @author Donghun Shin / donghun.shin@sk.com
 * @date 3/18/15
 */
public class DramaDomesticEpisodeSKB extends AbsEpisode {
    private static final Logger LOGGER = Logger.getLogger(DramaDomesticEpisodeSKB.class.getName());

    private static final String SERVICE = "skb";
    private static final String CATEGORY = "dramak";
    /**
     * Value overriding constructor
     * @param item item information
     */
    public DramaDomesticEpisodeSKB(final Config item) {
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
        return SERVICE;
    }

    /**
     * Get Category
     *
     * @return category
     */
    @Override
    public String getCategory() {
        return CATEGORY;
    }

    /**
     * Sample Program
     * @param args no args
     */
    public static void main(String[] args) {

    }

}
