package com.cyh.caprice.capriceservice.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cyh.caprice.capriceservice.pojo.WeChatMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author: cuiyuhao
 * @Description: 通过微信公众号推送消息给管理员，提醒管理员进行业务操作
 * 消息接收方必须先关注消息发送方公众服务号
 * @Data: Created in 2021/10/8-9:17
 **/
@Log4j2
@Component
public class WeChatMessageUtil {

    @Autowired
    private RedisUtil redisUtils;
    //创建新的静态参数
    private static RedisUtil redisUtil;

    @PostConstruct
    public void init() { //静态变量初始化的时候赋值
        redisUtil = this.redisUtils;
    }

    //从配置文件中获取相关参数
    private static String appId;
    private static String secret;

    @Value("${weChat.appId}")
    public void setAppId(String appId) {
        WeChatMessageUtil.appId = appId;
    }

    @Value("${weChat.secret}")
    public void setSecret(String secret) {
        WeChatMessageUtil.secret = secret;
    }

    /**
     * @Author: cuiyuhao
     * @Description: 发送微信推送主方法
     * @Data: Created in 2021/10/8-9:16
     * @Param: weChatMessage 参数实体
     * @Return: String
     **/
    public static Message sendWeChatMess(WeChatMessage weChatMessage) {
        Message message = new Message();
        if ("".equals(weChatMessage.getProjectName())) {
            message.setStatus(false);
            message.setCode("00001");
            message.setMessage("发送失败!,项目名不能为空");
            return message;
        }
        if ("".equals(weChatMessage.getTemplateId())) {
            message.setStatus(false);
            message.setCode("00002");
            message.setMessage("发送失败!,模板Id不能为空");
            return message;
        }
        if ("".equals(weChatMessage.getTouser())) {
            message.setStatus(false);
            message.setCode("00003");
            message.setMessage("发送失败!,发送用户不能为空");
            return message;
        }
        if ("".equals(weChatMessage.getCallbackUrl())) {
            message.setStatus(false);
            message.setCode("00005");
            message.setMessage("发送失败!,回调接口不能为空");
            return message;
        }
        log.info("------------微信公众号推送开始-------", weChatMessage);
        //1.获取微信公众号的access_token
        String access_token = redisServiceByToken();

        //2.通过微信接口发送消息
        // https://developers.weixin.qq.com/doc/offiaccount/Subscription_Messages/api.html
        // 微信API文档地址 方法名：send发送订阅通知
        String sendMessUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(sendMessUrl);//创建post请求

        //3.装配post请求参数
        JSONObject json = setPostParam(weChatMessage);
        message.setStatus(false);
        message.setCode("00004");
        message.setMessage("发送失败");
        try {
            StringEntity Entity = new StringEntity(json.toJSONString(), "UTF-8");
            //设置post求情参数
            httpPost.setEntity(Entity);
            log.info("------------通过微信接口推送消息-------");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 发送成功
                String resutlEntity = EntityUtils.toString(httpResponse.getEntity());
                message = JSONObject.parseObject(resutlEntity, Message.class);
                if (message.getErrcode().equals("0")) {
                    message.setStatus(true);
                    message.setCode("00000");
                    message.setMessage("推送成功");
                }
                //判断失败原因
                if (message.getErrcode().equals("40003")) {
                    message.setStatus(false);
                    message.setCode("40003");
                    message.setMessage("touser字段openid不正确");
                }
                if (message.getErrcode().equals("40037")) {
                    message.setStatus(false);
                    message.setCode("40037");
                    message.setMessage("订阅模板id不正确");
                }
                if (message.getErrcode().equals("41030")) {
                    message.setStatus(false);
                    message.setCode("41030");
                    message.setMessage("page不正确");
                }
                if (message.getErrcode().equals("45009")) {
                    message.setStatus(false);
                    message.setCode("45009");
                    message.setMessage("接口调用超过限额（目前默认每个帐号日调用限额为2000）");
                }
                log.info("------------微信公众号推送结束-------", message);
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("通过微信接口推送消息失败：", e);
        } finally {
            try {
                if (httpClient != null) {
                    // 释放资源
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("------------微信公众号推送结束-------", message);
        return message;
    }

    /**
     * @Author: cuiyuhao
     * @Description: 根据redis获取token
     * @Data: Created in 2021/10/9-11:16
     * @Return: String
     **/
    public static String redisServiceByToken() {
        // 连接本地的 Redis 服务
        //获取token
        if (redisUtil.get("access_token") == null) {
            String token = getAccessToken();
            redisUtil.set("access_token", token, 1200);
            return token;
        } else {
            String token = (String) redisUtil.get("access_token");
            return token;
        }
    }


    /**
     * @Author: cuiyuhao
     * @Description: 获取token
     * @Data: Created in 2021/10/8-9:18
     * @Param:
     * @Return: String
     **/
    public static String getAccessToken() {
        log.info("------------获取微信公众号的access_token开始-------");
        // https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
        // 微信API文档地址 方法名：auth.getAccessToken
        String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + secret + "";
        CloseableHttpClient httpCilent = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getTokenUrl);
        try {
            HttpResponse httpResponse = httpCilent.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String entity = EntityUtils.toString(httpResponse.getEntity());
                HashMap map = JSON.parseObject(entity, HashMap.class);
                if (map.get("access_token") != null) {
                    return map.get("access_token").toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("------------获取access_token接口异常:", e);
        } finally {
            try {
                // 释放资源
                httpCilent.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("------------获取微信公众号的access_token结束-------");
        return "";
    }

    /**
     * @Author: cuiyuhao
     * @Description: 装配post请求参数
     * @Data: Created in 2021/10/8-9:56
     * @Param: weChatMessage
     * @Return: JSONObject
     **/
    public static JSONObject setPostParam(WeChatMessage weChatMessage) {
        log.info("------------装配post请求参数开始-------");
        JSONObject json = new JSONObject();
        json.put("touser", weChatMessage.getTouser());//接收者（用户）的 openid
        json.put("template_id", weChatMessage.getTemplateId());//template_id: 所需下发的订阅模板id
        if (weChatMessage.getTemplateId().equals("_DJJ3X3bpUMBkrX6veLrM47dLZSh1g1EjeP5gNq68hM")) {
            json.put("url", weChatMessage.getUrl() + "/PZ.jsp?id=" + weChatMessage.getOrderId());//模板里详情页链接
        } else {
            json.put("url", weChatMessage.getUrl());//模板里详情页链接
        }
        //数据插入
        JSONObject dataJson = new JSONObject();
        for (int i = 0; i < weChatMessage.getFillData().length; i++) {
            JSONObject sonDateJson = new JSONObject();
            sonDateJson.put("value", weChatMessage.getFillData()[i]);
            dataJson.put("keyword" + (i + 1), sonDateJson);
        }
        JSONObject firstValue = new JSONObject();
        firstValue.put("value", weChatMessage.getTitle());
        dataJson.put("first", firstValue);
        JSONObject remarkValue = new JSONObject();
        remarkValue.put("value", weChatMessage.getRemark());
        dataJson.put("remark", remarkValue);
        json.put("data", dataJson);
        log.info("------------装配post请求参数结束-------");
        return json;
    }


    public static void main(String[] args) {
        WeChatMessage weChatMessage = new WeChatMessage();
        weChatMessage.setTitle("有订单待处理");
        weChatMessage.setTouser("oBtwJ69Gg4vI3wboQL1Ody_tp6ak");
        weChatMessage.setUrl("http://chajianshi.cn/jsp/login.jsp");
        weChatMessage.setTemplateId("5CoDmEQZt31IbL1iC2OHi3Lv8DejenAn-H3wyEbAyH4");
        weChatMessage.setRemark("请尽快处理订单！");
        String[] arr = new String[4];
        //1.订单状态
        arr[0] = "支付待审核";
        //2.用户姓名
        arr[1] = "崔宇浩";
        //3.时间
        arr[2] = DateUtils.getCurrentTime();
        //4.订单号
        arr[3] = "202110080001";
        weChatMessage.setFillData(arr);
        System.out.println(sendWeChatMess(weChatMessage));
    }
}
