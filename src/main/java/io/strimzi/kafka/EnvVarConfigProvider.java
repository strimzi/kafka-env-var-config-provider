/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.kafka;

import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Kafka configuration provider for reading environment variables
 */
public class EnvVarConfigProvider implements ConfigProvider {
    private static final Logger LOG = LoggerFactory.getLogger(EnvVarConfigProvider.class);

    private final Map<String, String> envVars;

    /**
     * Creates the configuration provider and gets the environment variables from the environment. This constructor is
     * used when running inside Apache Kafka.
     */
    public EnvVarConfigProvider() {
        this.envVars = System.getenv();
    }

    /**
     * Creates the configuration provider with the environment variables passed as parameter. This constructor is used
     * in tests.
     *
     * @param envVars   Map with environment variables
     */
    /*test*/ EnvVarConfigProvider(Map<String, String> envVars) {
        this.envVars = envVars;
    }

    @Override
    public void configure(Map<String, ?> map) {
        LOG.info("Configuring EnvVar config provider: {}", map);
    }

    @Override
    public ConfigData get(String path) {
        return new ConfigData(envVars);
    }

    @Override
    public ConfigData get(String path, Set<String> keys) {
        Map<String, String> vars = new HashMap(envVars);
        vars.keySet().retainAll(keys);
        return new ConfigData(vars);
    }

    @Override
    public void close() {
        LOG.info("Closing EnvVar config provider");
    }
}
