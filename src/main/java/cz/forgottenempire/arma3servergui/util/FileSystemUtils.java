package cz.forgottenempire.arma3servergui.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class FileSystemUtils {

    private FileSystemUtils() {
    }

    public static void directoryToLowercase(File directory) throws IOException {
        Collection<File> files = FileUtils.listFilesAndDirs(directory, TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        files.remove(directory); // current directory is in the list by default, need to remove it
        for (File file : files) {
            File newFile = new File(file.getParent(), file.getName().toLowerCase());
            try {
                if (file.isDirectory()) {
                    directoryToLowercase(file);
                    FileUtils.moveDirectory(file, newFile);
                } else if (file.isFile()) {
                    FileUtils.moveFile(file, newFile);
                }
            } catch (FileExistsException ignored) {
                // can be ignored
            }
        }
    }
}
