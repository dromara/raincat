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

package org.dromara.raincat.manager.spring;

import org.dromara.raincat.manager.config.Address;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * ApplicationStartListener.
 *
 * @author xiaoyu
 */
@Component
public class ApplicationStartListener implements ApplicationListener<WebServerInitializedEvent> {

    @Override
    public void onApplicationEvent(final WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        final String host = getHost();
        Address.getInstance()
                .setHost(host)
                .setPort(port)
                .setDomain(String.join(":", host, String.valueOf(port)));
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "127.0.0.1";
        }
    }
}
