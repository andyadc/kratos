package com.andyadc.kratos.client.http;

import com.andyadc.kratos.client.api.properties.GatewayClientProperties;
import com.andyadc.kratos.client.http.register.HttpClientRegistryService;

public class HttpClientAutoConfiguration {

    private GatewayClientProperties gatewayClientProperties;

    public HttpClientRegistryService httpClientRegistryService() {
        return new HttpClientRegistryService(gatewayClientProperties);
    }

}
