package uni.android.cn.oaid;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.github.gzuliyujiang.oaid.IRegisterCallback;
import com.taobao.weex.WXEnvironment;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

@SuppressWarnings("unused")
public class AndroidCNOAID extends UniModule {

    private static final Application application = WXEnvironment.getApplication();

    /**
     * 获取当前集成的 Android_CN_OAID 版本号
     */
    @UniJSMethod(uiThread = false)
    public String getVersion() {
        return BuildConfig.SDK_VERSION;
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId}及{@link #getOAID}，请不要调用这个方法
     *
     * @param tryWidevine 是否尝试WidevineID，由于兼容问题，IMEI/MEID及OAID获取失败后默认不尝试获取WidevineID
     * @param callback    UNI对象
     */
    @UniJSMethod(uiThread = false)
    public void register(int tryWidevine, UniJSCallback callback) {
        DeviceIdentifier.register(application, tryWidevine == 1, new IRegisterCallback() {
            /**
             * 启动时注册完成回调，
             *
             * @param clientId 客户端标识按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID。
             * @param error    OAID获取失败时的异常信息
             */
            @Override
            public void onComplete(String clientId, Exception error) {
                if (callback != null) {
                    JSONObject data = new JSONObject();
                    data.put("isSuccess", !TextUtils.isEmpty(clientId));
                    data.put("clientId", clientId);
                    data.put("error", error != null ? error.getMessage() : "");
                    callback.invoke(data);
                }
            }
        });
    }

    /**
     * 使用该方法获取客户端唯一标识，需要先调用{@link #register}预取
     *
     * @param returnRaw 返回的是否是原始值
     * @return 客户端唯一标识，可能是IMEI/MEID、OAID/AAID、AndroidID或GUID中的一种
     * @see #register
     * @see DeviceID#getClientId()
     * @see DeviceID#getClientIdMD5()
     * @see DeviceID#getClientIdSHA1()
     */
    @UniJSMethod(uiThread = false)
    public static String getClientId(int returnRaw) {
        return DeviceIdentifier.getClientId(returnRaw == 1);
    }

    /**
     * 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI/MEID，Android 10+ 非系统应用则不再允许获取 IMEI。
     * <pre>
     *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * </pre>
     *
     * @return IMEI或MEID，可能为空
     */
    @UniJSMethod(uiThread = false)
    public String getIMEI() {
        return DeviceIdentifier.getIMEI(application);
    }

    /**
     * 使用该方法获取OAID/AAID，需要先在{@link Application#onCreate()}里调用{@link #register}预取
     *
     * @see #register
     */
    @UniJSMethod(uiThread = false)
    public String getOAID() {
        return DeviceIdentifier.getOAID(application);
    }

    /**
     * 获取AndroidID
     *
     * @return AndroidID，可能为空
     */
    @UniJSMethod(uiThread = false)
    public String getAndroidID() {
        return DeviceIdentifier.getAndroidID(application);
    }

    /**
     * 获取数字版权管理设备ID
     *
     * @return WidevineID，可能为空
     * @deprecated 很鸡肋，不推荐使用了，因为在某些手机上调用会莫名其妙的造成闪退或卡死，还难以排查到原因
     */
    @UniJSMethod(uiThread = false)
    @Deprecated
    public static String getWidevineID() {
        return DeviceIdentifier.getWidevineID();
    }

    /**
     * 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
     *
     * @return 伪造的设备标识，不会为空，但会有一定的概率出现重复
     */
    @UniJSMethod(uiThread = false)
    public String getPseudoID() {
        return DeviceIdentifier.getPseudoID();
    }

    /**
     * 随机生成全局唯一标识并存到{@code SharedPreferences}、{@code ExternalStorage}及{@code SystemSettings}。
     * 为保障在Android10以下版本上的稳定性，需要加入权限{@code WRITE_EXTERNAL_STORAGE}及{@code WRITE_SETTINGS}。
     * <pre>
     *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *         tools:ignore="ScopedStorage" />
     *     <uses-permission
     *         android:name="android.permission.WRITE_SETTINGS"
     *         tools:ignore="ProtectedPermissions" />
     * </pre>
     *
     * @return GUID，不会为空，但应用卸载后会丢失
     * @see android.provider.Settings#ACTION_MANAGE_WRITE_SETTINGS
     */
    @UniJSMethod(uiThread = false)
    public String getGUID() {
        return DeviceIdentifier.getGUID(application);
    }

}
