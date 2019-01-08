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

package org.dromara.raincat.admin.service.login;

import org.dromara.raincat.admin.service.LoginService;
import org.dromara.raincat.common.holder.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * LoginServiceImpl.
 * @author xiaoyu
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    public static boolean LOGIN_SUCCESS = false;

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Value("${tx.admin.userName}")
    private String userName;

    @Value("${tx.admin.password}")
    private String password;

    @Override
    public Boolean login(final String userName, final String password) {
        LogUtil.info(LOGGER, "输入的用户名密码为:{}", () -> userName + "," + password);
        if (userName.equals(this.userName) && password.equals(this.password)) {
            LOGIN_SUCCESS = true;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean logout() {
        LOGIN_SUCCESS = false;
        return Boolean.TRUE;
    }
}
