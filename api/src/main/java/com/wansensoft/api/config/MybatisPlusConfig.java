/*
 * Copyright 2023-2033 WanSen AI Team, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://opensource.wansenai.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.wansensoft.api.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.wansensoft.utils.redis.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@MapperScan("com.wansensoft.mappers")
public class MybatisPlusConfig {

    public final RedisUtil redisUtil;

    public MybatisPlusConfig(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    /**
     * 根据token截取租户id
     * @param token
     * @return
     */
    public Long getTenantIdByToken(String token) {
        long tenantId = 0L;
        if(StringUtils.hasText(token)) {
            tenantId = Long.parseLong(redisUtil.getString(token + ":tenantId"));
        }
        return tenantId;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(HttpServletRequest request) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
//            @Override
//            public Expression getTenantId() {
//                String token = request.getHeader("Authorization");
//                Long tenantId = getTenantIdByToken(token);
//                if (tenantId!=0L) {
//                    return new LongValue(tenantId);
//                } else {
//                    //超管
//                    return null;
//                }
//            }
//
//            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
//            @Override
//            public boolean ignoreTable(String tableName) {
//                //获取开启状态
//                boolean res = true;
//                String token = request.getHeader("Authorization");
//                Long tenantId = getTenantIdByToken(token);
//                if (tenantId!=0L) {
//                    // 这里可以判断是否过滤表
//                    if ("jsh_material_property".equals(tableName) || "jsh_sequence".equals(tableName)
//                            || "jsh_user_business".equals(tableName) || "jsh_function".equals(tableName)
//                            || "jsh_platform_config".equals(tableName) || "jsh_tenant".equals(tableName)) {
//                        res = true;
//                    } else {
//                        res = false;
//                    }
//                }
//                return res;
//            }
//        }));


        // 如果用了分页插件注意先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        // 用了分页插件必须设置 MybatisConfiguration#useDeprecatedExecutor = false
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}