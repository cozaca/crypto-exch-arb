package com.exchanges.kraken.parser;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Quotation;
import com.exchanges.common.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MarketDataParser {

    private static final String asksRegex = "(?<=\"asks\":\\[)(.*)(?=\\],\"bids\")";
    private static final String bidsRegex = "(?<=\"bids\":\\[)(.*)(?=\\]\\}}\\})";

    private static final Pattern asksPattern = Pattern.compile(asksRegex, Pattern.MULTILINE);
    private static final Pattern bidsPattern = Pattern.compile(bidsRegex, Pattern.MULTILINE);

    private static final Comparator<Bid> bidComparator = (bid1, bid2) -> {
        int priceComparison = bid1.getPrice().compareTo(bid2.getPrice());
        if (priceComparison < 0) {
            return -1;
        } else {
            return bid1.getTimeStamp().compareTo(bid2.getTimeStamp());
        }
    };

    private static final Comparator<Ask> askComparator = (ask1, ask2) -> {
        int priceComparison = ask1.getPrice().compareTo(ask2.getPrice());
        if (priceComparison < 0) {
            return -1;
        } else {
            return ask1.getTimeStamp().compareTo(ask2.getTimeStamp());
        }
    };

    public static Tuple<List<Bid>, List<Ask>> parse(String response) {
        final Matcher asksMatcher = asksPattern.matcher(response);
        final Matcher bidsMatcher = bidsPattern.matcher(response);

        List<Bid> bids = convert(bidsMatcher, values -> new Bid(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2])));
        List<Ask> asks = convert(asksMatcher, values -> new Ask(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2])));
        bids.sort(bidComparator);
        asks.sort(askComparator);
        return new Tuple(bids, asks);
    }

    private static <T extends Quotation> List<T> convert(Matcher asksMatcher, Function<String[], T> f) {
        while (asksMatcher.find()) {
            String asksRaw = asksMatcher.group(0).replace("],[", "];[");
            return Arrays.stream(asksRaw.split(";"))
                    .map(askRaw -> askRaw.replace("]", "").replace("[", ""))
                    .map(askRaw -> askRaw.split(","))
                    .map(values -> f.apply(values))
                    .collect(Collectors.toList());

        }
        return new ArrayList<>();
    }

    private static Long parseLong(String value) {
        return new Long(value.replaceAll("\\\"", ""));
    }

    private static BigDecimal parseBigDecimal(String value) {
        return new BigDecimal(value.replaceAll("\\\"", ""));
    }
}
