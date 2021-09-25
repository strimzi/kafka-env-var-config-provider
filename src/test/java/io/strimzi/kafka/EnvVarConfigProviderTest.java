/*
 * Copyright Strimzi authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.strimzi.kafka;

import org.apache.kafka.common.config.ConfigData;
import org.apache.kafka.common.config.provider.ConfigProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvVarConfigProviderTest {
    private static ConfigProvider configProvider;

    @BeforeAll
    public static void beforeAll() {
        Map<String, String> envVars = new HashMap<>(3);
        envVars.put("ENV_VAR_01", "Value1");
        envVars.put("ENV_VAR_02", "Value2");
        envVars.put("ENV_VAR_03", "Value3");

        configProvider = new EnvVarConfigProvider(envVars);
        configProvider.configure(emptyMap());
    }

    @Test
    public void testGetAll() {
        ConfigData config = configProvider.get("somePath");
        Map<String, String> data = config.data();

        assertThat(data.size(), is(3));
        assertThat(data.get("ENV_VAR_01"), is("Value1"));
        assertThat(data.get("ENV_VAR_02"), is("Value2"));
        assertThat(data.get("ENV_VAR_03"), is("Value3"));
    }

    @Test
    public void testGetOneKey() {
        ConfigData config = configProvider.get("somePath", Collections.singleton("ENV_VAR_02"));
        Map<String, String> data = config.data();

        assertThat(data.size(), is(1));
        assertThat(data.get("ENV_VAR_02"), is("Value2"));
    }

    @Test
    public void testGetOneValueWithNullPath() {
        ConfigData config = configProvider.get(null, Collections.singleton("ENV_VAR_02"));
        Map<String, String> data = config.data();

        assertThat(data.size(), is(1));
        assertThat(data.get("ENV_VAR_02"), is("Value2"));
    }

    @Test
    public void testGetMultipleKeys() {
        ConfigData config = configProvider.get("somePath", new HashSet<>(Arrays.asList("ENV_VAR_02", "ENV_VAR_03")));
        Map<String, String> data = config.data();

        assertThat(data.size(), is(2));
        assertThat(data.get("ENV_VAR_02"), is("Value2"));
        assertThat(data.get("ENV_VAR_03"), is("Value3"));
    }

    @Test
    public void testGetMissingKey() {
        ConfigData config = configProvider.get("somePath", Collections.singleton("ENV_VAR_04"));
        Map<String, String> data = config.data();

        assertThat(data.size(), is(0));
    }

    @Test
    public void testServiceLoaderDiscovery() {
        ServiceLoader<ConfigProvider> serviceLoader = ServiceLoader.load(ConfigProvider.class);

        boolean discovered = false;

        for (ConfigProvider service : serviceLoader)    {
            System.out.println(service.getClass());
            if (service instanceof EnvVarConfigProvider) {
                discovered = true;
            }
        }

        assertTrue(discovered);
    }
}
