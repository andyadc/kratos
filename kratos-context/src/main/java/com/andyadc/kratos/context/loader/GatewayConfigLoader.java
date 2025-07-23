package com.andyadc.kratos.context.loader;

import com.andyadc.kratos.common.constants.Constants;
import com.andyadc.kratos.common.util.BeanPropertiesUtils;
import com.andyadc.kratos.common.util.StringUtils;
import com.andyadc.kratos.context.config.GatewayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * 网关配置加载器
 */
public class GatewayConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(GatewayConfigLoader.class);

    /**
     * 环境变量的前缀
     */
    private final static String CONFIG_ENV_PREFIEX = "KRATOS_";

    /**
     * 从环境变量获取的配置文件名称
     */
    private final static String CONFIG_ENV_FILE = "KRATOS_CONFIG_FILE";

    /**
     * JVM参数前缀
     */
    private final static String CONFIG_JVM_PREFIEX = "kratos.";

    /**
     * 从JVM参数获取的配置文件名称
     */
    private final static String CONFIG_JVM_FILE = "kratos.config.file";

    private final static GatewayConfigLoader INSTANCE = new GatewayConfigLoader();

    private final GatewayConfig gatewayConfig = new GatewayConfig();

    private GatewayConfigLoader() {
    }

    public static GatewayConfigLoader getInstance() {
        return INSTANCE;
    }

    public static GatewayConfig getGatewayConfig() {
        return INSTANCE.gatewayConfig;
    }

    public GatewayConfig loadConfig(String[] args) {
        // 加载配置文件中的配置
        this.loadFileConfig(args);
        // 加载环境变量配置
        this.loadEnvConfig();
        // 加载JVM参数
        this.loadJvmConfig();
        // 加载运行时参数
        this.loadRuntimeConfig(args);
        return gatewayConfig;
    }

    // 加载运行时参数
    private void loadRuntimeConfig(String[] args) {
        if (args != null && args.length > 0) {
            Properties properties = new Properties();
            for (String arg : args) {
                if (arg.startsWith(Constants.JVM_PARAMS_PREFIX) && arg.contains(Constants.EQUAL_SEPARATOR)) {
                    properties.put(arg.substring(2, arg.indexOf(Constants.EQUAL_SEPARATOR)), arg.substring(arg.indexOf(Constants.EQUAL_SEPARATOR) + 1));
                }
            }
            BeanPropertiesUtils.copy(properties, gatewayConfig);
        }
    }

    // 加载JVM参数
    private void loadJvmConfig() {
        Properties properties = System.getProperties();
        BeanPropertiesUtils.copy(properties, gatewayConfig, CONFIG_JVM_PREFIEX);
    }

    // 加载环境变量配置
    private void loadEnvConfig() {
        Map<String, String> envConfig = System.getenv();
        Properties properties = new Properties();
        properties.putAll(envConfig);
        BeanPropertiesUtils.copy(properties, gatewayConfig, CONFIG_ENV_PREFIEX);
    }

    // 加载配置文件内容
    private void loadFileConfig(String[] args) {
        String configFileName = this.getConfigFileName(args);
        // 加载配置文件
        InputStream is = GatewayConfigLoader.class.getClassLoader().getResourceAsStream(configFileName);
        if (is != null) {
            Properties properties = new Properties();
            try (is) {
                properties.load(is);
                BeanPropertiesUtils.copy(properties, gatewayConfig);
            } catch (Exception e) {
                logger.warn("loadFileConfig error, configFileName:{}", configFileName, e);
            }
            // 不处理
        }
    }

    // 获取配置文件
    private String getConfigFileName(String[] args) {
        // 默认的文件名称
        String configFileName = gatewayConfig.getConfigFileName();
        // 环境变量配置了文件名，则使用环境变量配置的文件名
        if (!StringUtils.isEmpty(System.getenv(CONFIG_ENV_FILE))) {
            configFileName = System.getenv(CONFIG_ENV_FILE);
        }
        // 如果JVM参数配置了文件名，则使用JVM参数配置的文件名
        if (!StringUtils.isEmpty(System.getProperty(CONFIG_JVM_FILE))) {
            configFileName = System.getProperty(CONFIG_JVM_FILE);
        }
        // 如果运行时参数配置了文件名，则使用运行时参数的配置文件
        if (args != null) {
            for (String arg : args) {
                if (arg.startsWith(Constants.JVM_PARAMS_PREFIX) && arg.contains(Constants.EQUAL_SEPARATOR)
                        && CONFIG_JVM_FILE.equals(arg.substring(2, arg.indexOf(Constants.EQUAL_SEPARATOR)))) {
                    configFileName = arg.substring(arg.indexOf(Constants.EQUAL_SEPARATOR) + 1);
                }
            }
        }
        return configFileName;
    }

}
