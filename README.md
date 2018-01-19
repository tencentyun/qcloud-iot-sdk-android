# 腾讯物联云SDK
腾讯物联云 SDK 依靠安全且性能强大的数据通道，为物联网领域开发人员提供终端(如传感器, 执行器, 嵌入式设备或智能家电等等)和云端的双向通信能力。

# 快速开始
本节将讲述如何在腾讯物联云控制台申请设备, 并结合本 SDK 快速体验设备通过 MQTT+TLS/SSL 协议连接到腾讯云, 发送和接收消息。

## 一. 控制台创建设备

#### 1. 注册/登录腾讯云账号
访问[腾讯云登录页面](https://cloud.tencent.com/login?s_url=https%3A%2F%2Fcloud.tencent.com%2F), 点击[立即注册](https://cloud.tencent.com/register?s_url=https%3A%2F%2Fcloud.tencent.com%2F), 免费获取腾讯云账号，若您已有账号，可直接登录。

#### 2. 访问物联云控制台
登录后点击右上角控制台，进入控制台后, 鼠标悬停在云产品上, 弹出层叠菜单。

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/{0603FE05-A96A-41E4-A0B8-AA2D9200928A}.png
)

点击物联云，或直接访问[物联云控制台](https://console.qcloud.com/iotcloud)

#### 3. 创建产品和设备
点击页面**创建新产品**按钮, 创建一个品类, 然后在下方**产品名称**一栏中点击刚刚创建好的产品进入产品设置页面，可在产品设置页面得到 **productID**，并可编辑产品名称及描述，之后再**设备列表**页面新建设备，设备名称在该产品下唯一。

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/iot_15157295174920.png
)

创建设备成功后，**需保存好设备私钥**，腾讯云后台不存储设备私钥，点击**管理**，得到**设备证书**（用于非对称加密连接）。

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/iot_15157296439268.png)

#### 4. 创建可订阅可发布的Topic

按照**第三步**中进入产品设置页面的方法进入页面后, 点击权限列表，再点击**定义 Topic 权限**, 输入 custom_data, 并设置为可订阅可发布权限，点击创建。

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/iot_15157461133250.png)

随后将会创建出 productName/${deviceName}/custom_data 的 Topic

## 二. 编译运行示例程序

#### 1. 下载SDK
运行如下命令从 github 克隆代码, 或者访问最新[下载](https://github.com/tencentyun/qcloud-iot-sdk-android/releases)地址, 将下载到的压缩包解压缩

```git clone https://github.com/tencentyun/qcloud-iot-sdk-android.git```

#### 2. 使用Android Studio打开工程
使用 Android Studio 导入 qcloud-iot-sdk-android/build.gradle 从而打开工程

#### 3. 填入设备信息
- 编辑 app/com.qcloud.iot.samples.mqtt.MQTTSample.java 文件中如下代码块, 填入之前创建产品和设备步骤中得到的 **PRODUCT_ID**, **DEVICE_NAME**，**DEVICE_CERT_NAME** 和 **DEVICE_KEY_NAME**。
- SDK 提供两种读取设备证书、私钥的接口：一是通过 AssetManager 进行读取，此时需在工程 **app/src/main** 路径下创建 **assets** 目录并将设备证书、私钥放置在该目录中；二是通过 InputStream 进行读取，此时需传入设备证书、私钥的全路径信息。

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/3F7DAD8A10D94F2992AF2AE8F12F6DEC.png)

#### 4. 运行
点击 Android Studio Run 'app' 按钮安装 Demo

#### 5. 连接MQTT
点击 Demo 中的【连接 MQTT 】按钮，观察 Demo 及 logcat 中日志信息，以下为 logcat 中日志信息：
```
com.qcloud.iot I/com.qcloud.iot.mqtt.TXMqttConnection: Start connecting to ssl://connect.iot.qcloud.com:8883
com.qcloud.iot D/IoTMqttFragment: onConnectCompleted, status[OK], reconnect[false], userContext[MQTTRequest{requestType='connect', requestId=0}], msg[connected to ssl://connect.iot.qcloud.com:8883]
```
#### 6. 订阅主题
点击 Demo 中的【订阅主题】按钮，观察 Demo 及 logcat 中日志信息，以下为 logcat 中日志信息：
```
com.qcloud.iot I/com.qcloud.iot.mqtt.TXMqttConnection: Starting subscribe topic: ******/******/custom_data
com.qcloud.iot D/IoTMqttFragment: onSubscribeCompleted, status[OK], topics[[******/******/custom_data]], userContext[MQTTRequest{requestType='subscribeTopic', requestId=1}], errMsg[subscribe success]
```
#### 7. 发布主题
点击 Demo 中的【发布主题】按钮，观察 Demo 及 logcat 中日志信息，以下为 logcat 中日志信息：
```
com.qcloud.iot I/com.qcloud.iot.mqtt.TXMqttConnection: Starting publish topic: ******/******/custom_data Message: {"temperature":"0","car_type":"suv","maximum_speed":"205","oil_consumption":"6.6"}
com.qcloud.iot D/IoTMqttFragment: onPublishCompleted, status[OK], topics[[******/******/custom_data]],  userContext[MQTTRequest{requestType='publishTopic', requestId=2}], errMsg[publish success]
```

#### 8. 观察消息下发
如下日志信息显示该消息因为是到达已被订阅的 Topic, 所以又被服务器原样推送到示例程序, 并进入相应的回调函数。以下为 logcat 中信息：
```
com.qcloud.iot I/com.qcloud.iot.mqtt.TXMqttConnection: Received topic: ******/******/custom_data, message: {"temperature":"0","car_type":"suv","maximum_speed":"205","oil_consumption":"6.6"}
com.qcloud.iot D/IoTMqttFragment: receive command, topic[******/******/custom_data], message[{"temperature":"0","car_type":"suv","maximum_speed":"205","oil_consumption":"6.6"}]
```

#### 9. 取消订阅主题
点击 Demo 中的【取消订阅主题】按钮，观察 Demo 及 logcat 中日志信息，以下为 logcat 中日志信息：
```
com.qcloud.iot I/com.qcloud.iot.mqtt.TXMqttConnection: Starting unsubscribe topic: ******/******/custom_data
com.qcloud.iot D/IoTMqttFragment: onUnSubscribeCompleted, status[OK], topics[[******/******/custom_data]], userContext[MQTTRequest{requestType='unSubscribeTopic', requestId=3}], errMsg[unsubscribe success]
```

#### 10. 断开MQTT连接
点击 Demo 中的【断开 MQTT 连接】按钮，观察 Demo 及 logcat 中日志信息，以下为 logcat 中日志信息：
```
com.qcloud.iot D/IoTMqttFragment: onDisconnectCompleted, status[OK], userContext[MQTTRequest{requestType='disconnect', requestId=4}], msg[disconnected to ssl://connect.iot.qcloud.com:8883]
```

#### 11. 观察控制台日志
可以登录物联云控制台, 点击左边导航栏中的**云日志**, 查看刚才上报的消息

![](http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/doc/iot_1515734324922.png)

## 三、集成方式
SDK 提供以下两种集成方式：
#### 1. 源码集成
从 [github](https://github.com/tencentyun/qcloud-iot-sdk-android) 上下载 SDK 源码，根据 IoT-SDK 运行方式分为以下两种情况：
- 若需要将 IoT-SDK 运行在 service 组件中，则需同时集成 iot_core、iot_service 两个 module (iot_service 依赖 iot_core);
- 若不需要将 IoT-SDK 运行在 service 组件中，则只需集成 iot_core module。

#### 2. gradle 集成
在 App 的 build.gradle 文件中增加 IoT-SDK 的依赖，根据 IoT-SDK 运行方式分为以下两种情况：
- 若需要将 IoT-SDK 运行在 service 组件中，则需同时依赖 iot-core、iot-service aar，当前版本号为 1.2.0:
```
dependencies {
    compile 'com.qcloud.iot:iot-core:1.2.0'
    compile 'com.qcloud.iot:iot-service:1.2.0'
}
```
- 若不需要将 IoT-SDK 运行在 service 组件中，则只需要依赖 iot-core:
```
dependencies {
    compile 'com.qcloud.iot:iot-core:1.2.0'
}
```

#关于SDK的更多使用方式及接口了解, 请访问[官方WiKi](https://github.com/tencentyun/qcloud-iot-sdk-android/wiki)