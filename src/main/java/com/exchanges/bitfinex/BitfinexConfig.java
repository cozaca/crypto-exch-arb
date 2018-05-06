package com.exchanges.bitfinex;

import com.github.jnidzwetzki.bitfinex.v2.BitfinexApiBroker;
import com.github.jnidzwetzki.bitfinex.v2.entity.APIException;
import com.github.jnidzwetzki.bitfinex.v2.entity.ConnectionCapabilities;

public class BitfinexConfig {

    private final BitfinexApiBroker connectionBroker;

    public BitfinexConfig()
    {
        connectionBroker = BitFinexConnection.getConnection();
    }

    public void connect() throws APIException {
        connectionBroker.connect();
    }

    public ConnectionCapabilities getCapabilities()
    {
        return connectionBroker.getCapabilities();
    }


}
