package com.cyh.caprice.capriceservice.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyh.caprice.capriceservice.pojo.SendWechatLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author: cuiyuhao
 * @Description:
 * @Data: Created in 15:06 2021/10/9
 **/
@Repository
@Mapper
public interface SendWechatLogMapper extends BaseMapper<SendWechatLog> {
}
