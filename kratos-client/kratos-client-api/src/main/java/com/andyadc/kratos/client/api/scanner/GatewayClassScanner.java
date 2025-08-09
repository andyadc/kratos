package com.andyadc.kratos.client.api.scanner;

import com.andyadc.kratos.client.api.annotation.GatewayInvoker;
import com.andyadc.kratos.client.api.annotation.GatewayService;
import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.constants.Protocol;
import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.invoker.HttpServiceInvoker;
import com.andyadc.kratos.context.invoker.RpcServiceInvoker;
import com.andyadc.kratos.context.invoker.ServiceInvoker;
import com.andyadc.kratos.context.invoker.WebSocketServiceInvoker;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.spring.ServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GatewayClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(GatewayClassScanner.class);

    /**
     * 扫描并获取ServiceDefinition实例
     */
    public static ServiceDefinition scanAndGetServiceDefinition(Object bean, Object... args) {
        Class<?> clazz = bean.getClass();
        if (!clazz.isAnnotationPresent(GatewayService.class)) {
            return null;
        }
        Method[] methods = clazz.getMethods();
        if (methods == null || methods.length == 0) {
            return null;
        }
        GatewayService gatewayService = clazz.getAnnotation(GatewayService.class);
        String serviceId = gatewayService.serviceId();
        String protocol = gatewayService.protocol();
        String patternPath = gatewayService.patternPath();
        String version = gatewayService.version();
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        Map<String, ServiceInvoker> invokerMap = new HashMap<>();
        for (Method method : methods) {
            GatewayInvoker gatewayInvoker = method.getAnnotation(GatewayInvoker.class);
            // 为空，执行下一次循环
            if (gatewayInvoker == null) {
                continue;
            }
            String path = gatewayInvoker.path();
            String ruleId = gatewayInvoker.ruleId();
            switch (protocol) {
                case Protocol.HTTP -> invokerMap.put(path, getHttpServiceInvoker(ruleId, path));
                case Protocol.RPC -> {
                    RpcServiceInvoker rpcServiceInvoker = getRpcServiceInvoker(ruleId, path, (ServiceBean<?>) args[0], method);
                    String rpcVersion = rpcServiceInvoker.getVersion();
                    if (!StringUtils.isEmpty(rpcVersion)) {
                        version = rpcVersion;
                    }
                    invokerMap.put(path, rpcServiceInvoker);
                }
                case Protocol.WEBSOCKET -> invokerMap.put(path, getWebSocketServiceInvoker(ruleId, path, method));
                default -> invokerMap.put(path, getHttpServiceInvoker(ruleId, path));
            }
        }
        serviceDefinition.setUniqueId(serviceId + Constants.COLON_SEPARATOR + version);
        serviceDefinition.setServiceId(serviceId);
        serviceDefinition.setVersion(version);
        serviceDefinition.setProtocol(protocol);
        serviceDefinition.setPatternPath(patternPath);
        serviceDefinition.setEnable(Constants.ENABLED_TRUE);
        serviceDefinition.setInvokerMap(invokerMap);
        return serviceDefinition;
    }

    /**
     * 获取HttpServiceInvoker实例
     */
    private static HttpServiceInvoker getHttpServiceInvoker(String ruleId, String path) {
        HttpServiceInvoker serviceInvoker = new HttpServiceInvoker();
        serviceInvoker.setRuleId(ruleId);
        serviceInvoker.setInvokerPath(path);
        return serviceInvoker;
    }

    /**
     * 获取RpcServiceInvoker实例
     */
    private static RpcServiceInvoker getRpcServiceInvoker(String ruleId, String path, ServiceBean<?> serviceBean, Method method) {
        RpcServiceInvoker rpcServiceInvoker = new RpcServiceInvoker();
        rpcServiceInvoker.setRuleId(ruleId);
        rpcServiceInvoker.setInvokerPath(path);

        String methodName = method.getName();
        String registryAddress = serviceBean.getRegistry().getAddress();
        String interfaceClass = serviceBean.getInterface();

        rpcServiceInvoker.setRegisterAddress(registryAddress);
        rpcServiceInvoker.setMethodName(methodName);
        rpcServiceInvoker.setInterfaceClass(interfaceClass);

        String[] parameterTypes = new String[method.getParameterCount()];
        Class<?>[] classes = method.getParameterTypes();
        for (int i = 0; i < classes.length; i++) {
            parameterTypes[i] = classes[i].getName();
        }
        rpcServiceInvoker.setParameterTypes(parameterTypes);
        Integer timeout = getRpcTimeout(serviceBean);
        rpcServiceInvoker.setTimeout(timeout);
        rpcServiceInvoker.setVersion(serviceBean.getVersion());
        return rpcServiceInvoker;

    }

    private static Integer getRpcTimeout(ServiceBean<?> serviceBean) {
        Integer timeout = serviceBean.getTimeout();
        if (timeout == null || timeout <= 0) {
            ProviderConfig providerConfig = serviceBean.getProvider();
            if (providerConfig != null) {
                Integer providerConfigTimeout = providerConfig.getTimeout();
                if (providerConfigTimeout == null || providerConfigTimeout <= 0) {
                    timeout = Constants.RPC_TIMEOUT;
                } else {
                    timeout = providerConfigTimeout;
                }
            }
        }
        return timeout;
    }

    private static WebSocketServiceInvoker getWebSocketServiceInvoker(String ruleId, String path, Method method) {
        WebSocketServiceInvoker webSocketServiceInvoker = new WebSocketServiceInvoker();
        webSocketServiceInvoker.setRuleId(ruleId);
        webSocketServiceInvoker.setInvokerPath(path);
        // TODO
        return webSocketServiceInvoker;
    }
}

