package com.qcloud.iot.samples;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.qcloud.iot.R;
import com.qcloud.iot.common.Status;
import com.qcloud.iot.mqtt.TXMqttActionCallBack;
import com.qcloud.iot.samples.mqtt.MQTTRequest;
import com.qcloud.iot.samples.mqtt.MQTTSample;
import com.qcloud.iot.util.TXLog;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class IoTMqttFragment extends Fragment {

    private static final String TAG = "TXMQTT";

    private IoTMainActivity mParent;

    private MQTTSample mMQTTSample;

    private Button mConnectBtn;

    private Button mCloseConnectBtn;

    private Button mSubScribeBtn;

    private Button mUnSubscribeBtn;

    private Button mPublishBtn;

    private Button mCheckFirmwareBtn;

    private TextView mLogInfoText;

    private Spinner mSpinner;

    private Button mSubdevOnlineBtn;
    private Button mSubdevOfflineBtn;

    private EditText mItemText;

    // Default testing parameters
    private String mBrokerURL = "ssl://iotcloud-mqtt.gz.tencentdevices.com:8883";
    private String mProductID = "PRODUCT-ID";
    private String mDevName = "DEVICE-NAME";
    private String mDevPSK = "DEVICE-SECRET";
    private String mSubProductID = "SUBDEV_PRODUCT-ID";
    private String mSubDevName = "SUBDEV_DEV-NAME";
    private String mTestTopic = "TEST_TOPIC_WITH_SUB_PUB";

    private final static String BROKER_URL = "broker_url";
    private final static String PRODUCT_ID = "product_id";
    private final static String DEVICE_NAME = "dev_name";
    private final static String DEVICE_PSK = "dev_psk";
    private final static String SUB_PRODUCID = "sub_prodid";
    private final static String SUB_DEVNAME = "sub_devname";
    private final static String TEST_TOPIC  = "test_topic";


    private AtomicInteger temperature = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iot_mqtt, container, false);

        mParent = (IoTMainActivity) this.getActivity();

//        mMQTTSample = new MQTTSample(this.getActivity(), new SelfMqttActionCallBack());
        mConnectBtn = view.findViewById(R.id.connect);
        mCloseConnectBtn = view.findViewById(R.id.close_connect);
        mSubScribeBtn = view.findViewById(R.id.subscribe_topic);
        mUnSubscribeBtn = view.findViewById(R.id.unSubscribe_topic);
        mPublishBtn = view.findViewById(R.id.publish_topic);
        mCheckFirmwareBtn = view.findViewById(R.id.check_firmware);

        mLogInfoText = view.findViewById(R.id.log_info);

        mSpinner = view.findViewById(R.id.spinner4);
        mItemText = view.findViewById(R.id.editText2);

        mSubdevOnlineBtn = view.findViewById(R.id.subdev_online);
        mSubdevOfflineBtn = view.findViewById(R.id.subdev_offline);


        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                   String[] items = getResources().getStringArray(R.array.setup_items);
                                                   String paraStr = mItemText.getText().toString();

                                                   if (position == 0) {
                                                       return;
                                                   }

                                                   if (paraStr.equals("")) {
                                                       return;
                                                   }

                                                   Log.d("TXMQTT", "Set " + items[position] + " to " + paraStr);
                                                   Toast toast = Toast.makeText(mParent, "Set " + items[position] + " to " + paraStr, Toast.LENGTH_LONG);
                                                   toast.show();
                                                   SharedPreferences sharedPreferences =  mParent.getSharedPreferences("config",Context.MODE_PRIVATE);
                                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                                   switch(position) {
                                                       case 1:
                                                           mBrokerURL = paraStr;
                                                           editor.putString(BROKER_URL, mBrokerURL);
                                                           break;
                                                       case 2:
                                                           mProductID = paraStr;
                                                           editor.putString(PRODUCT_ID, mProductID);
                                                       case 3:
                                                           mDevName = paraStr;
                                                           editor.putString(DEVICE_NAME, mDevName);
                                                           break;
                                                       case 4:
                                                           mDevPSK = paraStr;
                                                           editor.putString(DEVICE_PSK, mDevPSK);
                                                           break;
                                                       case 5:
                                                           mSubProductID = paraStr;
                                                           editor.putString(SUB_PRODUCID, mSubProductID);
                                                           break;
                                                       case 6:
                                                           mSubDevName = paraStr;
                                                           editor.putString(SUB_DEVNAME, mSubDevName);
                                                           break;
                                                       case 7:
                                                           mTestTopic = paraStr;
                                                           editor.putString(TEST_TOPIC, mTestTopic);
                                                           break;
                                                       default:
                                                           break;
                                                   }
                                                   editor.commit();
                                               }

                                               @Override
                                               public void onNothingSelected(AdapterView<?> parent) {

                                               }
                                           }

        );

         mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = mParent.getSharedPreferences("config", Context.MODE_PRIVATE);
                mBrokerURL = settings.getString(BROKER_URL, mBrokerURL);
                mProductID = settings.getString(PRODUCT_ID, mProductID);
                mDevName = settings.getString(DEVICE_NAME, mDevName);
                mDevPSK = settings.getString(DEVICE_PSK, mDevPSK);
                mSubProductID = settings.getString(SUB_PRODUCID, mSubProductID);
                mSubDevName = settings.getString(SUB_DEVNAME, mSubDevName);
                mTestTopic = settings.getString(TEST_TOPIC, mTestTopic);
                mMQTTSample = new MQTTSample(mParent, new SelfMqttActionCallBack(), mBrokerURL, mProductID,
                                    mDevName, mDevPSK, mSubProductID, mSubDevName, mTestTopic);

                mMQTTSample.connect();
            }
        });

        mCloseConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMQTTSample.disconnect();
            }
        });

        mSubScribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在腾讯云控制台增加自定义主题（权限为订阅和发布）：custom_data，用于接收IoT服务端转发的自定义数据。
                // 本例中，发布的自定义数据，IoT服务端会在发给当前设备。
                mMQTTSample.subscribeTopic("data");
            }
        });

        mSubdevOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMQTTSample.setSubdevOnline();
            }
        });

        mSubdevOfflineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMQTTSample.setSubDevOffline();
            }
        });

        mUnSubscribeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMQTTSample.unSubscribeTopic("data");
            }
        });

        mPublishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 要发布的数据
                Map<String, String> data = new HashMap<String, String>();
                // 车辆类型
                data.put("car_type", "suv");
                // 车辆油耗
                data.put("oil_consumption", "6.6");
                // 车辆最高速度
                data.put("maximum_speed", "205");
                // 温度信息
                data.put("temperature", String.valueOf(temperature.getAndIncrement()));

                // 需先在腾讯云控制台，增加自定义主题: data，用于更新自定义数据
                mMQTTSample.publishTopic("data", data);
            }
        });

        mCheckFirmwareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMQTTSample.checkFirmware();
            }
        });

        return view;
    }

    public void closeConnection() {
        mMQTTSample.disconnect();
    }

    /**
     * 实现TXMqttActionCallBack回调接口
     */
    private class SelfMqttActionCallBack extends TXMqttActionCallBack {

        @Override
        public void onConnectCompleted(Status status, boolean reconnect, Object userContext, String msg) {
            String userContextInfo = "";
            if (userContext instanceof MQTTRequest) {
                userContextInfo = userContext.toString();
            }
            String logInfo = String.format("onConnectCompleted, status[%s], reconnect[%b], userContext[%s], msg[%s]",
                    status.name(), reconnect, userContextInfo, msg);
            mParent.printLogInfo(TAG, logInfo, mLogInfoText, TXLog.LEVEL_INFO);
        }

        @Override
        public void onConnectionLost(Throwable cause) {
            String logInfo = String.format("onConnectionLost, cause[%s]", cause.toString());
            mParent.printLogInfo(TAG, logInfo, mLogInfoText, TXLog.LEVEL_INFO);
        }

        @Override
        public void onDisconnectCompleted(Status status, Object userContext, String msg) {
            String userContextInfo = "";
            if (userContext instanceof MQTTRequest) {
                userContextInfo = userContext.toString();
            }
            String logInfo = String.format("onDisconnectCompleted, status[%s], userContext[%s], msg[%s]", status.name(), userContextInfo, msg);
            mParent.printLogInfo(TAG, logInfo, mLogInfoText, TXLog.LEVEL_INFO);
        }

        @Override
        public void onPublishCompleted(Status status, IMqttToken token, Object userContext, String errMsg) {
            String userContextInfo = "";
            if (userContext instanceof MQTTRequest) {
                userContextInfo = userContext.toString();
            }
            String logInfo = String.format("onPublishCompleted, status[%s], topics[%s],  userContext[%s], errMsg[%s]",
                    status.name(), Arrays.toString(token.getTopics()), userContextInfo, errMsg);
            mParent.printLogInfo(TAG, logInfo, mLogInfoText);
        }

        @Override
        public void onSubscribeCompleted(Status status, IMqttToken asyncActionToken, Object userContext, String errMsg) {
            String userContextInfo = "";
            if (userContext instanceof MQTTRequest) {
                userContextInfo = userContext.toString();
            }
            String logInfo = String.format("onSubscribeCompleted, status[%s], topics[%s], userContext[%s], errMsg[%s]",
                    status.name(), Arrays.toString(asyncActionToken.getTopics()), userContextInfo, errMsg);
            if (Status.ERROR == status) {
                mParent.printLogInfo(TAG, logInfo, mLogInfoText, TXLog.LEVEL_ERROR);
            } else {
                mParent.printLogInfo(TAG, logInfo, mLogInfoText);
            }
        }

        @Override
        public void onUnSubscribeCompleted(Status status, IMqttToken asyncActionToken, Object userContext, String errMsg) {
            String userContextInfo = "";
            if (userContext instanceof MQTTRequest) {
                userContextInfo = userContext.toString();
            }
            String logInfo = String.format("onUnSubscribeCompleted, status[%s], topics[%s], userContext[%s], errMsg[%s]",
                    status.name(), Arrays.toString(asyncActionToken.getTopics()), userContextInfo, errMsg);
            mParent.printLogInfo(TAG, logInfo, mLogInfoText);
        }

        @Override
        public void onMessageReceived(final String topic, final MqttMessage message) {
            String logInfo = String.format("receive command, topic[%s], message[%s]", topic, message.toString());
            mParent.printLogInfo(TAG, logInfo, mLogInfoText);
        }
    }
}
