package com.cream.mqtt.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: Cream
 * @Date: 2023-05-06-14:48
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt")
public class Mqtt {
    //broker的地址
    private String brokerUrl;
    //用户名
    private String username;
    //密码
    private String password;
    //clientId（不可重复）
    private String clientId;
    //发布的Topic
    private String pubTopic;
    //订阅的Topic
    private String subTopic;
}
