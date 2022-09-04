/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.e2e.flink.v2.elasticsearch;

import org.apache.seatunnel.e2e.flink.FlinkContainer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

public class ElasticsearchSourceToConsoleIT extends FlinkContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSourceToConsoleIT.class);

    private ElasticsearchContainer container;

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:Indentation"})
    @BeforeEach
    public void startElasticsearchContainer() throws InterruptedException{
        container = new ElasticsearchContainer(DockerImageName.parse("elasticsearch:6.8.23").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
                .withNetwork(NETWORK)
                .withNetworkAliases("elasticsearch")
                .withLogConsumer(new Slf4jLogConsumer(LOGGER));
        container.start();
        LOGGER.info("Elasticsearch container started");
        Thread.sleep(5000L);

    }

    @Test
    public void testElasticsearchSourceToConsoleSink() throws IOException, InterruptedException {
        Container.ExecResult sinkEsResult = executeSeaTunnelFlinkJob("/elasticsearch/fakesource_to_elasticsearch.conf");
        Assertions.assertEquals(0, sinkEsResult.getExitCode());
        Container.ExecResult sourceEsResult = executeSeaTunnelFlinkJob("/elasticsearch/elasticsearch_to_console.conf");
        Assertions.assertEquals(0, sourceEsResult.getExitCode());
    }

    @AfterEach
    public void closeContainer() {
        if (container != null) {
            container.stop();
        }
    }
}
