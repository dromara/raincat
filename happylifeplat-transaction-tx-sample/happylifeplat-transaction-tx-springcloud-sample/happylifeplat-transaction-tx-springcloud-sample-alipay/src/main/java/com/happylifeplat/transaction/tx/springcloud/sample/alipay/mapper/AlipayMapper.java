package com.happylifeplat.transaction.tx.springcloud.sample.alipay.mapper;

import com.happylifeplat.transaction.tx.springcloud.sample.alipay.entity.Alipay;
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
public interface AlipayMapper {


    @Insert("INSERT INTO alipay(name,amount,create_time) VALUES(#{name}, #{amount},#{createTime})")
    int save(Alipay alipay);


}
