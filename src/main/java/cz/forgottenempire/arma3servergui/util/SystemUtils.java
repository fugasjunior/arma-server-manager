package cz.forgottenempire.arma3servergui.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class SystemUtils {

    public static boolean isPortAvailable(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (ConnectException e) {
            return true;
        } catch (IOException e) {
            throw new IllegalStateException("Error while trying to check open port", e);
        }
    }

    public static OSType getOsType() {
        String osName = System.getProperty("os.name");
        if (osName == null) {
            return OSType.UNKNOWN;
        }
        if (osName.contains("nux") || osName.contains("aix")) {
            return OSType.LINUX;
        }
        if (osName.contains("Win")) {
            return OSType.WINDOWS;
        }
        return OSType.UNKNOWN;
    }

    public enum OSType {
        WINDOWS,
        LINUX,
        UNKNOWN
    }
}
