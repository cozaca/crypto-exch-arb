package com.exchanges.kraken.api;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Tuple;

import java.util.*;

public class KrakenDataManager {

    private final KrakenApi krakenApi;

    public KrakenDataManager(KrakenApi krakenApi) {
        this.krakenApi = krakenApi;
    }

    public Tuple<Set<Bid>, Set<Ask>> subscribeToDepth(String currencPair) {
        return krakenApi.subscribeToDepth(currencPair);
    }

}
