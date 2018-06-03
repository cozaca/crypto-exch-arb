package com.exchanges.kraken.api;

import com.exchanges.common.Ask;
import com.exchanges.common.Bid;
import com.exchanges.common.Quotation;
import com.exchanges.common.Tuple;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KrakenApi {

    private static final String OTP = "otp";
    private static final String NONCE = "nonce";
    private static final String MICRO_SECONDS = "000";

    private static final String asksRegex = "(?<=\"asks\":\\[)(.*)(?=\\],\"bids\")";
    private static final String bidsRegex = "(?<=\"bids\":\\[)(.*)(?=\\]\\}}\\})";

    private static final Pattern asksPattern = Pattern.compile(asksRegex, Pattern.MULTILINE);
    private static final Pattern bidsPattern = Pattern.compile(bidsRegex, Pattern.MULTILINE);

    private static final Map<String, String> parameters = new HashMap<>();

    /** The API key. */
    private String key;

    /** The API secret. */
    private String secret;

    /**
     * Query a public method of the API with the given parameters.
     *
     * @param method the API method
     * @param parameters the method parameters
     * @return the API response
     * @throws IllegalArgumentException if the API method is null
     * @throws IOException if the request could not be created or executed
     */
    public String queryPublic(Method method, Map<String, String> parameters) throws IOException {

        ApiRequest request = new ApiRequest();
        request.setMethod(method);

        if (parameters != null) {
            request.setParameters(parameters);
        }

        return request.execute();
    }

    /**
     * Query a public method of the API without any parameters.
     *
     * @param method the public API method
     * @return the API response
     * @throws IOException if the request could not be created or executed
     */
    public String queryPublic(Method method) throws IOException {
        return queryPublic(method, null);
    }

    /**
     * Query a private method of the API with the given parameters.
     *
     * @param method the private API method
     * @param otp the one-time password
     * @param parameters the method parameters
     * @return the API response
     * @throws IOException if the request could not be created or executed
     * @throws NoSuchAlgorithmException if the SHA-256 or HmacSha512 algorithm
     *         could not be found
     * @throws InvalidKeyException if the HMAC key is invalid
     */
    public String queryPrivate(Method method, String otp, Map<String, String> parameters) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        ApiRequest request = new ApiRequest();
        request.setKey(key);

        // clone parameter map
        parameters = parameters == null ? new HashMap<>() : new HashMap<>(parameters);

        // set OTP parameter
        if (otp != null) {
            parameters.put(OTP, otp);
        }

        // generate nonce
        String nonce = String.valueOf(System.currentTimeMillis()) + MICRO_SECONDS;
        parameters.put(NONCE, nonce);

        // set the parameters and retrieve the POST data
        String postData = request.setParameters(parameters);

        // create SHA-256 hash of the nonce and the POST data
        byte[] sha256 = KrakenUtils.sha256(nonce + postData);

        // set the API method and retrieve the path
        byte[] path = KrakenUtils.stringToBytes(request.setMethod(method));

        // decode the API secret, it's the HMAC key
        byte[] hmacKey = KrakenUtils.base64Decode(secret);

        // create the HMAC message from the path and the previous hash
        byte[] hmacMessage = KrakenUtils.concatArrays(path, sha256);

        // create the HMAC-SHA512 digest, encode it and set it as the request signature
        String hmacDigest = KrakenUtils.base64Encode(KrakenUtils.hmacSha512(hmacKey, hmacMessage));
        request.setSignature(hmacDigest);

        return request.execute();
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public String queryPrivate(Method method) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, null, null);
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public String queryPrivate(Method method, String otp) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, otp, null);
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public String queryPrivate(Method method, Map<String, String> parameters) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, null, parameters);
    }

    /**
     * Sets the API key.
     *
     * @param key the API key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Sets the API secret.
     *
     * @param secret the API secret
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Tuple<Set<Bid>, Set<Ask>> subscribeToDepth(String currencPair) {
        parameters.put("pair", currencPair);
        String rawMarketData;
        try {
            rawMarketData = queryPublic(Method.DEPTH, new HashMap<>());
        } catch (IOException e) {
            rawMarketData = "";
        }
        parameters.clear();;
        return parse(rawMarketData);
    }


    private static Tuple<Set<Bid>, Set<Ask>> parse(String response) {
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

    /**
     * Represents an API method.
     *
     * @author nyg
     */
    public enum Method {

        /* Public methods */
        TIME("Time", true),
        ASSETS("Assets", true),
        ASSET_PAIRS("AssetPairs", true),
        TICKER("Ticker", true),
        OHLC("OHLC", true),
        DEPTH("Depth", true),
        TRADES("Trades", true),
        SPREAD("Spread", true),

        /* Private methods */
        BALANCE("Balance", false),
        TRADE_BALANCE("TradeBalance", false),
        OPEN_ORDERS("OpenOrders", false),
        CLOSED_ORDERS("ClosedOrders", false),
        QUERY_ORDERS("QueryOrders", false),
        TRADES_HISTORY("TradesHistory", false),
        QUERY_TRADES("QueryTrades", false),
        OPEN_POSITIONS("OpenPositions", false),
        LEDGERS("Ledgers", false),
        QUERY_LEDGERS("QueryLedgers", false),
        TRADE_VOLUME("TradeVolume", false),
        ADD_ORDER("AddOrder", false),
        CANCEL_ORDER("CancelOrder", false),
        DEPOSIT_METHODS("DepositMethods", false),
        DEPOSIT_ADDRESSES("DepositAddresses", false),
        DEPOSIT_STATUS("DepositStatus", false),
        WITHDRAW_INFO("WithdrawInfo", false),
        WITHDRAW("Withdraw", false),
        WITHDRAW_STATUS("WithdrawStatus", false),
        WITHDRAW_CANCEL("WithdrawCancel", false),;

        public final String name;
        public final boolean isPublic;

        Method(String name, boolean isPublic) {
            this.name = name;
            this.isPublic = isPublic;
        }
    }
}