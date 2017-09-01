
package com.happylifeplat.transaction.common.netty.serizlize.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.happylifeplat.transaction.common.netty.bean.HeartBeat;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionGroup;
import com.happylifeplat.transaction.common.netty.bean.TxTransactionItem;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.ArrayList;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * KryoPoolFactory
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/7/18 16:03
 * @since JDK 1.8
 */
public class KryoPoolFactory {

    private static volatile KryoPoolFactory poolFactory = null;

    private KryoFactory factory = () -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(HeartBeat.class);
       /* kryo.setRegistrationRequired(true);
        kryo.register(TxTransactionGroup.class);
        kryo.register(TxTransactionItem.class);
        kryo.register(ArrayList.class);*/
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        // kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
/*
        FieldSerializer someClassSerializer = new FieldSerializer(kryo, TxTransactionGroup.class);
        CollectionSerializer listSerializer = new CollectionSerializer();
        listSerializer.setElementClass(TxTransactionItem.class, kryo.getSerializer(TxTransactionItem.class));
        listSerializer.setElementsCanBeNull(false);
        someClassSerializer.getField("itemList").setClass(ArrayList.class, listSerializer);
        kryo.register(TxTransactionGroup.class, someClassSerializer);*/

        return kryo;
    };

    private KryoPool pool = new KryoPool.Builder(factory).build();

    private KryoPoolFactory() {
    }

    public static KryoPool getKryoPoolInstance() {
        if (poolFactory == null) {
            synchronized (KryoPoolFactory.class) {
                if (poolFactory == null) {
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }

    public KryoPool getPool() {
        return pool;
    }
}

