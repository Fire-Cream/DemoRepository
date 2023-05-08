package com.cream.mqtt.callback;

import com.cream.mqtt.client.MqClient;
import com.cream.mqtt.enums.QosEnum;
import com.cream.mqtt.model.Mqtt;
import jakarta.annotation.Resource;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: Cream
 * @Date: 2023-05-06-15:08
 * @Description:
 */
@Component
public class MessageCallBack implements MqttCallbackExtended {

    private MqClient mqClient;

    public MessageCallBack() {
    }

    public MessageCallBack(MqClient mqClient) {
        this.mqClient = mqClient;
    }

    //日志
    private static final Logger log = LoggerFactory.getLogger(MessageCallBack.class);

    /**
     * 改方法是连接完成的回调
     *
     * @param isReconnect
     * @param serverUrl
     */
    @Override
    public void connectComplete(boolean isReconnect, String serverUrl) {
        if (isReconnect) {
            log.info("重连成功，服务地址：" + serverUrl);
        } else {
            log.info("连接成功，服务地址：" + serverUrl);
        }
    }

    /**
     * 丢失对服务端的连接后触发该方法回调，此处可以做一些特殊处理，比如重连
     *
     * @param throwable
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.info("连接断开");
    }

    /**
     * 订阅到消息后的回调
     * 该方法由mqtt客户端同步调用,在此方法未正确返回之前，不会发送ack确认消息到broker
     * 一旦该方法向外抛出了异常客户端将异常关闭，当再次连接时；所有QoS1,QoS2且客户端未进行ack确认的消息都将由broker服务器再次发送到客户端
     *
     * @param topic
     * @param mqttMessage
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("订阅到消息;topic={},messageId={},qos={},msg={}",
                topic, mqttMessage.getId(), mqttMessage.getQos(), new String(mqttMessage.getPayload()));
    }

    /**
     * 消息发布完成且收到ack确认后的回调
     * QoS0：消息被网络发出后触发一次
     * QoS1：当收到broker的PUBACK消息后触发
     * QoS2：当收到broer的PUBCOMP消息后触发
     *
     * @param iMqttDeliveryToken
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        int messageId = iMqttDeliveryToken.getMessageId();
        String[] topics = iMqttDeliveryToken.getTopics();
        log.info("消息发布成功,messageId={},topics={}", messageId, topics);
    }
}
