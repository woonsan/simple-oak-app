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

import javax.annotation.PreDestroy;
import javax.jcr.Repository;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.api.ContentRepository;
import org.apache.jackrabbit.oak.http.OakServlet;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleOakAppConfiguration {

    private static Logger log = LoggerFactory.getLogger(SimpleOakAppConfiguration.class);

    private ContentRepository contentRepository;
    private volatile Repository repository;

    @Bean
    public ServletRegistrationBean<OakServlet> httpBindingServletRegistrationBean() {
        checkRepositoryInitialized();
        return new ServletRegistrationBean(new OakServlet(contentRepository), "/*");
    }

    @Bean
    public Repository repository() {
        checkRepositoryInitialized();
        return repository;
    }

    @PreDestroy
    public void destroy() {
        log.info("TODO: shutdown the repository and its store object(s)...");
    }

    private void checkRepositoryInitialized() {
        Repository repo = repository;

        if (repo == null) {
            synchronized (this) {
                repo = repository;

                if (repo == null) {
                    repo = new Jcr(new Oak() {
                        @Override
                        public ContentRepository createContentRepository() {
                            contentRepository = super.createContentRepository();
                            return contentRepository;
                        }
                    }).createRepository();

                    repository = repo;
                }
            }
        }
    }
}