package com.exchanges.bitfinex;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Tuple;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexTick;
import com.github.jnidzwetzki.bitfinex.v2.entity.Timeframe;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

public class BitfinexDataManager {

    private final BitfinexApi bitfinexApi;
    private final BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> dataHandler;
    private final CopyOnWriteArrayList<Tuple<Bid, Ask>> data;

    public BitfinexDataManager() {
        bitfinexApi = new BitfinexApi();
        dataHandler = getDataHandler();
        data = new CopyOnWriteArrayList<>();
    }

    public void subscribeCandleStick(BitfinexCurrencyPair currencyPair) {
        bitfinexApi.subscribeCandleStickBySymbol(currencyPair, Timeframe.MINUTES_1, dataHandler);
    }

    public void unsubscribeCandleStick(BitfinexCurrencyPair currencyPair) {
        bitfinexApi.unsubscribeCandleStickBySymbol(currencyPair, Timeframe.MINUTES_1, dataHandler);
    }

    public List<Tuple<Bid, Ask>> getData() {
        return data;
    }

    private BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> getDataHandler() {
        return (sy, tick) -> data.add(new Tuple<>(new Bid(tick.getLow(), tick.getVolume().get(), tick.getTimestamp()),
                new Ask(tick.getHigh(), tick.getVolume().get(), tick.getTimestamp())));
    }
}
