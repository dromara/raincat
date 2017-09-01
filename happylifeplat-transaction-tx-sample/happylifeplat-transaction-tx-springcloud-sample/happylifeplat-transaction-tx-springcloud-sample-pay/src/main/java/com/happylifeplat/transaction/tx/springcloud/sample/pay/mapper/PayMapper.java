package com.happylifeplat.transaction.tx.springcloud.sample.pay.mapper;

import com.happylifeplat.transaction.tx.springcloud.sample.pay.entiy.Pay;
import org.apache.ibatis.annotations.Insert;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/8/7 14:01
 * @since JDK 1.8
 */
public interface PayMapper {


    @Insert("INSERT INTO pay(name,total_amount,create_time) VALUES(#{name}, #{totalAmount},#{createTime})")
    int  save(Pay pay);

}
