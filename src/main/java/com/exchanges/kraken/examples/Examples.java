package com.exchanges.kraken.examples;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Tuple;
import com.exchanges.kraken.api.KrakenApi;
import com.exchanges.kraken.api.KrakenApi.Method;
import com.exchanges.kraken.api.KrakenDataManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Examples {

    public static void main(String[] args) throws IOException {

        KrakenDataManager manager = new KrakenDataManager(new KrakenApi());

        String currencyPair = "XBTEUR";
        int i = 0;
        final Tuple<Set<Bid>, Set<Ask>> initialData = manager.subscribeToDepth(currencyPair);
        while (i < 100) {
            Tuple<Set<Bid>, Set<Ask>> data = manager.subscribeToDepth(currencyPair);
            Tuple<Set<Bid>, Set<Ask>> dataToModify = manager.subscribeToDepth(currencyPair);
            dataToModify.x.removeIf(bid -> initialData.x.contains(bid));
            dataToModify.y.removeIf(ask -> initialData.y.contains(ask));
            System.out.println("Found " + dataToModify.x.size() + " bid updates. " + dataToModify.x.toString());
            System.out.println("Found " + dataToModify.y.size() + " ask updates. " + dataToModify.y.toString());
            System.out.println();
            initialData.x.clear();
            initialData.x.addAll(data.x);
            initialData.y.clear();
            initialData.y.addAll(data.y);
            try {
                Thread.sleep(10);
            } catch (Exception e) {

            }

        }

        /*tuple.x.stream().forEach(
            q -> System.out.println(q)
        );
        System.out.println();
        tuple.y.stream().forEach(
            q -> System.out.println(q)
        );*/

    }
}
