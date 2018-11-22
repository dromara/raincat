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

package org.dromara.raincat.admin.service.recover;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.raincat.admin.helper.ConvertHelper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.page.PageParameter;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.service.RecoverTransactionService;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.dromara.raincat.common.bean.adapter.TransactionRecoverAdapter;
import org.dromara.raincat.common.holder.DateUtils;
import org.dromara.raincat.common.holder.RepositoryPathUtils;
import org.dromara.raincat.common.serializer.ObjectSerializer;

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
 * file impl.
 * @author xiaoyu(Myth)
 */
public class FileRecoverTransactionServiceImpl implements RecoverTransactionService {

    private final ObjectSerializer objectSerializer;

    public FileRecoverTransactionServiceImpl(final ObjectSerializer objectSerializer) {
        this.objectSerializer=objectSerializer;
    }

    @Override
    public CommonPager<TransactionRecoverVO> listByPage(final RecoverTransactionQuery query) {
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
                voList = collect.stream()
                        .skip(start)
                        .limit(pageSize).collect(Collectors.toList());
            } else {
                totalCount = 0;
                voList = null;
            }
        } else if (StringUtils.isNoneBlank(query.getTxGroupId())
                && Objects.isNull(query.getRetry())) {
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
            totalCount = Objects.requireNonNull(files).length;
            voList = findByPage(files, start, pageSize);
        }
        voCommonPager.setPage(PageHelper.buildPage(query.getPageParameter(), totalCount));
        voCommonPager.setDataList(voList);
        return voCommonPager;
    }

    @Override
    public Boolean batchRemove(final List<String> ids, final String applicationName) {
        if (CollectionUtils.isEmpty(ids) || StringUtils.isBlank(applicationName)) {
            return Boolean.FALSE;
        }
        final String filePath = RepositoryPathUtils.buildFilePath(applicationName);
        ids.stream().map(id ->
                new File(getFullFileName(filePath, id))).forEach(File::delete);
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateRetry(final String id, final Integer retry, final String applicationName) {
        if (StringUtils.isBlank(id)
                || StringUtils.isBlank(applicationName)
                || Objects.isNull(retry)) {
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

    private void writeFile(final TransactionRecoverAdapter adapter, final String fullFileName) {
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

    private TransactionRecoverAdapter readRecover(final File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] content = new byte[(int) file.length()];
                fis.read(content);
                return objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private TransactionRecoverVO readTransaction(final File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] content = new byte[(int) file.length()];
                fis.read(content);
                final TransactionRecoverAdapter adapter = objectSerializer.deSerialize(content, TransactionRecoverAdapter.class);
                return ConvertHelper.buildVO(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<TransactionRecoverVO> findAll(final File[] files) {
        if (files != null && files.length > 0) {
            return Arrays.stream(files)
                    .map(this::readTransaction)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private List<TransactionRecoverVO> findByPage(final File[] files, final int start, final int pageSize) {
        if (files != null && files.length > 0) {
            return Arrays.stream(files).skip(start).limit(pageSize)
                    .map(this::readTransaction)
                    .collect(Collectors.toList());
        }
        return null;
    }

    private String getFullFileName(final String filePath, final String id) {
        return String.format("%s/%s", filePath, id);
    }

}
