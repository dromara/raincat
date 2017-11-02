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

package com.happylifeplat.transaction.admin.service.recover;

import com.happylifeplat.transaction.admin.helper.ConvertHelper;
import com.happylifeplat.transaction.admin.helper.PageHelper;
import com.happylifeplat.transaction.admin.page.CommonPager;
import com.happylifeplat.transaction.admin.page.PageParameter;
import com.happylifeplat.transaction.admin.query.RecoverTransactionQuery;
import com.happylifeplat.transaction.admin.service.RecoverTransactionService;
import com.happylifeplat.transaction.admin.vo.TransactionRecoverVO;
import com.happylifeplat.transaction.common.bean.adapter.TransactionRecoverAdapter;
import com.happylifeplat.transaction.common.holder.DateUtils;
import com.happylifeplat.transaction.common.holder.RepositoryPathUtils;
import com.happylifeplat.transaction.common.serializer.ObjectSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>Description: .</p>
 * 文件实现
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2017/10/19 17:08
 * @since JDK 1.8
 */
public class FileRecoverTransactionServiceImpl implements RecoverTransactionService {


    @Autowired
    private ObjectSerializer objectSerializer;


    /**
     * 分页获取补偿事务信息
     *
     * @param query 查询条件
     * @return CommonPager<TransactionRecoverVO>
     */
    @Override
    public CommonPager<TransactionRecoverVO> listByPage(RecoverTransactionQuery query) {
        final String filePath = RepositoryPathUtils.buildFilePath(query.getApplicationName());
        final PageParameter pageParameter = query.getPageParameter();
        final int currentPage = pageParameter.getCurrentPage();
        final int pageSize = pageParameter.getPageSize();

        int start = (currentPage - 1) * pageSize;

        CommonPager<TransactionRecoverVO> voCommonPager = new CommonPager<>();
        File path;
        File[] files;
        int totalCount;
        List<TransactionRecoverVO> voList;


        //如果只查 重试条件的
        if (StringUtils.isBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
            path = new File(filePath);
            files = path.listFiles();
            final List<TransactionRecoverVO> all = findAll(files);
            if (CollectionUtils.isNotEmpty(all)) {
                final List<TransactionRecoverVO> collect =
                        all.stream().filter(Objects::nonNull)
                                .filter(vo -> vo.getRetriedCount() < query.getRetry())
                                .collect(Collectors.toList());
                totalCount = collect.size();
                voList = collect.stream().skip(start).limit(pageSize).collect(Collectors.toList());
            } else {
                totalCount = 0;
                voList = null;
            }
        } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.isNull(query.getRetry())) {
            final String fullFileName = getFullFileName(filePath, query.getTxGroupId());
            final File file = new File(fullFileName);
            files = new File[]{file};
            totalCount = files.length;
            voList = findAll(files);
        } else if (StringUtils.isNoneBlank(query.getTxGroupId()) && Objects.nonNull(query.getRetry())) {
            final String fullFileName = getFullFileName(filePath, query.getTxGroupId());
            final File file = new File(fullFileName);
            files = new File[]{file};
            totalCount = files.length;
            voList = findAll(files)
                    .stream().filter(Objects::nonNull)
                    .filter(vo -> vo.getRetriedCount() < query.getRetry())
                    .collect(Collectors.toList());
        } else {
            path = new File(filePath);
            files = path.listFiles();
            totalCount = files.length;
            voList = findByPage(files, start, pageSize);
        }
        voCommonPager.setPage(PageHelper.buildPage(query.getPageParameter(), totalCount));
        voCommonPager.setDataList(voList);
        return voCommonPager;
    }


    /**
     * 批量删除补偿事务信息
     *
     * @param ids             ids 事务id集合
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean batchRemove(List<String> ids, String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        final String filePath = RepositoryPathUtils.buildFilePath(applicationName);
        ids.stream().map(id -> new File(getFullFileName(filePath, id)))
                .forEach(File::delete);

        return Boolean.TRUE;
    }


    /**
     * 更改恢复次数
     *
     * @param id              事务id
     * @param retry           恢复次数
     * @param applicationName 应用名称
     * @return true 成功
     */
    @Override
    public Boolean updateRetry(String id, Integer retry, String applicationName) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(applicationName) || Objects.isNull(retry)) {
            return false;
        }
        final String filePath = RepositoryPathUtils.buildFilePath(applicationName);
        final String fullFileName = getFullFileName(filePath, id);
        final File file = new File(fullFileName);
        final TransactionRecoverAdapter adapter = readRecover(file);
        if (Objects.nonNull(adapter)) {
            try {
                adapter.setLastTime(DateUtils.getDateYYYY());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            adapter.setRetriedCount(retry);
            writeFile(adapter, fullFileName);
            return true;
        }


        return false;
    }


    private void writeFile(TransactionRecoverAdapter adapter, String fullFileName) {

        try {
            RandomAccessFile raf = new RandomAccessFile(fullFileName, "rw");
            try (FileChannel channel = raf.getChannel()) {
                byte[] content = objectSerializer.serialize(adapter);
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


    private TransactionRecoverAdapter readRecover(File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] content = new byte[(int) file.length()];

                final int read = fis.read(content);

                return objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    private TransactionRecoverVO readTransaction(File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] content = new byte[(int) file.length()];

                final int read = fis.read(content);
                final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
                return ConvertHelper.buildVO(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<TransactionRecoverVO> findAll(File[] files) {
        if (files != null && files.length > 0) {
            return Arrays.stream(files)
                    .map(this::readTransaction)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<TransactionRecoverVO> findByPage(File[] files, int start, int pageSize) {
        if (files != null && files.length > 0) {
            return Arrays.stream(files).skip(start).limit(pageSize)
                    .map(this::readTransaction)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private String getFullFileName(String filePath, String id) {
        return String.format("%s/%s", filePath, id);
    }


}
