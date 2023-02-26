package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

@Service
public class ServerLogsService {

    private final PathsFactory pathsFactory;

    public ServerLogsService(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    public String getLastLinesFromServerLog(Server server, int count) {
        File serverLogFile = pathsFactory.getServerLogFile(server.getType(), server.getId());
        if (!serverLogFile.exists()) {
            return "";
        }

        try {
            return getLastNLines(serverLogFile, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generated using ChatGTP
     * This method is efficient for large files such as server logs, as it doesn't load the whole content
     * into memory
     */
    private String getLastNLines(File file, int n) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long filePointer = raf.length();
        int lines = 0;
        StringBuilder result = new StringBuilder();

        while (filePointer >= 0 && lines < n) {
            raf.seek(filePointer);
            if (filePointer == 0) {
                // If we're at the beginning of the file, read the last line and exit
                result.insert(0, raf.readLine() + '\n');
                break;
            } else {
                // Otherwise, read the current character and move back one byte
                int currentByte = raf.read();
                filePointer--;
                if (currentByte == '\n') {
                    // If we've found a line ending, read the next line
                    result.insert(0, raf.readLine() + '\n');
                    lines++;
                } else if (currentByte == '\r') {
                    // If we've found a carriage return, read the next byte and check for a line ending
                    filePointer--;
                    raf.seek(filePointer);
                    if (raf.read() == '\n') {
                        // If the next byte is a line feed, read the next line
                        result.insert(0, raf.readLine() + '\n');
                        lines++;
                    }
                }
            }
        }

        raf.close();
        return result.toString();
    }
}
