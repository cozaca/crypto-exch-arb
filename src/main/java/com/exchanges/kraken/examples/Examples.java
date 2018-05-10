package com.exchanges.kraken.examples;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Tuple;
import com.exchanges.kraken.api.KrakenApi;
import com.exchanges.kraken.api.KrakenApi.Method;
import com.exchanges.kraken.parser.MarketDataParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Examples {

    public static void main(String[] args) throws IOException {

        KrakenApi api = new KrakenApi();

        String response;
        Map<String, String> input = new HashMap<>();

        input.put("pair", "XBTEUR");
        int i = 0;
        response = api.queryPublic(Method.DEPTH, input);
        final Tuple<Set<Bid>, Set<Ask>> initialData = MarketDataParser.parse(response);
        while (i < 100) {
            response = api.queryPublic(Method.DEPTH, input);
            Tuple<Set<Bid>, Set<Ask>> data = MarketDataParser.parse(response);
            Tuple<Set<Bid>, Set<Ask>> dataToModify = MarketDataParser.parse(response);
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
