/*
 * Copyright (c) 2010-2030 Founder Ltd. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Founder. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the agreements
 * you entered into with Founder.
 *
 */

package com.tencent.nameservice.sdk.consumer.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * TokenInterceptor.
 *
 * @author tenkye
 * @date 2021/12/17 5:11 下午
 */
@Slf4j
@Component
public class TokenInterceptor implements RequestInterceptor {


    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     *
     */
    @Override
    public void apply(RequestTemplate template) {

        template.header("X-SHA-TOKEN", "xxxx");
    }
}
