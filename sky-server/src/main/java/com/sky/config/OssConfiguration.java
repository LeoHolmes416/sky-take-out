package com.sky.config;

import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyuncs.exceptions.ClientException;
import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OSS配置类，用于创建 AliOssUtil 对象
 */
@Configuration
@Slf4j
public class OssConfiguration {
    @Bean
    @ConditionalOnMissingBean  //configuration默认单例，这句可以不写
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) throws ClientException {
        log.info("开始创建阿里云文件上传工具类对象: {}",aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getBucketName(),
                //从环境变量获取AccessKey的ID和密码
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider());
    }
}
