package cz.forgottenempire.servermanager.common;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProcessFactory {

    public Process startProcess(File executable, List<String> parameters) throws IOException {
        File directory = executable.getParentFile();
        return startProcess(executable, parameters, directory);
    }

    public Process startProcess(File executable, List<String> parameters, File directory) throws IOException {
        return getBaseProcessBuilder(executable, parameters, directory)
                .start();
    }

    public Process startProcessWithDiscardedOutput(File executable, List<String> parameters) throws IOException {
        File directory = executable.getParentFile();
        return getBaseProcessBuilder(executable, parameters, directory)
                .redirectOutput(Redirect.DISCARD)
                .redirectError(Redirect.DISCARD)
                .start();
    }

    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File outputFile)
            throws IOException {

        File directory = executable.getParentFile();
        return startProcessWithRedirectedOutput(executable, parameters, directory, outputFile);
    }

    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File directory,
            File outputFile) throws IOException {

        return getBaseProcessBuilder(executable, parameters, directory)
                .redirectErrorStream(true)
                .redirectOutput(Redirect.appendTo(outputFile))
                .start();
    }

    private ProcessBuilder getBaseProcessBuilder(File executable, List<String> parameters, File directory) {
        List<String> commands = new ArrayList<>();
        commands.add(executable.getAbsolutePath());
        commands.addAll(parameters);

        return new ProcessBuilder(commands)
                .directory(directory);
    }
}
