package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.BitfinexConnectionFeature;
import com.github.jnidzwetzki.bitfinex.v2.SequenceNumberAuditor;
import com.github.jnidzwetzki.bitfinex.v2.entity.*;
import com.github.jnidzwetzki.bitfinex.v2.entity.symbol.BitfinexCandlestickSymbol;
import com.github.jnidzwetzki.bitfinex.v2.manager.ConnectionFeatureManager;
import com.github.jnidzwetzki.bitfinex.v2.manager.QuoteManager;

import java.util.function.BiConsumer;

public class BitfinexApi {

    private final BitfinexApiBroker connectionBroker;
    private final QuoteManager quoteManager;

    public BitfinexApi() {
        connectionBroker = BitFinexConnection.getConnection();
        connect();
        setBasicConfiguration(connectionBroker);
        quoteManager = connectionBroker.getQuoteManager();
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

    public void unsubsribeCandleStickBySymbol(BitfinexCandlestickSymbol symbol, BiConsumer<BitfinexCandlestickSymbol, BitfinexTick> callback){
        try {
            quoteManager.removeCandlestickCallback(symbol, callback);
        } catch (APIException e) {
            e.printStackTrace();
        }
        quoteManager.unsubscribeCandles(symbol);
    }


}
