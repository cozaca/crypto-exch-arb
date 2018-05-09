package com.exchanges.kraken.MarketData;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Quotation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<Quotation> parse(String response) {
        final Matcher asksMatcher = asksPattern.matcher(response);
        final Matcher bidsMatcher = bidsPattern.matcher(response);

        List<Quotation> quotations = new ArrayList<>();
        quotations.addAll(convert(asksMatcher, values -> new Ask(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2]))));
        quotations.addAll(convert(bidsMatcher, values -> new Bid(parseBigDecimal(values[0]), parseBigDecimal(values[1]), parseLong(values[2]))));
        return quotations;
    }

    private static <T extends Quotation> List<Quotation> convert(Matcher asksMatcher, Function<String[], T> f) {
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
