package com.raincat.manager.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: .</p>
 *
 * @author xiaoyu(Myth)
 * @version 1.0
 * @date 2018/2/8 18:02
 * @since JDK 1.8
 */

public class Test {

    private Map<String,TestYu> tables = new HashMap<>();

    public Map<String, TestYu> getTables() {
        return tables;
    }

    public void setTables(Map<String, TestYu> tables) {
        this.tables = tables;
    }
}
