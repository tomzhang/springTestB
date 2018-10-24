package com.jk51.model.coupon.tags;

import com.jk51.model.promotions.EasyToSeeConstants;

import java.util.*;

/**
 * Created by javen73 on 2018/3/27.
 */
public class TagsGoods {
    public String goodsId;
    public List<String> tags = new ArrayList<>();

    public BestTitle bestTitle = new BestTitle();

    // 商品标题显示  包邮、用券
    private Set<String> titles = new HashSet<String>();

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Set<String> getTitles() {
        return titles;
    }

    public void setTitles(Set<String> titles) {
        this.titles = titles;
    }

    public BestTitle getBestTitle() {
        return bestTitle;
    }

    public void setBestTitle(BestTitle bestTitle) {
        this.bestTitle = bestTitle;
    }

    public class BestTitle {
        private Integer bestType;
        private Integer bestPrice;

        public void setBestTitle(Integer bestType, Integer bestPrice) {
            if (Objects.nonNull(bestType) && Objects.nonNull(bestPrice)) {
                if (Objects.isNull(this.bestType) && Objects.isNull(this.bestPrice)) {
                    this.bestType = bestType;
                    this.bestPrice = bestPrice;
                } else {
                    //原来已有数据的情况下，把活动顺序拿出来，记住他们的下标，下标越小就越优先，如果相同，比较价格
                    int source_index = 0;
                    int dest_index = 0;
                    for (int i = 0; i < EasyToSeeConstants.BEST_ACTIVITY.length; i++) {
                        Integer type = EasyToSeeConstants.BEST_ACTIVITY[i];
                        if (this.bestType == type) {
                            source_index = i;
                        }
                        if (bestType == type) {
                            dest_index = i;
                        }
                    }
                    if (source_index == dest_index) {
                        if (this.bestPrice < bestPrice) {
                            this.bestType = bestType;
                            this.bestPrice = bestPrice;
                        }
                    } else if (source_index > dest_index) {
                        this.bestType = bestType;
                        this.bestPrice = bestPrice;
                    }
                }
            }
        }

        public void setBestTitle(BestTitle bestTitle) {
            Integer bestType = bestTitle.getBestType();
            Integer bestPrice = bestTitle.getBestPrice();
            if (!Objects.isNull(bestType) && !Objects.isNull(bestPrice)) {
                if (Objects.isNull(this.bestType) && Objects.isNull(this.bestPrice)) {
                    this.bestType = bestType;
                    this.bestPrice = bestPrice;
                } else {
                    //原来已有数据的情况下，把活动顺序拿出来，记住他们的下标，下标越小就越优先，如果相同，比较价格
                    int source_index = 0;
                    int dest_index = 0;
                    for (int i = 0; i < EasyToSeeConstants.BEST_ACTIVITY.length; i++) {
                        Integer type = EasyToSeeConstants.BEST_ACTIVITY[i];
                        if (this.bestType == type) {
                            source_index = i;
                        }
                        if (bestType == type) {
                            dest_index = i;
                        }
                    }
                    if (source_index == dest_index) {
                        if (this.bestPrice < this.bestPrice) {
                            this.bestType = bestType;
                            this.bestPrice = bestPrice;
                        }
                    } else if (source_index > dest_index) {
                        this.bestType = bestType;
                        this.bestPrice = bestPrice;
                    }
                }
            }

        }

        public Integer getBestType() {
            return bestType;
        }

        public void setBestType(Integer bestType) {
            this.bestType = bestType;
        }

        public Integer getBestPrice() {
            return bestPrice;
        }

        public void setBestPrice(Integer bestPrice) {
            this.bestPrice = bestPrice;
        }
    }
}
