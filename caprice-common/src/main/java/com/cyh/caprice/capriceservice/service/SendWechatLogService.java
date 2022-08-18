package com.cyh.caprice.capriceservice.service;

import com.cyh.caprice.capriceservice.pojo.WeChatMessage;
import org.springframework.stereotype.Service;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:11 2021/10/9
 **/
@Service
public interface SendWechatLogService {
    void SendWechatMessage(WeChatMessage weChatMessage);
}
