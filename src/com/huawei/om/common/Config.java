package com.huawei.om.common;

import com.alibaba.druid.filter.config.ConfigFilter;
import com.huawei.om.utils.PropertiesUtil;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by z00383627 on 2017/3/1.
 *
 */
public class Config {
    private static final Logger LOG = Logger.getLogger(Config.class);

    private String propertiesPath = null;
    private Properties properties = null;
    private static Config config = new Config();

    private Config() {

    }

    public static Config getInstance() {
        return Config.config;
    }

    public void init() {
        this.properties = new ConfigFilter().loadConfig(Constant.CONFIG_SCHEME_FILE + this.propertiesPath);
    }

    public void init(String propertiesPath) {
        this.propertiesPath = propertiesPath;
        this.init();
    }

    public void init(String[] args) {
        if (null == args || args.length < 1) {
            LOG.error("spark app should be launched with a properties-file");
            System.exit(0);
        }
        init(args[0]);
    }

    public String getStringConfig(String key) {     //TODO trim
        return PropertiesUtil.getProperty(this.properties, key);
    }

    public int getIntegerConfig(String key) {
        return this.getIntegerConfig(key, 0);
    }

    public int getIntegerConfig(String key, int defaultValue) {
        try {
            return Integer.parseInt(this.getStringConfig(key));
        }catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public float getFloatConfig(String key) {
        return this.getFloatConfig(key, 0.0f);
    }

    public float getFloatConfig(String key, float defaultValue) {
        try {
            return Float.parseFloat(this.getStringConfig(key));
        }catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanConfig(String key) {
        return Boolean.parseBoolean(PropertiesUtil.getProperty(this.properties, key));
    }

    public Map<String, String> getKafkaInputParams() {
        HashMap<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put(Constant.GROUP_ID, Config.getInstance().getStringConfig(Constant.GROUP_ID));
        kafkaParams.put(Constant.METADATA_BROKER_LIST, Config.getInstance().getStringConfig(Constant.METADATA_BROKER_LIST));
        kafkaParams.put(Constant.ZOOKEEPER_CONNECT, Config.getInstance().getStringConfig(Constant.ZOOKEEPER_CONNECT));
        kafkaParams.put(Constant.ZOOKEEPER_CONNECT_TIMEOUT_MS, Config.getInstance().getStringConfig(Constant.ZOOKEEPER_CONNECT_TIMEOUT_MS));
        kafkaParams.put(Constant.AUTO_OFFSET_RESET, Config.getInstance().getStringConfig(Constant.AUTO_OFFSET_RESET));
        kafkaParams.put(Constant.AUTO_COMMIT_ENABLE, Config.getInstance().getStringConfig(Constant.AUTO_COMMIT_ENABLE));
        return kafkaParams;
    }
}
