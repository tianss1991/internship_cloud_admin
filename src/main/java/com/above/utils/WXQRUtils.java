package com.above.utils;

import com.above.config.wechat.WeChatConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信生成二维码工具
 */
public class WXQRUtils {

    private static final String APPID = WeChatConfig.APPID;
    private static final String APPSECRECT = WeChatConfig.APPSECRECT;
    private static final String PAGE = WeChatConfig.PAGE;
    private static final String ENV_VERSION = WeChatConfig.ENV_VERSION;

    /**
     * @param sence 二维码携带参数
     * @return 通用返回，200-成功返回map，500失败返回提醒
     */
    public static CommonResult<Object> createQrUrl(String sence){
        /*请求地址*/
        String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        String getUnlimitedUrl =  "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";

        /*获取微信请求的凭证*/
        String requestUrl = accessTokenUrl.replace("APPID", APPID).replace("APPSECRET", APPSECRECT);
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
        if (jsonObject != null) {
            try {
                //错误码
                String errcode = "errcode";
                // 业务操作
                if (!StringUtils.isBlank(jsonObject.getString(errcode))) {
                    //判断状态码
                    switch (jsonObject.getString("errcode")) {
                        case "-1":
                            return CommonResult.error(500, "系统繁忙，请稍候再试");
                        case "40001":
                            return CommonResult.error(500, "AppSecret不合法,请重新获取");
                        case "40013":
                            return CommonResult.error(500, "AppID不合法,请重新获取");
                        default:
                            return CommonResult.error(500, "未知错误");
                    }
                }
                String accessToken = jsonObject.getString("access_token");
                String qrUrl = getUnlimitedUrl.replace("ACCESS_TOKEN",accessToken);
                Map<String,Object> map = new HashMap<>(16);
                map.put("page",PAGE);
                //配置后无需发布即可跳转
                map.put("check_path",false);
                map.put("env_version",ENV_VERSION);
                map.put("scene",sence);
                JSONObject json = (JSONObject) JSONObject.toJSON(map);
                String fileName = MyStringUtils.uuId(16) + ".png";
                String url = CommonUtil.getServerUrl(qrUrl, json.toJSONString(),fileName);
                if (StringUtils.isBlank(url)){
                    return CommonResult.error(500,"生成小程序码失败");
                }else{
                    Map<String, Object> returnMap = new HashMap<>(16);

                    returnMap.put("fileName",fileName);
                    returnMap.put("url",url);

                    return CommonResult.success(returnMap);
                }
            }catch (Exception e){
                e.printStackTrace();
                return CommonResult.error(500,"生成失败");
            }
        }else {
            return CommonResult.error(500,"获取AK失败");
        }
    }
}
