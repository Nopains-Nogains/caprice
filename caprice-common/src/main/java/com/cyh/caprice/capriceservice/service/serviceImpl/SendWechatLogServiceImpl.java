package com.cyh.caprice.capriceservice.service.serviceImpl;


import com.alibaba.fastjson.JSONObject;
import com.cyh.caprice.capriceservice.dao.SendWechatLogMapper;
import com.cyh.caprice.capriceservice.pojo.SendWechatLog;
import com.cyh.caprice.capriceservice.pojo.WeChatMessage;
import com.cyh.caprice.capriceservice.service.SendWechatLogService;
import com.cyh.caprice.capriceservice.util.DateUtils;
import com.cyh.caprice.capriceservice.util.Message;
import com.cyh.caprice.capriceservice.util.WeChatMessageUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:14 2021/10/9
 **/
@Service
@Log4j2
public class SendWechatLogServiceImpl implements SendWechatLogService {
    @Autowired
    private SendWechatLogMapper sendWechatLogMapper;

    @Override
    /**
     * @Author: cuiyuhao
     * @Description: 微信公众号推送接口
     * @Data: Created in 2021/10/11-11:21
     * @Param: WeChatMessage
     * @Return: Message
     **/
    @Async
    public void SendWechatMessage(@RequestBody WeChatMessage weChatMessage) {
        //调用微信推送工具类
        Message message = WeChatMessageUtil.sendWeChatMess(weChatMessage);
        //记录推送日志
        SendWechatLog sendWechatLog = new SendWechatLog();
        sendWechatLog.setProjectName(weChatMessage.getProjectName().isEmpty() ? "null" :weChatMessage.getProjectName());
        sendWechatLog.setOpenId(weChatMessage.getTouser().isEmpty() ? "null" : weChatMessage.getTouser());
        sendWechatLog.setRequestState(String.valueOf(message.isStatus()));
        sendWechatLog.setRequestTime(DateUtils.getCurrentTime());
        sendWechatLog.setRequestInformation(message.getMessage());
        sendWechatLog.setTitle(weChatMessage.getTitle().isEmpty() ? "null" :weChatMessage.getTitle());
        sendWechatLog.setCallbackUrl(weChatMessage.getCallbackUrl().isEmpty() ? "null" :weChatMessage.getCallbackUrl());
        sendWechatLog.setOrderId(weChatMessage.getOrderId());
        sendWechatLogMapper.insert(sendWechatLog);
        //调用回调接口
        String callbackUrl = weChatMessage.getCallbackUrl();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(callbackUrl);//创建post请求
        try {
            JSONObject json = new JSONObject();
            json.put("message", message.getMessage());
            json.put("code", message.getCode());
            StringEntity Entity = new StringEntity(json.toJSONString(), "UTF-8");
            Entity.setContentType("application/json");
            httpPost.setEntity(Entity);
            httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用回调接口异常：", e);
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
    }
}
