package fun.qianxiao.yunchu.config;

import android.text.TextUtils;

import com.blankj.utilcode.constant.TimeConstants;

import fun.qianxiao.yunchu.utils.MySpUtils;

public class AppConfig {
    //TAG
    public static final String APP_TAG = "YUNCHU_TAG";

    //友盟统计
    public static final String UMENG_APP_ID = "60a10964c9aacd3bd4d6083e";
    public static final String UMENG_CHANNEL = "android";

    //域名
    public static final String APP_HOST = "https://wd.cn.ecsxs.com";
    //检查更新配置
    public static final String CHECKUPDATE_URL = "http://qianxiao.fun/app/yunchu/updateConfig.json";
    //隐私协议地址
    public static final String PRIVACYAGREEMENT_URL = "http://qianxiao.fun/app/yunchu/PrivacyPolicy.html";
    public static final String USERAGREEMENT_URL = "http://qianxiao.fun/app/yunchu/UserAgreement.html";
    public static final String REQUEST_TOKEN = "INmvNS86U6zHik7RFKlZGDl5wLvWNQqO";

    public static final String INVITE_CODE = "EI-447027";
    public static final String LOGIN_EXPIRE_TIME = String.valueOf(3 * TimeConstants.DAY/1000 - 1);

}
