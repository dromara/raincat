/*
 *
 * Copyright 2017-2018 549477611@qq.com(xiaoyu)
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.dromara.raincat.core.spi.repository;

import com.google.common.collect.Lists;
import org.dromara.raincat.annotation.RaincatSPI;
import org.dromara.raincat.common.bean.TransactionRecover;
import org.dromara.raincat.common.config.TxConfig;
import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.enums.CompensationOperationTypeEnum;
import org.dromara.raincat.common.exception.TransactionRuntimeException;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.holder.TransactionRecoverUtils;
import org.dromara.raincat.common.serializer.ObjectSerializer;
import org.dromara.raincat.core.spi.TransactionRecoverRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * file impl.
 *
 * @author xiaoyu
 */
@SuppressWarnings("all")
@RaincatSPI("file")
public class FileTransactionRecoverRepository implements TransactionRecoverRepository {

    private static volatile boolean initialized;

    private String filePath;

    private ObjectSerializer serializer;

    @Override
    public void setSerializer(final ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(final TransactionRecover transactionRecover) {
        writeFile(transactionRecover);
        return ROWS;
    }

    @Override
    public int remove(final String id) {
        String fullFileName = getFullFileName(id);
        File file = new File(fullFileName);
        if (file.exists()) {
            file.delete();
        }
        return ROWS;
    }

    @Override
    public int update(final TransactionRecover transactionRecover) throws TransactionRuntimeException {
        if (CompensationOperationTypeEnum.TASK_EXECUTE.getCode() == transactionRecover.getOperation()) {
            TransactionRecover recover = findById(transactionRecover.getId());
            recover.setCompleteFlag(CommonConstant.TX_TRANSACTION_COMPLETE_FLAG_OK);
            writeFile(recover);
            return ROWS;
        }
        transactionRecover.setLastTime(new Date());
        transactionRecover.setVersion(transactionRecover.getVersion() + 1);
        transactionRecover.setRetriedCount(transactionRecover.getRetriedCount() + 1);
        try {
            writeFile(transactionRecover);
        } catch (Exception e) {
            throw new TransactionRuntimeException(UPDATE_EX);
        }
        return ROWS;
    }

    @Override
    public TransactionRecover findById(final String id) {
        String fullFileName = getFullFileName(id);
        File file = new File(fullFileName);
        try {
            return readTransaction(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<TransactionRecover> listAll() {
        List<TransactionRecover> transactionRecoverList = Lists.newArrayList();
        File path = new File(filePath);
        File[] files = path.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    transactionRecoverList.add(readTransaction(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return transactionRecoverList;
    }

    @Override
    public List<TransactionRecover> listAllByDelay(final Date date) {
        final List<TransactionRecover> transactionRecovers = listAll();
        return transactionRecovers.stream()
                .filter(recover -> recover.getLastTime().compareTo(date) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public void init(final String appName, final TxConfig txConfig) {
        filePath = RepositoryPathUtils.buildFilePath(appName);
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    private void writeFile(final TransactionRecover transaction) {
        makeDir();
        String file = getFullFileName(transaction.getId());
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "rw");
            try (FileChannel channel = raf.getChannel()) {
                byte[] content = TransactionRecoverUtils.convert(transaction, serializer);
                ByteBuffer buffer = ByteBuffer.allocate(content.length);
                buffer.put(content);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.force(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFullFileName(final String id) {
        return String.format("%s/%s", filePath, id);
    }

    private TransactionRecover readTransaction(final File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] content = new byte[(int) file.length()];
            fis.read(content);
            return TransactionRecoverUtils.transformBean(content, serializer);
        }
    }

    private void makeDir() {
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

}
