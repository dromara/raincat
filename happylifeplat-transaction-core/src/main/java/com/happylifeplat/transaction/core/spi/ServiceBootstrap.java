package com.happylifeplat.transaction.core.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 *  ServiceBootstrap
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/27 13:51
 * @since JDK 1.8
 */
public class ServiceBootstrap {


  public static <S> S loadFirst(Class<S> clazz) {
      final ServiceLoader<S> loader = loadAll(clazz);
      final Iterator<S> iterator = loader.iterator();
      if (!iterator.hasNext()) {
      throw new IllegalStateException(String.format(
          "No implementation defined in /META-INF/services/%s, please check whether the file exists and has the right implementation class!",
          clazz.getName()));
    }
    return iterator.next();
  }

  public static <S>  ServiceLoader<S> loadAll(Class<S> clazz) {
    return ServiceLoader.load(clazz);
  }
}
