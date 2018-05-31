package com.qcloud.iot.samples.shadow;

import android.content.Context;
import android.os.Handler;

import com.qcloud.iot.common.Status;
import com.qcloud.iot.mqtt.TXMqttConstants;
import com.qcloud.iot.samples.IoTShadowFragment;
import com.qcloud.iot.shadow.DeviceProperty;
import com.qcloud.iot.shadow.TXShadowActionCallBack;
import com.qcloud.iot.shadow.TXShadowConnection;
import com.qcloud.iot.shadow.TXShadowConstants;
import com.qcloud.iot.util.AsymcSslUtils;
import com.qcloud.iot.util.TXLog;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class ShadowSample {

    private static final String TAG = ShadowSample.class.getSimpleName();

    /**
     * 产品名称
     */
    private static final String PRODUCT_ID = "YOUR_PRODUCT_ID";

    /**
     * 设备名称
     */
    private static final String DEVICE_NAME = "YOUR_DEVICE_NAME";


    /**
     * 密钥
     */
    private static final String SECRET_KEY = null;
	
    /**
     * 设备证书名
     */
    private static final String DEVICE_CERT_NAME = "YOUR_DEVICE_NAME_cert.crt";

    /**
     * 设备私钥文件名
     */
    private static final String DEVICE_KEY_NAME = "YOUR_DEVICE_NAME_private.key";

    private Context mContext;

    private IoTShadowFragment mParent;

    /**
     * shadow连接实例
     */
    private TXShadowConnection mShadowConnection;

    /**
     * shadow action 回调接口
     */
    private TXShadowActionCallBack mShadowActionCallBack;

    private AtomicInteger mUpdateCount = new AtomicInteger(0);

    private AtomicInteger mTemperatureDesire = new AtomicInteger(20);

    /**
     * 设备属性集（该变量必须为全局变量）
     */
    private List<DeviceProperty> mDevicePropertyList = null;

    private Handler mHandler;

    private boolean isConnected = false;

    public ShadowSample(IoTShadowFragment parent, TXShadowActionCallBack shadowActionCallBack) {
        this.mParent = parent;
        this.mContext = parent.getContext();
        this.mShadowActionCallBack = shadowActionCallBack;
        this.mHandler = new Handler();
        this.mDevicePropertyList = new ArrayList<>();
    }

    public void getDeviceDocument() {
        if (!isConnected) {
            return;
        }
        mShadowConnection.get(null);
    }

    public void connect() {
        TXLog.i(TAG, "connect");

        mShadowConnection = new TXShadowConnection(mContext, PRODUCT_ID, DEVICE_NAME, SECRET_KEY, mShadowActionCallBack);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(8);
        options.setKeepAliveInterval(240);
        options.setAutomaticReconnect(true);
        options.setSocketFactory(AsymcSslUtils.getSocketFactoryByAssetsFile(mContext, DEVICE_CERT_NAME, DEVICE_KEY_NAME));
        Status status = mShadowConnection.connect(options, null);
        mParent.printLogInfo(TAG, String.format("connect IoT completed, status[%s]", status));
        isConnected = true;
    }

    public void registerProperty() {
        if (!isConnected) {
            return;
        }

        DeviceProperty deviceProperty1 = new DeviceProperty();
        deviceProperty1.key("updateCount").data(String.valueOf(mUpdateCount.getAndIncrement())).dataType(TXShadowConstants.JSONDataType.INT);
        mShadowConnection.registerProperty(deviceProperty1);

        DeviceProperty deviceProperty2 = new DeviceProperty();
        deviceProperty2.key("temperatureDesire").data(String.valueOf(mTemperatureDesire.getAndIncrement())).dataType(TXShadowConstants.JSONDataType.INT);
        mShadowConnection.registerProperty(deviceProperty2);

        mDevicePropertyList.add(deviceProperty1);
        mDevicePropertyList.add(deviceProperty2);
    }

    public void closeConnect() {
        if (!isConnected) {
            return;
        }
        mShadowConnection.disConnect(null);
        isConnected = false;
    }

    public void updateDeviceProperty(String propertyJSONDocument, List<DeviceProperty> devicePropertyList) {
        if (!isConnected) {
            return;
        }
        mParent.printLogInfo(TAG, "update device property success and report null desired info");
        // 在确认delta更新后，调用reportNullDesiredInfo()接口进行上报
        mShadowConnection.reportNullDesiredInfo();
    }

    public void loop() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isConnected) {
                    return;
                }
                if (mShadowConnection.getConnectStatus() != TXMqttConstants.ConnectStatus.kConnected) {
                    TXLog.e(TAG, "mqtt connection is not connect!!!");
                    mHandler.postDelayed(this, 30 * 1000);
                    return;
                }

                for (DeviceProperty deviceProperty : mDevicePropertyList) {
                    if ("updateCount".equals(deviceProperty.mKey)) {
                        deviceProperty.data(String.valueOf(mUpdateCount.getAndIncrement()));
                    } else if ("temperatureDesire".equals(deviceProperty.mKey)) {
                        deviceProperty.data(String.valueOf(mTemperatureDesire.getAndIncrement()));
                    }
                }

                mParent.printLogInfo(TAG, "update device property");
                mShadowConnection.update(mDevicePropertyList, null);
                mHandler.postDelayed(this, 10 * 1000);
            }
        });
    }
}