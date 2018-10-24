package com.jk51.modules.goods.event;

import com.jk51.model.Goods;
import org.springframework.context.ApplicationEvent;

public class GoodsEvent extends ApplicationEvent {
    private Goods goods;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public GoodsEvent(Object source) {
        super(source);
    }

    public GoodsEvent(Object source, Goods goods) {
        super(source);
        this.goods = goods;
    }

    public Goods getGoods() {
        return goods;
    }
}
