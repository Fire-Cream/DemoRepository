package com.cream.mqtt.client;

import com.cream.mqtt.callback.MessageCallBack;
import com.cream.mqtt.enums.QosEnum;
import com.cream.mqtt.model.Mqtt;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: Cream
 * @Date: 2023-05-06-14:59
 * @Description:
 */
@Component
public class MqClient {

    //日志
    private static final Logger log = LoggerFactory.getLogger(MqClient.class);

    @Resource
    private Mqtt mqtt;

    @Resource
    private MessageCallBack messageCallBack;

    //客户端
    private IMqttClient mqttClient;

    /**
     * 断开连接
     *
     * @PostConstruct: 在客户端应用程序启动后就将该方法初始化好
     */
    @PostConstruct
    private void init() {
        MqttClientPersistence mqttClientPersistence = new MemoryPersistence();
        try {
            //获取mqttClient对象
            mqttClient = new MqttClient(mqtt.getBrokerUrl(), mqtt.getClientId(), mqttClientPersistence);
            //连接mqtt
            this.connect(mqtt.getUsername(), mqtt.getPassword());

            this.subscribe(mqtt.getSubTopic(), QosEnum.Qos1);

            this.publish(mqtt.getPubTopic(), "test", QosEnum.Qos1, false);
        } catch (MqttException e) {
            log.error("初始化mqtt失败, brokerUrl={},clientId={}", mqtt.getBrokerUrl(), mqtt.getClientId());
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接服务端
     *
     * @param username
     * @param password
     */
    public void connect(String username, String password) throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        mqttClient.setCallback(messageCallBack);
        try {
            mqttClient.connect(options);
        } catch (MqttException e) {
            mqttClient.reconnect();
            log.error("mqtt客户端连接服务器失败, 失败原因{}", e.getMessage());
        }
    }

    /**
     * 断开连接
     *
     * @PreDestroy: 是对该方法的优化, 作用是在客户端连接断开时自动调用该方法
     */
    @PreDestroy
    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            log.error("断开连接失败, 失败原因{}", e.getMessage());
        }
    }

    /**
     * 重连方法
     */
    public void reconnect() {
        try {
            mqttClient.reconnect();
        } catch (MqttException e) {
            log.error("重连失败, 失败原因", e.getMessage());
        }
    }


    /**
     * 消息发布
     *
     * @param topic  主题名称
     * @param msg    消息内容
     * @param qos    消息发布类型
     * @param retain 是否保留消息
     */
    public void publish(String topic, String msg, QosEnum qos, boolean retain) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(msg.getBytes());
        mqttMessage.setQos(qos.value());
        mqttMessage.setRetained(retain);

        try {
            mqttClient.publish(topic, mqttMessage);
            log.error("消息发布");
        } catch (MqttException e) {
            log.error("消息发布失败, errorMsg={},topic={},payload={},Qos={},retain={}", e.getMessage(), topic, msg, qos.value(), retain);
        }
    }

    /**
     * 消息订阅
     *
     * @param topic 订阅主题
     * @param qos   消息发布类型
     */
    public void subscribe(String topic, QosEnum qos) {

        try {
            mqttClient.subscribe(topic, qos.value());
            log.info("消息订阅, topic={},qos={}", topic, qos.value());
        } catch (Exception e) {
            log.error("消息订阅失败, errorMsg={},topic={},qos={}", e.getMessage(), topic, qos.value());
        }
    }

    /**
     * 取消订阅
     *
     * @param topic 取消订阅主题
     */
    public void unsubscribe(String topic) {

        try {
            mqttClient.unsubscribe(topic);
            log.info("取消订阅, topic={}", topic);
        } catch (MqttException e) {
            log.error("取消订阅失败, errorMsg={},topic={}", e.getMessage(), topic);
        }
    }

}
