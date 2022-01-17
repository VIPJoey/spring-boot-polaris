/*
 * Copyright (c) 2010-2030 Founder Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Founder. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with Founder.
 *
 */

package com.tencent.nameservice.sdk;

import com.tencent.nameservice.sdk.PolarisProperties.ConsumerProperties;
import feign.Feign;
import feign.RequestInterceptor;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * PolarisConsumerFactoryBean.
 *
 * @author tenkye
 * @date 2021/11/19 9:15 下午
 */
@Slf4j
public class PolarisConsumerFactoryBean<T> implements FactoryBean {

    @Resource
    private PolarisProperties polarisProperties;

    @Resource
    private Map<String, RequestInterceptor> interceptorMap;

    private Class<?> mapperClass;

    // 这里的mapperClass就是Mapper接口的class
    public PolarisConsumerFactoryBean(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    public Class<?> getObjectType() {
        return mapperClass;
    }

    @Override
    public Object getObject() {

        PolarisConsumer consumer = AnnotationUtils.getAnnotation(mapperClass, PolarisConsumer.class);
        assert consumer != null;
        String id = consumer.id();
        Assert.notNull(id, "The attribute id for this PolarisConsumer can't blank.");

        ConsumerProperties properties = polarisProperties.getConsumer().get(id);
        if (null == properties) {
            properties = new ConsumerProperties();
        }
        String namespace = consumer.namespace();
        if (StringUtils.hasText(namespace)) {
            properties.setNamespace(namespace);
        }
        String service = consumer.service();
        if (StringUtils.hasText(service)) {
            properties.setService(service);
        }
        String version = consumer.version();
        if (StringUtils.hasText(version)) {
            properties.setVersion(version);
        }

        // interceptors
        List<RequestInterceptor> interceptors;

        if (CollectionUtils.isNotEmpty(properties.getInterceptors())) {

            // Extract interceptor by name if specified
            interceptors = properties.getInterceptors().stream()
                    .map(x -> interceptorMap.get(x))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {

            // Use all interceptors
            interceptors = new ArrayList<>(interceptorMap.values());
        }

        return Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .requestInterceptors(interceptors)
                .target(PolarisTarget.create(mapperClass, properties));
    }
}
