package com.qcloud.iot.samples;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qcloud.iot.R;
import com.qcloud.iot.util.TXLog;

public class IoTMainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;

    private IoTMqttFragment mMqttFragment;

    private IoTRemoteServiceFragment mRemoteServiceFragment;

    private IoTShadowFragment mShadowFragment;

    private IoTEntryFragment mEntryFragment;

    private Button btnMqtt;

    private Button btnShadow;

    private Button btnRemoteService;

    private Button btnEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_main);
        initComponent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 设置默认的Fragment
        setDefaultFragment();
    }

    /**
     * 设置默认的Fragment
     */
    private void setDefaultFragment() {
        handlerButtonClick(R.id.btn_basic_function);
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        // 初始化控件
        btnShadow = (Button) findViewById(R.id.btn_shadow);
        btnRemoteService = (Button) findViewById(R.id.btn_remote_service);
        btnEntry = (Button) findViewById(R.id.btn_entry_demo);
        btnMqtt = (Button) findViewById(R.id.btn_basic_function);

        btnShadow.setOnClickListener(this);
        btnRemoteService.setOnClickListener(this);
        btnEntry.setOnClickListener(this);
        btnMqtt.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        handlerButtonClick(v.getId());
    }

    /**
     * 处理tab点击事件
     *
     * @param id
     */
    private void handlerButtonClick(int id) {
        // 重置按钮状态
        resetButton(id);
        // 开启Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 隐藏Fragment
        hideFragments(transaction);

        switch (id) {

            case R.id.btn_basic_function:
                if (mMqttFragment == null) {
                    mMqttFragment = new IoTMqttFragment();
                    transaction.add(R.id.fragment_content, mMqttFragment);
                } else {
                    transaction.show(mMqttFragment);
                }
                break;

            case R.id.btn_shadow:

                if (mShadowFragment == null) {
                    mShadowFragment = new IoTShadowFragment();
                    transaction.add(R.id.fragment_content, mShadowFragment);
                } else {
                    transaction.show(mShadowFragment);
                }
                break;

            case R.id.btn_remote_service:

                if (mRemoteServiceFragment == null) {
                    mRemoteServiceFragment = new IoTRemoteServiceFragment();
                    transaction.add(R.id.fragment_content, mRemoteServiceFragment);
                } else {
                    transaction.show(mRemoteServiceFragment);
                }
                break;

            case R.id.btn_entry_demo:

                if (mEntryFragment == null) {
                    mEntryFragment = new IoTEntryFragment();
                    transaction.add(R.id.fragment_content, mEntryFragment);
                } else {
                    transaction.show(mEntryFragment);
                }
                break;

        }
        // 事务提交
        transaction.commit();

    }


    /**
     * 重置button状态
     */
    private void resetButton(int id) {
        switch (id) {
            case R.id.btn_basic_function:
                btnMqtt.setBackgroundColor(Color.LTGRAY);
                btnEntry.setBackgroundColor(Color.WHITE);
                btnShadow.setBackgroundColor(Color.WHITE);
                btnRemoteService.setBackgroundColor(Color.WHITE);
                break;

            case R.id.btn_entry_demo:
                btnEntry.setBackgroundColor(Color.LTGRAY);
                btnMqtt.setBackgroundColor(Color.WHITE);
                btnShadow.setBackgroundColor(Color.WHITE);
                btnRemoteService.setBackgroundColor(Color.WHITE);
                break;

            case R.id.btn_shadow:
                btnShadow.setBackgroundColor(Color.LTGRAY);
                btnMqtt.setBackgroundColor(Color.WHITE);
                btnEntry.setBackgroundColor(Color.WHITE);
                btnRemoteService.setBackgroundColor(Color.WHITE);
                break;

            case R.id.btn_remote_service:
                btnRemoteService.setBackgroundColor(Color.LTGRAY);
                btnMqtt.setBackgroundColor(Color.WHITE);
                btnShadow.setBackgroundColor(Color.WHITE);
                btnEntry.setBackgroundColor(Color.WHITE);
                break;
        }
    }

    /**
     * 隐藏Fragment
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (null != mMqttFragment) {
            transaction.hide(mMqttFragment);
        }
        if (null != mRemoteServiceFragment) {
            transaction.hide(mRemoteServiceFragment);
        }
        if (null != mShadowFragment) {
            transaction.hide(mShadowFragment);
        }
        if (null != mEntryFragment) {
            transaction.hide(mEntryFragment);
        }
    }

    /**
     * 打印日志信息
     *
     * @param logInfo
     */
    protected void printLogInfo(final String tag, final String logInfo, final TextView textView, int logLevel) {
        switch (logLevel) {
            case TXLog.LEVEL_DEBUG:
                TXLog.d(tag, logInfo);
                break;

            case TXLog.LEVEL_INFO:
                TXLog.i(tag, logInfo);
                break;

            case TXLog.LEVEL_ERROR:
                TXLog.e(tag, logInfo);
                break;

            default:
                TXLog.d(tag, logInfo);
                break;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(logInfo + "\n");
            }
        });
    }

    /**
     * 打印日志信息
     *
     * @param logInfo
     */
    protected void printLogInfo(final String tag, final String logInfo, final TextView textView) {
        printLogInfo(tag, logInfo, textView, TXLog.LEVEL_DEBUG);
    }

}
