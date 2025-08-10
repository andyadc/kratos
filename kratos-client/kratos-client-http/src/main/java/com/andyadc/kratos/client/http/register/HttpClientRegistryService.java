package com.andyadc.kratos.client.http.register;

import com.andyadc.kratos.client.api.properties.GatewayClientProperties;
import com.andyadc.kratos.client.api.register.AbstractClientRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class HttpClientRegistryService extends AbstractClientRegistryService {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientRegistryService.class);

    private static final Set<Object> BEAN_SET = new HashSet<>();

    public HttpClientRegistryService(GatewayClientProperties gatewayClientProperties) {
        super(gatewayClientProperties);
    }

}
