/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.woonsan.oak.app.simple;

import java.io.File;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.jcr.Repository;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.server.remoting.davex.JcrRemotingServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleOakAppConfiguration {

    private static Logger log = LoggerFactory.getLogger(SimpleOakAppConfiguration.class);

    private volatile Repository repository;

    @SuppressWarnings("serial")
    @Bean
    public ServletRegistrationBean<JcrRemotingServlet> httpBindingServletRegistrationBean() {
        return new ServletRegistrationBean<JcrRemotingServlet>(new JcrRemotingServlet() {
            @Override
            protected Repository getRepository() {
                return repository();
            }
        }, "/*");
    }

    @Bean
    public Repository repository() {
        try {
            final FileStore fs = FileStoreBuilder.fileStoreBuilder(new File("repository")).build();
            final SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            repository = new Jcr(new Oak(ns)).createRepository();
            log.info("Reository creaetd with nodeStore, {}", ns);
        } catch (InvalidFileStoreVersionException e) {
            log.error("Failed to create repository due to invalid file store version.", e);
        } catch (IOException e) {
            log.error("Failed to create repository due to IO exception.", e);
        }

        return repository;
    }

    @PreDestroy
    public void destroy() {
        if (repository instanceof JackrabbitRepository) {
            ((JackrabbitRepository) repository).shutdown();
        }
    }
}