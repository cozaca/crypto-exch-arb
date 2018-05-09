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

public class Examples {

    public static void main(String[] args) throws IOException {

        KrakenApi api = new KrakenApi();

        String response;
        Map<String, String> input = new HashMap<>();

        input.put("pair", "XBTEUR");
        response = api.queryPublic(Method.DEPTH, input);
        Tuple<List<Bid>, List<Ask>> tuple = MarketDataParser.parse(response);
        tuple.x.stream().forEach(
            q -> System.out.println(q)
        );
        tuple.y.stream().forEach(
            q -> System.out.println(q)
        );

    }
}
