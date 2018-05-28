package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexConnectionFeature;
import com.github.jnidzwetzki.bitfinex.v2.SequenceNumberAuditor;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexStreamSymbol;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexTickerSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.ConnectionFeatureManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.OrderbookManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.function.BiConsumer;

import static com.exchanges.bitfinex.SubscriptionType.*;

public class BitfinexApi {

    private final BitfinexApiBroker connectionBroker;
    private final QuoteManager quoteManager;
    private final OrderbookManager orderbookManager;
    private final Table<SubscriptionType, BitfinexCurrencyPair, BitfinexStreamSymbol> subscribedSymbols;

    public BitfinexApi() {
        connectionBroker = BitFinexConnection.getConnection();
        connect();
        setBasicConfiguration(connectionBroker);
        quoteManager = connectionBroker.getQuoteManager();
        orderbookManager = connectionBroker.getOrderbookManager();
        subscribedSymbols = HashBasedTable.create();
    }

    public void connect() {
        try {
            connectionBroker.connect();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    private void setBasicConfiguration(BitfinexApiBroker connectionBroker) {
        ConnectionFeatureManager cfManager = connectionBroker.getConnectionFeatureManager();

        SequenceNumberAuditor sequenceNumberAuditor = connectionBroker.getSequenceNumberAuditor();
        sequenceNumberAuditor.setErrorPolicy(SequenceNumberAuditor.ErrorPolicy.LOG_ONLY);

        cfManager.enableConnectionFeature(BitfinexConnectionFeature.SEQ_ALL);
    }

    public void subscribeCandleStickBySymbol(BitfinexCurrencyPair currencyPair, Timeframe timeframe, BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback) {
        BitfinexCandlestickSymbol symbol
                = new BitfinexCandlestickSymbol(currencyPair, timeframe);
        subscribedSymbols.put(CANDLE_STICK, currencyPair, symbol);
        try {
            quoteManager.registerCandlestickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.subscribeCandles(symbol);
    }

    public void subscribeTickerStreamBySymbol(BitfinexCurrencyPair currencyPair) {
        BitfinexTickerSymbol symbol
                = new BitfinexTickerSymbol(currencyPair);

        subscribedSymbols.put(TICKER_STREAM, currencyPair, symbol);
        // The consumer will be called on all received candles for the symbol
        BiConsumer<BitfinexTickerSymbol, BitfinexTick> callback = (sy, tick) -> {
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
        OrderbookConfiguration orderbookConfiguration
                = new OrderbookConfiguration(currencyPair, OrderBookPrecision.P0, OrderBookFrequency.F0, 25);

        subscribedSymbols.put(ORDER_BOOK_STREAM, currencyPair, orderbookConfiguration);
        BiConsumer<OrderbookConfiguration, OrderbookEntry> callback = (orderbookConfig, entry) -> {
            System.out.format("Got entry (%s) for orderbook (%s)\n", entry, orderbookConfig);

        };
        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);

        } catch (APIException e) {
            e.printStackTrace();
        }
        orderbookManager.subscribeOrderbook(orderbookConfiguration);
    }

    public void unsubscribeCandleStickBySymbol(BitfinexCurrencyPair currencyPair, Timeframe timeframe, BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback) {
        BitfinexCandlestickSymbol symbol = (BitfinexCandlestickSymbol) subscribedSymbols.get(CANDLE_STICK, currencyPair);
        try {
            quoteManager.removeCandlestickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.unsubscribeCandles(symbol);
    }

    public void unsubscribeStickStreamBySymbol(BitfinexCurrencyPair currencyPair, BiConsumer<BitfinexTickerSymbol, BitfinexTick> callback) {
        BitfinexTickerSymbol symbol = (BitfinexTickerSymbol) subscribedSymbols.get(TICKER_STREAM, currencyPair);
        try {
            quoteManager.removeTickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.unsubscribeTicker(symbol);
    }

    public void unsubscribeOrderBookStreamBySymbol(BitfinexCurrencyPair currencyPair, BiConsumer<OrderbookConfiguration, OrderbookEntry> callback) {
        OrderbookConfiguration orderbookConfiguration
                = (OrderbookConfiguration) subscribedSymbols.get(SubscriptionType.ORDER_BOOK_STREAM, currencyPair);

        try {
            orderbookManager.registerOrderbookCallback(orderbookConfiguration, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        orderbookManager.unsubscribeOrderbook(orderbookConfiguration);
    }


}
