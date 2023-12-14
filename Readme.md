# Android_CN_OAID

[gzu-liyujiang/Android_CN_OAID](https://github.com/gzu-liyujiang/Android_CN_OAID/) 的 UNI 版本插件工程

---

## 写在前面

由于 UNI 账号的验证等原因，没有进行完整的验证。其中离线打包自测可以正常调用，云打包自测可以正常调用。自定义基座本地运行时出现了同步后的`net.lingala.zip4j.ZipFile`类找不到的错误，就没有继续进行验证了。

本插件只是对源项目的简单封装，其中因为 uni 平台的原生插件参数不支持 bool 类型，所以源项目接口中对应的 bool 参数调整为 int 类型，`1->true | else->false`

插件源码在 `library` module 下，[直达链接](./library/src/main/java/uni/android/cn/oaid/AndroidCNOAID.java)，内有相关注释

插件的离线包在`release`目录下，[直达链接](./release/4.2.8/)，内有演示页、插件包、配置文件

## 方法说明

首先需要获取插件对象

```vue
var oaid = uni.requireNativePlugin("Android_CN_OAID")
```

### 注册

使用此插件时，需要在**用户同意隐私政策后**调用`register`方法进行注册

```
      // 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
			// * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
      oaid.register(0, (ret) => {
					if (ret.isSuccess) {
						this.showModal('注册成功，clientId: ' + ret.clientId)
					} else {
						this.showModal('注册失败，错误信息为: ' + ret.error)
					}
				})
```

### 获取各种ID

```vue
			// 使用该方法获取客户端唯一标识，需要先调用{@link #register}预取
      oaid.getClientId(1)

      // 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI/MEID，Android 10+ 非系统应用则不再允许获取 IMEI。
      oaid.getIMEI()

      // 使用该方法获取OAID/AAID，需要先在{@link Application#onCreate()}里调用{@link #register}预取
      oaid.getOAID()

      // 获取AndroidID
      oaid.getAndroidID()

      // 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
			// 不会为空，但会有一定的概率出现重复
      oaid.getPseudoID()

      // 随机生成全局唯一标识并存到{@code SharedPreferences}、{@code ExternalStorage}及{@code SystemSettings}。
			// GUID不会为空，但应用卸载后会丢失
       oaid.getGUID()
```

## 集成插件

uni 的原生插件集成分为`在线打包`与`离线打包`两种

### 在线打包

#### 复制插件

将 release 的`Android_CN_OAID.zip`文件解压到 uni 项目的`nativeplugins`目录下（不存在时需要创建）

#### 配置插件

1. 打开 uni 项目的`manifest.json`文件，在`App原生插件配置`选项卡中点击`选择本地插件`，并在弹出的对话框中勾选`Android_CN_OAID`后点击确认保存

2. 打开`manifest.json`文件的源码视图，在`android`节点下增加`"enableOAID" : false`的配置项。（UNI 的云打包默认配置的是 1.0.30 版本的 MSA_OAID_SDK，此版本调用时会出现错误，需要通过此配置移除默认的包）

3. 判断是否需要根据你的应用，替换当前插件中默认存在的`msa_oaid_sdk_1.0.25.aar`

#### 运行测试

原生插件需要运行自定义基座中才可进行调试，具体需要参照 UNI 文档

### 离线打包

离线打包仅提供演示页代码，需要开发者自行引入aar并配置原项目的依赖项

#### 添加依赖

1.复制 `release/version/Android_CN_OAID.zip`中的`android_cn_oaid-release.aar`文件

2.增加`jitpack.io`的 maven 仓库（按照 gradle 版本区分，修改项目的`build.gralde`或`settings.gradle`文件）

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

3.修改项目的`build.gradle`

```groovy
dependencies {
    // 原生插件
    implementation files('./libs/android_cn_oaid-release.aar')
    // 源项目
    implementation ("com.github.gzu-liyujiang:Android_CN_OAID:$sdkVer") {
        exclude group: 'com.huawei.hms', module: 'ads-identifier'
        exclude group: 'com.hihonor.mcs', module: 'ads-identifier'
    }
}
```

#### 配置插件

在`assets/dcloud_uniplugins.json`文件的`nativePlugins`节点下，增加插件配置

```json
"nativePlugins": [
    {
      "plugins": [
        {
          "type": "module",
          "name": "Android_CN_OAID",
          "class": "uni.android.cn.oaid.AndroidCNOAID"
        }
      ]
    }
  ]
```

#### 运行测试

**离线打包需要配置的`appid`、`dcloud_appkey`以及证书等，需要参照官方文档**
