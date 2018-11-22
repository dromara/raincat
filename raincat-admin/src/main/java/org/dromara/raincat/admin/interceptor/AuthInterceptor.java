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

package org.dromara.raincat.admin.interceptor;

import org.dromara.raincat.admin.annotation.Permission;
import org.dromara.raincat.admin.service.login.LoginServiceImpl;
import org.dromara.raincat.admin.annotation.Permission;
import org.dromara.raincat.admin.service.login.LoginServiceImpl;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * AuthInterceptor.
 * @author xiaoyu(Myth)
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            final Permission annotation = method.getAnnotation(Permission.class);
            if (Objects.isNull(annotation)) {
                return Boolean.TRUE;
            }
            final boolean login = annotation.isLogin();
            if (login) {
                if (!LoginServiceImpl.LOGIN_SUCCESS) {
                    request.setAttribute("code", "404");
                    request.setAttribute("msg", "please login！");
                    request.getRequestDispatcher("/").forward(request, response);
                    return Boolean.FALSE;
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

}
