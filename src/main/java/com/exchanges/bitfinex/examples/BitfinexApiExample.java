package com.exchanges.bitfinex.examples;

import com.exchanges.bitfinex.BitfinexDataManager;
import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Tuple;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexCurrencyPair;
import com.github.jnidzwetzki.bitfinex.v2.entity.BitfinexTick;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;

import java.util.List;
import java.util.function.BiConsumer;

public class BitfinexApiExample {

    public static void main(String[] args) throws InterruptedException {
        BitfinexDataManager bitfinexDataManager = new BitfinexDataManager();

        // The consumer will be called on all received candles for the symbol
        final BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback = (sy, tick) -> {
            System.out.format("Got BitfinexTick (%s) for symbol (%s)\n", tick, sy);

        };

        bitfinexDataManager.subscribeCandleStick(BitfinexCurrencyPair.BTC_EUR);

        Thread.sleep(1000);

        bitfinexDataManager.unsubscribeCandleStick(BitfinexCurrencyPair.BTC_EUR);
        List<Tuple<Bid, Ask>> data = bitfinexDataManager.getData();

        data.stream().map(t -> t.x).forEach(b -> System.out.println("Bid == price -> " + b.getPrice()));
    }
}
