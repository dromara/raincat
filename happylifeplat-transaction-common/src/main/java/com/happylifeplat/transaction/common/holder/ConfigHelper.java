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
package com.happylifeplat.transaction.common.holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author xiaoyu
 */
public class ConfigHelper {


    private PropertiesConfiguration propertiesConfiguration = null;

    public String getStringValue(String key) {
        return propertiesConfiguration.getString(key);
    }

    public void setProperty(String key, Object val) {
        propertiesConfiguration.setProperty(key, val);
        try {
            propertiesConfiguration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int getIntValue(String key) {
        return propertiesConfiguration.getInt(key);
    }

    public float getFloatValue(String key) {
        return propertiesConfiguration.getFloat(key);
    }

    public ConfigHelper(String propertyPath) {
        try {
            propertiesConfiguration = new PropertiesConfiguration(propertyPath);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Please configure check  file: " + propertyPath);
        }
    }

}
