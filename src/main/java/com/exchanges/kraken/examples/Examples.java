package com.exchanges.kraken.examples;

import com.exchanges.common.Quotation;
import com.exchanges.kraken.MarketData.MarketDataParser;
import com.exchanges.kraken.api.KrakenApi;
import com.exchanges.kraken.api.KrakenApi.Method;

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
        List<Quotation> result = MarketDataParser.parse(response);
        result.stream().forEach(
                q -> System.out.println(q)
        );
    }
}
