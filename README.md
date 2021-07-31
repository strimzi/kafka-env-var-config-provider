[![Build Status](https://dev.azure.com/cncf/strimzi/_apis/build/status/kafka-env-var-config-provider?branchName=main)](https://dev.azure.com/cncf/strimzi/_build/latest?definitionId=37&branchName=main)
[![GitHub release](https://img.shields.io/github/release/strimzi/kafka-env-var-config-provider.svg)](https://github.com/strimzi/kafka-env-var-config-provider/releases/latest)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.strimzi/kafka-env-var-config-provider/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.strimzi/kafka-env-var-config-provider)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Twitter Follow](https://img.shields.io/twitter/follow/strimziio.svg?style=social&label=Follow&style=for-the-badge)](https://twitter.com/strimziio)

# EnvVar Configuration Provider for Apache Kafka

Apache Kafka supports pluggable configuration providers which can load configuration data from external sources.
The configuration providers in this repo can be used to load data from environment variables.
It can be used in all Kafka components and does not depend on the other Strimzi components. 
So you could, for example, use it with your producer or consumer applications even if you don't use the Strimzi operators to provide your Kafka cluster.
One of the example use-cases is to load certificates or JAAS configuration from environment variables mapped from Kubernetes Secrets.

## Using it with Strimzi

From Strimzi Kafka Operators release 0.25.0, the EnvVar Configuration Provider is included in all the Kafka deployments.
You can use it for example with Kafka Connect and Kafka Connect connectors.
Following example shows how to use it to get database password from environment variable in the connector configuration:

1) Deploy Kafka Connect, enable the EnvVar Configuration Provider and map database password to environment variable:
    ```yaml
    apiVersion: kafka.strimzi.io/v1beta2
    kind: KafkaConnect
    metadata:
      name: my-connect
      annotations:
        strimzi.io/use-connector-resources: "true"
    spec:
      # ...
      config:
        # ...
        config.providers: env
        config.providers.env.class: io.strimzi.kafka.EnvVarConfigProvider
      # ...
      externalConfiguration:
        env:
          - name: DB_PASSWORD
            valueFrom:
              secretKeyRef:
                name: db-creds
                key: dbPassword
      # ...
      ```

2) Create the connector:
    ```yaml
    apiVersion: kafka.strimzi.io/v1beta2
    kind: KafkaConnector
    metadata:
      name: my-connector
      labels:
        strimzi.io/cluster: my-connect
    spec:
      # ...
      config:
        option: ${env:DB_PASSWORD}
        # ...
    ```

## Adding the EnvVar Configuration Provider to Apache Kafka clients

You can add EnvVar Configuration Provider as any other Java dependency using Maven or any other build tool.
For example:

```xml
<dependency>
    <groupId>io.strimzi</groupId>
    <artifactId>kafka-env-var-config-provider</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Adding the EnvVar Configuration Provider to Apache Kafka server components

You can also use the EnvVar Configuration Provider with your own Apache Kafka deployments not managed by Strimzi. 
To add EnvVar Configuration Provider to your own Apache Kafka server distribution, you can download the ZIP or TAR.GZ files frm the GitHub release page and unpack it into Kafka's `libs` directory.

## Using the configuration provider

First, you need to initializer the configuration provider.

```properties
config.providers=env
config.providers.env.class=io.strimzi.kafka.EnvVarConfigProvider
```

Once you initialize it, you can use it to load data from environment variables.
For example:
```properties
option=${env:MY_ENV_VAR}
```

## Other Strimzi Configuration Providers

If you run Apache Kafka on Kubernetes, you might also be interested in our [Kubernetes Configuration Provider](https://github.com/strimzi/kafka-kubernetes-config-provider).
