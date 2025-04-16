package com.andyadc.kratos.exector.rpc;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.enums.ResponseCode;
import com.andyadc.kratos.common.exception.RpcConnectionException;
import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.context.attribute.AttributeKeyFactory;
import com.andyadc.kratos.context.cache.filter.CacheFactory;
import com.andyadc.kratos.context.config.GatewayConfig;
import com.andyadc.kratos.context.ctx.RequestResponseContext;
import com.andyadc.kratos.context.factory.RpcFactory;
import com.andyadc.kratos.context.invoker.RpcServiceInvoker;
import com.andyadc.kratos.context.loader.GatewayConfigLoader;
import com.andyadc.kratos.context.request.RpcGatewayRequest;
import com.github.benmanes.caffeine.cache.Cache;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.apache.dubbo.remoting.Constants.DISPATCHER_KEY;
import static org.apache.dubbo.rpc.protocol.dubbo.Constants.SHARE_CONNECTIONS_KEY;

/**
 * RPC辅助类
 */
public class RpcHelper {

    public static final String RPC_TRANSFER_CONTEXT = "RPC_TRANSFER_CONTEXT";

    private static final String APPLICATION_CONFIG_NAME = "kratos-consumer";

    private static final String APPLICATION_OWNER = "kratos";

    private static final String APPLICATION_ORGANIZATION = "kratos";

    private static final int DEFAULT_TIMEOUT = 5000;

    private final ApplicationConfig applicationConfig;
    private final Cache<String, GenericService> cache = CacheFactory.getRpcCache();
    private final ReferenceConfigCache referenceConfigCache = ReferenceConfigCache.getCache();

    private RpcHelper() {
        this.applicationConfig = new ApplicationConfig(APPLICATION_CONFIG_NAME);
        this.applicationConfig.setOwner(APPLICATION_OWNER);
        this.applicationConfig.setOrganization(APPLICATION_ORGANIZATION);
    }

    public static RpcHelper getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public static RpcGatewayRequest buildRpcRequest(RpcServiceInvoker rpcServiceInvoker, Object[] parameters) {
        return RpcFactory.getRpcRequest(rpcServiceInvoker, parameters);
    }

    public static Map<String, String> getRpcAttachment(RequestResponseContext requestResponseContext) {
        return RpcFactory.getRpcAttachment(requestResponseContext);
    }

    public CompletableFuture<Object> $invokeAsync(RequestResponseContext context, RpcGatewayRequest rpcGatewayRequest) {
        fillRpcContext(context);
        //	创建泛化调用对象, 并进行缓存
        GenericService genericService = newGenericServiceForReg(rpcGatewayRequest.getRegistryAddress(),
                rpcGatewayRequest.getInterfaceClass(),
                rpcGatewayRequest.getTimeout(),
                rpcGatewayRequest.getVersion());

        try {
            //执行泛化调用请求
            CompletableFuture<Object> completableFuture = genericService.$invokeAsync(rpcGatewayRequest.getMethodName(),
                    rpcGatewayRequest.getParameterTypes(),
                    rpcGatewayRequest.getArgs());
            return completableFuture;
        } catch (Exception e) {
            throw new RpcConnectionException(e, context.getUniqueId(),
                    context.getOriginRequest().getPath(),
                    rpcGatewayRequest.getInterfaceClass(),
                    rpcGatewayRequest.getMethodName(),
                    ResponseCode.RPC_REQUEST_ERROR);
        }
    }

    private void fillRpcContext(RequestResponseContext context) {
        RpcContext.getContext().set(RPC_TRANSFER_CONTEXT, context);
        //附加信息传递
        if (context.getAttribute(AttributeKeyFactory.getAttachment()) != null) {
            RpcContext.getContext().getAttachments().putAll(context.getAttribute(AttributeKeyFactory.getAttachment()));
        }
    }

    public void destroyAll() {
        cache.invalidateAll();
    }

    private GenericService newGenericServiceForReg(String registriesStr,
                                                   String interfaceClass,
                                                   int timeout,
                                                   String version) {

        String key = registriesStr + ":" + interfaceClass + ":" + version;
        GenericService genericService = cache.get(key, s -> {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress(registriesStr);
            registryConfig.setCheck(false);
            registryConfig.setTimeout(20000);
            if (registriesStr.indexOf("://") < 0) {
                registryConfig.setProtocol("zookeeper");
            }
            return newGenericService(List.of(registryConfig), interfaceClass, timeout, version);
        });
        return genericService;
    }

    private GenericService newGenericService(List<RegistryConfig> registries,
                                             String interfaceClass,
                                             int timeout,
                                             String version) {

        if (timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        GatewayConfig gatewayConfig = GatewayConfigLoader.getGatewayConfig();
        int rpcConnections = gatewayConfig.getRpcConnections();

        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<GenericService>();
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistries(registries);
        referenceConfig.setTimeout(timeout);
        referenceConfig.setGeneric("true");
        referenceConfig.setInterface(interfaceClass);
        referenceConfig.setAsync(true);
        referenceConfig.setCheck(false);
        referenceConfig.setLoadbalance(Constants.RLB_NAME);

        referenceConfig.setParameters(new HashMap<>());
        referenceConfig.getParameters().put(DISPATCHER_KEY, "direct");
        referenceConfig.getParameters().put(SHARE_CONNECTIONS_KEY, String.valueOf(rpcConnections));
        if (StringUtils.isNotEmpty(version)) {
            referenceConfig.setVersion(version);
        }
        return ReferenceConfigCache.getCache().get(referenceConfig);
    }

    private enum Singleton {
        INSTANCE;
        private final RpcHelper singleton;

        Singleton() {
            singleton = new RpcHelper();
        }

        public RpcHelper getInstance() {
            return singleton;
        }
    }

}
