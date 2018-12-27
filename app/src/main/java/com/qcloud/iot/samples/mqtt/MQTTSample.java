package com.qcloud.iot.samples.mqtt;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.qcloud.iot.gateway.TXGatewayConnection;
import com.qcloud.iot.mqtt.TXMqttActionCallBack;
import com.qcloud.iot.mqtt.TXMqttConnection;
import com.qcloud.iot.mqtt.TXMqttConstants;
import com.qcloud.iot.mqtt.TXOTACallBack;
import com.qcloud.iot.mqtt.TXOTAConstansts;

import com.qcloud.iot.util.AsymcSslUtils;
import com.qcloud.iot.util.SymcSslUtils;

import com.qcloud.iot.util.TXLog;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

public class MQTTSample {

    private static final String TAG = "TXMQTT";

    // Default Value, should be changed in testing
    private String mBrokerURL = "ssl://iotcloud-mqtt.gz.tencentdevices.com:8883";
    private String mProductID = "PRODUCT-ID";
    private String mDevName = "DEVICE-NAME";
    private String mDevPSK = "DEVICE-SECRET";
    private String mSubProductID = "SUBDEV_PRODUCT-ID";
    private String mSubDevName = "SUBDEV_DEV-NAME";
    private String mTestTopic = "TEST_TOPIC_WITH_SUB_PUB";
	
    private Context mContext;

    private TXMqttActionCallBack mMqttActionCallBack;

    /**
     * MQTT连接实例
     */
    private TXGatewayConnection mMqttConnection;

    /**
     * 请求ID
     */
    private static AtomicInteger requestID = new AtomicInteger(0);

    public MQTTSample(Context context, TXMqttActionCallBack callBack) {
        mContext = context;
        mMqttActionCallBack = callBack;
        mMqttConnection = new TXGatewayConnection(mContext, mBrokerURL, mProductID, mDevName, mDevPSK, mMqttActionCallBack);
    }

    public MQTTSample(Context context, TXMqttActionCallBack callBack, String brokerURL, String productId,
                      String devName, String devPSK, String subProductID, String subDevName, String testTopic) {
        mBrokerURL = brokerURL;
        mProductID = productId;
        mDevName = devName;
        mDevPSK = devPSK;
        mSubProductID = subProductID;
        mSubDevName = subDevName;
        mTestTopic = testTopic;

        mContext = context;
        mMqttActionCallBack = callBack;
        mMqttConnection = new TXGatewayConnection(mContext, mBrokerURL, mProductID, mDevName, mDevPSK, mMqttActionCallBack);
    }

    /**
     * 获取主题
     *
     * @param topicName
     * @return
     */
    private String getTopic(String topicName) {
        return mTestTopic;
    }

    /**
     * 建立MQTT连接
     */
    public void connect() {
        mMqttConnection = new TXGatewayConnection(mContext, mBrokerURL, mProductID, mDevName, mDevPSK, mMqttActionCallBack);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(8);
        options.setKeepAliveInterval(240);
        options.setAutomaticReconnect(true);

        options.setSocketFactory(AsymcSslUtils.getSocketFactory());
       

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

    public void setSubdevOnline() {
        // set subdev online
        mMqttConnection.gatewaySubdevOnline(mSubProductID, mSubDevName);
    }

    public void setSubDevOffline() {
        mMqttConnection.gatewaySubdevOffline(mSubProductID, mSubDevName);
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

        Log.d(TAG, "sub topic is " + topic);

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
        Log.d(TAG, "Start to unSubscribe" + topic);
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

        Log.d(TAG, "pub topic " + topic + message);
        // 发布主题
        mMqttConnection.publish(topic, message, mqttRequest);

    }

    public void checkFirmware() {

        mMqttConnection.initOTA(Environment.getExternalStorageDirectory().getAbsolutePath(), new TXOTACallBack() {
            @Override
            public void onReportFirmwareVersion(int resultCode, String version, String resultMsg) {
                TXLog.e(TAG, "onReportFirmwareVersion:" + resultCode + ", version:" + version + ", resultMsg:" + resultMsg);
            }

            @Override
            public void onDownloadProgress(int percent, String version) {
                TXLog.e(TAG, "onDownloadProgress:" + percent);
            }

            @Override
            public void onDownloadCompleted(String outputFile, String version) {
                TXLog.e(TAG, "onDownloadCompleted:" + outputFile + ", version:" + version);

                mMqttConnection.reportOTAState(TXOTAConstansts.ReportState.DONE, 0, "OK", version);
            }

            @Override
            public void onDownloadFailure(int errCode, String version) {
                TXLog.e(TAG, "onDownloadFailure:" + errCode);

                mMqttConnection.reportOTAState(TXOTAConstansts.ReportState.FAIL, errCode, "FAIL", version);
            }
        });
        mMqttConnection.reportCurrentFirmwareVersion("0.0.1");
    }
}
