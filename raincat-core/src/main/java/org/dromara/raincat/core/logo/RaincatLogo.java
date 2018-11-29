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

package org.dromara.raincat.core.logo;

import org.dromara.raincat.common.constant.CommonConstant;
import org.dromara.raincat.common.holder.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The hmily logo.
 *
 * @author xiaoyu
 */
public class RaincatLogo {

    private static final String HMILY_LOGO = "\n" +
            "            _                 _   \n" +
            "           (_)               | |  \n" +
            "  _ __ __ _ _ _ __   ___ __ _| |_ \n" +
            " | '__/ _` | | '_ \\ / __/ _` | __|\n" +
            " | | | (_| | | | | | (_| (_| | |_ \n" +
            " |_|  \\__,_|_|_| |_|\\___\\__,_|\\__|\n" +
            "                                  \n" +
            "                                  \n";

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RaincatLogo.class);

    public void logo() {
        String bannerText = buildBannerText();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(bannerText);
        } else {
            System.out.print(bannerText);
        }
    }

    private String buildBannerText() {
        return CommonConstant.LINE_SEPARATOR
                + CommonConstant.LINE_SEPARATOR
                + HMILY_LOGO
                + CommonConstant.LINE_SEPARATOR
                + " :: raincat :: (v" + VersionUtils.getVersion(getClass(), "1.0.0") + ")"
                + CommonConstant.LINE_SEPARATOR;
    }

}
