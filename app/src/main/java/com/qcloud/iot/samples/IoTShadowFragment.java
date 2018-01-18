package com.qcloud.iot.samples;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qcloud.iot.R;
import com.qcloud.iot.samples.shadow.ShadowSample;
import com.qcloud.iot.shadow.DeviceProperty;
import com.qcloud.iot.shadow.TXShadowActionCallBack;

import java.util.List;

public class IoTShadowFragment extends Fragment {

    private static final String TAG = IoTShadowFragment.class.getSimpleName();

    private IoTMainActivity mParent;

    private Button mConnectBtn;

    private Button mDocumentBtn;

    private Button mRegisterPropertyBtn;

    private Button mCloseConnectBtn;

    private Button mLoopBtn;

    private TextView mLogInfoText;

    private ShadowSample mShadowSample;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iot_shadow, container, false);
        init(view);
        return view;
    }

    /**
     * 初始化
     */
    private void init(final View view) {
        mParent = (IoTMainActivity) this.getActivity();
        mConnectBtn = view.findViewById(R.id.connect);
        mDocumentBtn = view.findViewById(R.id.get_device_document);
        mRegisterPropertyBtn = view.findViewById(R.id.register_property);
        mCloseConnectBtn = view.findViewById(R.id.close_connect);
        mLoopBtn = view.findViewById(R.id.loop);
        mLogInfoText = view.findViewById(R.id.log_info);

        mShadowSample = new ShadowSample(this, new ShadowActionCallBack());

        mDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShadowSample.getDeviceDocument();
            }
        });

        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShadowSample.connect();
            }
        });

        mRegisterPropertyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShadowSample.registerProperty();
            }
        });

        mCloseConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShadowSample.closeConnect();
            }
        });

        mLoopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShadowSample.loop();
            }
        });
    }

    private class ShadowActionCallBack extends TXShadowActionCallBack {
        @Override
        public void onRequestCallback(String type, int result, String document) {
            super.onRequestCallback(type, result, document);
            String logInfo = String.format("onRequestCallback, type[%s], result[%d], document[%s]", type, result, document);
            printLogInfo(TAG, logInfo);
        }

        @Override
        public void onDevicePropertyCallback(String propertyJSONDocument, List<DeviceProperty> devicePropertyList) {
            super.onDevicePropertyCallback(propertyJSONDocument, devicePropertyList);
            String logInfo = String.format("onDevicePropertyCallback, propertyJSONDocument[%s], deviceProperty[%s]",
                    propertyJSONDocument, devicePropertyList.toString());
            printLogInfo(TAG, logInfo);
            mShadowSample.updateDeviceProperty(propertyJSONDocument, devicePropertyList);
        }
    }

    /**
     * 打印日志
     *
     * @param tag
     * @param logInfo
     */
    public void printLogInfo(final String tag, final String logInfo) {
        mParent.printLogInfo(tag, logInfo, mLogInfoText);
    }
}
