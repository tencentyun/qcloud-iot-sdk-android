package com.qcloud.iot.samples.mqtt;

import android.content.Context;

import com.qcloud.iot.mqtt.TXMqttActionCallBack;
import com.qcloud.iot.mqtt.TXMqttConnection;
import com.qcloud.iot.mqtt.TXMqttConstants;
import com.qcloud.iot.util.AsymcSslUtils;
import com.qcloud.iot.util.TXLog;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class MQTTSample {

    private static final String TAG = "iot.mqtt.MQTTSample";

    /**
     * 产品ID
     */
    private static final String PRODUCT_ID = "YOUR_PRODUCT_ID";

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "YOUR_DEVICE_NAME";

    /**
     * 设备证书名
     */
    private static final String DEVICE_CERT_NAME = "YOUR_DEVICE_NAME_cert.crt";

    /**
     * 设备私钥文件名
     */
    private static final String DEVICE_KEY_NAME = "YOUR_DEVICE_NAME_private.key";

    private Context mContext;

    private TXMqttActionCallBack mMqttActionCallBack;

    /**
     * MQTT连接实例
     */
    private TXMqttConnection mMqttConnection;

    /**
     * 请求ID
     */
    private static AtomicInteger requestID = new AtomicInteger(0);

    public MQTTSample(Context context, TXMqttActionCallBack callBack) {
        mContext = context;
        mMqttActionCallBack = callBack;
        mMqttConnection = new TXMqttConnection(mContext, PRODUCT_ID, DEVICE_NAME, mMqttActionCallBack);
    }

    /**
     * 获取主题
     *
     * @param topicName
     * @return
     */
    private String getTopic(String topicName) {
        return String.format("%s/%s/%s", PRODUCT_ID, DEVICE_NAME, topicName);
    }

    /**
     * 建立MQTT连接
     */
    public void connect() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(8);
        options.setKeepAliveInterval(240);
        options.setAutomaticReconnect(true);
        options.setSocketFactory(AsymcSslUtils.getSocketFactoryByAssetsFile(mContext, DEVICE_CERT_NAME, DEVICE_KEY_NAME));

        MQTTRequest mqttRequest = new MQTTRequest("connect", requestID.getAndIncrement());
        mMqttConnection.connect(options, mqttRequest);

        DisconnectedBufferOptions bufferOptions = new DisconnectedBufferOptions();
        bufferOptions.setBufferEnabled(true);
        bufferOptions.setBufferSize(1024);
        bufferOptions.setDeleteOldestMessages(true);
        mMqttConnection.setBufferOpts(bufferOptions);
    }

    /**
     * 断开MQTT连接
     */
    public void disconnect() {
        MQTTRequest mqttRequest = new MQTTRequest("disconnect", requestID.getAndIncrement());
        mMqttConnection.disConnect(mqttRequest);
    }

    /**
     * 订阅主题
     *
     * @param topicName 主题名
     */
    public void subscribeTopic(String topicName) {
        // 主题
        String topic = getTopic(topicName);
        // QOS等级
        int qos = TXMqttConstants.QOS1;
        // 用户上下文（请求实例）
        MQTTRequest mqttRequest = new MQTTRequest("subscribeTopic", requestID.getAndIncrement());

        // 订阅主题
        mMqttConnection.subscribe(topic, qos, mqttRequest);
    }

    /**
     * 取消订阅主题
     *
     * @param topicName 主题名
     */
    public void unSubscribeTopic(String topicName) {
        // 主题
        String topic = getTopic(topicName);
        // 用户上下文（请求实例）
        MQTTRequest mqttRequest = new MQTTRequest("unSubscribeTopic", requestID.getAndIncrement());

        // 取消订阅主题
        mMqttConnection.unSubscribe(topic, mqttRequest);
    }

    /**
     * 发布主题
     */
    public void publishTopic(String topicName, Map<String, String> data) {
        // 主题
        String topic = getTopic(topicName);
        // MQTT消息
        MqttMessage message = new MqttMessage();

        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entrys : data.entrySet()) {
                jsonObject.put(entrys.getKey(), entrys.getValue());
            }
        } catch (JSONException e) {
            TXLog.e(TAG, e, "pack json data failed!");
        }
        message.setQos(TXMqttConstants.QOS1);
        message.setPayload(jsonObject.toString().getBytes());

        // 用户上下文（请求实例）
        MQTTRequest mqttRequest = new MQTTRequest("publishTopic", requestID.getAndIncrement());

        // 发布主题
        mMqttConnection.publish(topic, message, mqttRequest);
    }
}
