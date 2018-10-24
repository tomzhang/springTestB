package com.jk51.modules.trades.domain;

public class TradePipeFactory {
    public static TradePipe create(String type) {
        switch (type) {
            case "end":
                return new TradeEndPipe();
        }

        throw new IllegalArgumentException("类型错误");
    }
}
