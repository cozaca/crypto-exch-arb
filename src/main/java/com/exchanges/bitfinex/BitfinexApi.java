package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexConnectionFeature;
import com.github.jnidzwetzki.bitfinex.v2.SequenceNumberAuditor;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexTickerSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.ConnectionFeatureManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;

import java.util.function.BiConsumer;

public class BitfinexApi {

    private final BitfinexApiBroker connectionBroker;
    private final QuoteManager quoteManager;
    private final OrderbookManager orderbookManager;

    public BitfinexApi() {
        connectionBroker = BitFinexConnection.getConnection();
        connect();
        setBasicConfiguration(connectionBroker);
        quoteManager = connectionBroker.getQuoteManager();
        orderbookManager = connectionBroker.getOrderbookManager();
    }

    public void connect()  {
        try {
            connectionBroker.connect();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    public ConnectionCapabilities getCapabilities() {
        return connectionBroker.getCapabilities();
    }

    private void setBasicConfiguration(BitfinexApiBroker connectionBroker) {
        final ConnectionFeatureManager cfManager = connectionBroker.getConnectionFeatureManager();

        final SequenceNumberAuditor sequenceNumberAuditor = connectionBroker.getSequenceNumberAuditor();
        sequenceNumberAuditor.setErrorPolicy(SequenceNumberAuditor.ErrorPolicy.LOG_ONLY);

        cfManager.enableConnectionFeature(BitfinexConnectionFeature.SEQ_ALL);
    }

    public void subscribeCandleStickBySymbol(BitfinexCurrencyPair currencyPair, Timeframe timeframe) {
        final BitfinexCandlestickSymbol symbol
                = new BitfinexCandlestickSymbol(currencyPair, timeframe);

        // The consumer will be called on all received candles for the symbol
        final BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback = (sy, tick) -> {
            System.out.format("Got BitfinexTick (%s) for symbol (%s)\n", tick, sy);

        };

        try {
            quoteManager.registerCandlestickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.subscribeCandles(symbol);
    }

    public void subscribeTickerStreamBySymbol(BitfinexCurrencyPair currencyPair) {
        final BitfinexTickerSymbol symbol
                = new BitfinexTickerSymbol(currencyPair);

        // The consumer will be called on all received candles for the symbol
        final BiConsumer<BitfinexTickerSymbol, BitfinexTick> callback = (sy, tick) -> {
            System.out.format("Got BitfinexTickerStream (%s) for symbol (%s)\n", tick, sy);

        };

        try {
            quoteManager.registerTickCallback(symbol, callback);

        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.subscribeTicker(symbol);
    }


    public void subscribeOrderBookStreamBySymbol(BitfinexCurrencyPair currencyPair) {
        final OrderbookConfiguration  orderbookConfiguration
                = new OrderbookConfiguration(currencyPair, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        final BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("Got entry (%s) for orderbook (%s)\n", entry, orderbookConfig);
            
        };
        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);

        } catch (APIException e) {
            e.printStackTrace();
        }
        orderbookManager.subscribeOrderbook(orderbookConfiguration);
    }

    public void unsubscribeCandleStickBySymbol(BitfinexCandlestickSymbol symbol, BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback){
        try {
            quoteManager.removeCandlestickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.unsubscribeCandles(symbol);
    }

    public void unsubscribeStickStreamBySymbol(BitfinexTickerSymbol symbol, BiConsumer<BitfinexTickerSymbol, BitfinexTick> callback){
        try {
            quoteManager.removeTickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.unsubscribeTicker(symbol);
    }

    public void unsubscribeOrderBookStreamBySymbol(OrderbookConfiguration symbol, BiConsumer<OrderbookConfiguration, OrderbookEntry> callback){
        try {
            orderbookManager.registerOrderbookCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        orderbookManager.unsubscribeOrderbook(symbol);
    }


}
