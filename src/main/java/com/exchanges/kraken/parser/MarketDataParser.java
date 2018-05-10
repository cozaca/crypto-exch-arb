package com.exchanges.kraken.parser;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Quotation;
import com.exchanges.common.Tuple;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MarketDataParser {

    private static final String asksRegex = "(?<=\"asks\":\\[)(.*)(?=\\],\"bids\")";
    private static final String bidsRegex = "(?<=\"bids\":\\[)(.*)(?=\\]\\}}\\})";

    private static final Pattern asksPattern = Pattern.compile(asksRegex, Pattern.MULTILINE);
    private static final Pattern bidsPattern = Pattern.compile(bidsRegex, Pattern.MULTILINE);

    public static Tuple<Set<Bid>, Set<Ask>> parse(String response) {
        Set<Bid> bids = new TreeSet<>(Bid.comparator);
        Set<Ask> asks = new TreeSet<>(Ask.comparator);

        bids.addAll(convert(bidsPattern.matcher(response), values -> new Bid(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2]))));
        asks.addAll(convert(asksPattern.matcher(response), values -> new Ask(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2]))));

        return new Tuple(bids, asks);
    }

    private static <T extends Quotation> Set<T> convert(Matcher asksMatcher, Function<String[], T> f) {
        while (asksMatcher.find()) {
            String asksRaw = asksMatcher.group(0).replace("],[", "];[");
            return Arrays.stream(asksRaw.split(";"))
                    .map(askRaw -> askRaw.replace("]", "").replace("[", ""))
                    .map(askRaw -> askRaw.split(","))
                    .map(values -> f.apply(values))
                    .collect(Collectors.toSet());

        }
        return new HashSet<>();
    }

    private static Long parseLong(String value) {
        return new Long(value.replaceAll("\\\"", ""));
    }

    private static BigDecimal parseBigDecimal(String value) {
        return new BigDecimal(value.replaceAll("\\\"", ""));
    }
}
