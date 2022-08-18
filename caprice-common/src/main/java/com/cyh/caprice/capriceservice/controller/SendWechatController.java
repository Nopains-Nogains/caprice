package com.cyh.caprice.capriceservice.controller;

import com.cyh.caprice.capriceservice.pojo.WeChatMessage;
import com.cyh.caprice.capriceservice.service.SendWechatLogService;
import com.cyh.caprice.capriceservice.util.JwtUtil;
import com.cyh.caprice.capriceservice.util.LimitAccessTimes;
import com.cyh.caprice.capriceservice.util.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:25 2021/10/9
 **/
@Log4j2
@Controller
@RequestMapping("/SendWechat")
public class SendWechatController {

    @Autowired
    private SendWechatLogService sendWechatLogService;

    @RequestMapping(value = "/SendWechatMessage", method = RequestMethod.POST)
    @ResponseBody
    public void SendWechatMessage(@RequestBody WeChatMessage weChatMessage) {
        log.info("--------发送微信推送-------");
        sendWechatLogService.SendWechatMessage(weChatMessage);
    }

    /**
     * @Author: cuiyuhao
     * @Description: 获取请求身份token
     * @Data: Created in 2021/10/12-10:19
     * @Return: Message
     **/
    @LimitAccessTimes
    @RequestMapping(value = "/getAutograph", method = RequestMethod.POST)
    @ResponseBody
    public Message getAutograph() {
        Message message = new Message();
        Map<String, Object> map = new HashMap<String, Object>();
        String token = JwtUtil.createToken("jsc", map);
        message.setCode("00000");
        message.setStatus(true);
        message.setMessage("生成Token成功，有效期3小时");
        message.setData(token);
        return message;
    }


}
