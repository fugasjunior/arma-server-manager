package cz.forgottenempire.arma3servergui.common.util;

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
}
