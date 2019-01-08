package org.dromara.raincat.common.holder;

/**
 * The type O sinfo utils.
 *
 * @author: chenbin
 */
public final class OSinfoUtils {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Is linux boolean.
     *
     * @return the boolean
     */
    public static boolean isLinux() {
        return OS.contains("linux");
    }

}  