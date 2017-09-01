package com.happylifeplat.transaction.tx.springcloud.sample.wechat.mapper;

import com.happylifeplat.transaction.tx.springcloud.sample.wechat.entity.Wechat;
import org.apache.ibatis.annotations.Insert;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 15:22
 * @since JDK 1.8
 */
public interface WechatMapper {


    @Insert("INSERT INTO wechat(name,amount,create_time) VALUES(#{name}, #{amount},#{createTime})")
    int save(Wechat wechat);


}
