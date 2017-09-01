package com.happylifeplat.transaction.core.spi.repository;

import com.google.common.collect.Lists;
import com.happylifeplat.transaction.common.enums.CompensationCacheTypeEnum;
import com.happylifeplat.transaction.common.exception.TransactionRuntimeException;
import com.happylifeplat.transaction.core.bean.TransactionRecover;
import com.happylifeplat.transaction.core.config.TxConfig;
import com.happylifeplat.transaction.core.config.TxFileConfig;
import com.happylifeplat.transaction.core.spi.ObjectSerializer;
import com.happylifeplat.transaction.core.spi.TransactionRecoverRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;


/**
 * <p>Description: .</p>
 * <p>Company: 深圳市旺生活互联网科技有限公司</p>
 * <p>Copyright: 2015-2017 happylifeplat.com All Rights Reserved</p>
 * 文件的实现方式
 *
 * @author yu.xiao@happylifeplat.com
 * @version 1.0
 * @date 2017/5/11 14:33
 * @since JDK 1.8
 */
@SuppressWarnings("unchecked")
public class FileTransactionRecoverRepository implements TransactionRecoverRepository {


    private String filePath;

    private volatile static boolean initialized;


    private ObjectSerializer serializer;

    @Override
    public void setSerializer(ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */
    @Override
    public int create(TransactionRecover transactionRecover) {
        writeFile(transactionRecover);
        return 1;
    }

    /**
     * 删除对象
     *
     * @param id 事务对象id
     * @return rows
     */
    @Override
    public int remove(String id) {
        String fullFileName = getFullFileName(id);
        File file = new File(fullFileName);
        if (file.exists()) {
            file.delete();
        }
        return 1;
    }

    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     */
    @Override
    public int update(TransactionRecover transactionRecover) throws TransactionRuntimeException {
        transactionRecover.setLastTime(new Date());
        transactionRecover.setVersion(transactionRecover.getVersion() + 1);
        transactionRecover.setRetriedCount(transactionRecover.getRetriedCount() + 1);
        try {
            writeFile(transactionRecover);
        } catch (Exception e) {
            throw new TransactionRuntimeException("更新数据异常！");
        }
        return 1;
    }


    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    @Override
    public TransactionRecover findById(String id) {
        return null;
    }

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    @Override
    public List<TransactionRecover> listAll() {
        List<TransactionRecover> transactionRecoverList = Lists.newArrayList();
        File path = new File(filePath);
        File[] files = path.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                TransactionRecover transaction = readTransaction(file);
                assert transaction != null;
                if (transaction.getVersion() == 1) {
                    transactionRecoverList.add(transaction);
                    transaction.setVersion(transaction.getVersion() + 1);
                    writeFile(transaction);
                }
            }
        }
        return transactionRecoverList;
    }


    @Override
    public void init(String modelName, TxConfig txConfig) {
        filePath = buildFilePath(modelName, txConfig.getTxFileConfig());
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    private String buildFilePath(String modelName, TxFileConfig txFileConfig) {

        String fileName = String.join("_", "TX", txFileConfig.getPrefix(), modelName.replaceAll("-", "_"));

        return String.join("/", txFileConfig.getPath(), fileName);


    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.FILE.getCompensationCacheType();
    }

    private void writeFile(TransactionRecover transaction) {
        makeDirIfNecessory();

        String file = getFullFileName(transaction.getId());

        FileChannel channel = null;
        RandomAccessFile raf;
        try {
            byte[] content = serialize(transaction);
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(content.length);
            buffer.put(content);
            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            channel.force(true);
        } catch (Exception e) {
            throw new TransactionRuntimeException(e);
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (IOException e) {
                    throw new TransactionRuntimeException(e);
                }
            }
        }
    }

    private String getFullFileName(String id) {
        return String.format("%s/%s", filePath, id);
    }

    private TransactionRecover readTransaction(File file) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            byte[] content = new byte[(int) file.length()];

            fis.read(content);

            return deserialize(content);
        } catch (Exception e) {
            throw new TransactionRuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // throw new TransactionRuntimeException(e);
                }
            }
        }
    }

    private void makeDirIfNecessory() {
        if (!initialized) {
            synchronized (FileTransactionRecoverRepository.class) {
                if (!initialized) {
                    File rootPathFile = new File(filePath);
                    if (!rootPathFile.exists()) {

                        boolean result = rootPathFile.mkdir();

                        if (!result) {
                            throw new TransactionRuntimeException("cannot create root path, the path to create is:" + filePath);
                        }

                        initialized = true;
                    } else if (!rootPathFile.isDirectory()) {
                        throw new TransactionRuntimeException("rootPath is not directory");
                    }
                }
            }
        }
    }

    private byte[] serialize(TransactionRecover transaction) throws Exception {
        return serializer.serialize(transaction);

    }

    private TransactionRecover deserialize(byte[] value) throws Exception {
        return serializer.deSerialize(value, TransactionRecover.class);
    }
}
