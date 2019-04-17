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

package org.dromara.raincat.springcloud.service;

import org.apache.commons.lang3.RandomUtils;
import org.dromara.raincat.core.service.RpcApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * SpringCloudRpcApplicationServiceImpl.
 *
 * @author xiaoyu
 */
@Service("rpcApplicationService")
public class SpringCloudRpcApplicationServiceImpl implements RpcApplicationService {

    private static final String DEFAULT_APPLICATION_NAME = "raincat-springCloud";

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public String findModelName() {
        return Optional.ofNullable(appName).orElse(buildDefaultApplicationName());
    }

    private String buildDefaultApplicationName() {
        return DEFAULT_APPLICATION_NAME + RandomUtils.nextInt(1, 10);
    }
}
