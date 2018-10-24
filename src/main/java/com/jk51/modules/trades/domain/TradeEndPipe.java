package com.jk51.modules.trades.domain;

import com.jk51.model.order.Trades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Administrator on 2017/6/1.
 */
public class TradeEndPipe implements TradePipe {
    List<Consumer> consumers = new ArrayList();

    @Override
    public void handler(List<Trades> trades) {
        consumers.stream().forEach(action -> action.accept(trades));
    }

    public void use(Consumer<List<Trades>> consumer) {
        consumers.add(consumer);
    }
}
