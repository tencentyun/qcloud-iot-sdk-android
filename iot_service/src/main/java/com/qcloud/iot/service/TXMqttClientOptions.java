package com.qcloud.iot.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * mqtt远程客户端选项
 */

public class TXMqttClientOptions implements Parcelable {

    /**
     * 服务器URI
     */
    private String mServerURI;

    /**
     * Iot Hub控制台获取产品ID
     */
    private String mProductId;

    /**
     * 设备名，唯一
     */
    private String mDeviceName;

    public static final Creator<TXMqttClientOptions> CREATOR = new Creator<TXMqttClientOptions>() {
        @Override
        public TXMqttClientOptions createFromParcel(Parcel in) {
            return new TXMqttClientOptions(in);
        }

        @Override
        public TXMqttClientOptions[] newArray(int size) {
            return new TXMqttClientOptions[size];
        }
    };

    public TXMqttClientOptions() {
    }

    public TXMqttClientOptions(String serverURI, String productId, String deviceName) {
        this.mServerURI = serverURI;
        this.mProductId = productId;
        this.mDeviceName = deviceName;
    }

    protected TXMqttClientOptions(Parcel in) {
        mServerURI = in.readString();
        mProductId = in.readString();
        mDeviceName = in.readString();
    }


    public String getServerURI() {
        return mServerURI;
    }

    public TXMqttClientOptions serverURI(String mServerURI) {
        this.mServerURI = mServerURI;
        return this;
    }

    public String getProductId() {
        return mProductId;
    }

    public TXMqttClientOptions productId(String productId) {
        this.mProductId = productId;
        return this;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public TXMqttClientOptions deviceName(String mDeviceName) {
        this.mDeviceName = mDeviceName;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mServerURI);
        parcel.writeString(mProductId);
        parcel.writeString(mDeviceName);
    }

    @Override
    public String toString() {
        return "TXMqttClientOptions{" +
                "mServerURI='" + mServerURI + '\'' +
                ", mProductId='" + mProductId + '\'' +
                ", mDeviceName='" + mDeviceName + '\'' +
                '}';
    }
}
