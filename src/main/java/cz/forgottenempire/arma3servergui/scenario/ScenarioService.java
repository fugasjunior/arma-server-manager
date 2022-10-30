package cz.forgottenempire.arma3servergui.scenario;

import static java.time.ZoneId.systemDefault;

import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.common.exceptions.ServerNotInitializedException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

@Service
@Slf4j
class ScenarioService {

    private final PathsFactory pathsFactory;

    @Autowired
    public ScenarioService(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    public void uploadScenarioToServer(MultipartFile file) {
        File missionsFolder = pathsFactory.getScenariosBasePath().toFile();
        if (!missionsFolder.isDirectory()) {
            throw new ServerNotInitializedException();
        }

        String scenarioName = file.getOriginalFilename();
        if (scenarioName == null) {
            return;
        }

        // as some files may already be URI-endoded, decode them
        scenarioName = UriUtils.decode(scenarioName, Charset.defaultCharset());

        log.info("Handling scenario upload {} (size {})", scenarioName, file.getSize());
        try {
            File scenarioFile = new File(missionsFolder, scenarioName);
            file.transferTo(scenarioFile);
            log.info("Successfully downloaded scenario {}", scenarioName);
        } catch (IOException e) {
            log.error("Could not download scenario {} due to {}", scenarioName, e.toString());
            throw new RuntimeException(e);
        }
    }

    public List<Scenario> getAllScenarios() {
        List<Scenario> scenarios = new ArrayList<>();

        String[] extensions = new String[]{"pbo"};
        File missionsFolder = pathsFactory.getScenariosBasePath().toFile();
        if (!missionsFolder.isDirectory()) {
            throw new ServerNotInitializedException();
        }
        for (Iterator<File> it = FileUtils.iterateFiles(missionsFolder, extensions, false); it.hasNext(); ) {
            File scenarioFile = it.next();
            Scenario scenarioDto = new Scenario();
            scenarioDto.setName(scenarioFile.getName());
            scenarioDto.setFileSize(scenarioFile.length());
            setScenarioFileCreationTime(scenarioFile, scenarioDto);
            scenarios.add(scenarioDto);
        }
        scenarios.sort(Comparator.naturalOrder());
        return scenarios;
    }

    public boolean deleteScenario(String name) {
        try {
            Files.delete(pathsFactory.getScenarioPath(name));
            log.info("Successfully deleted scenario {}", name);
        } catch (IOException e) {
            log.error("Could not delete scenario {} due to {}", name, e.toString());
            return false;
        }
        return true;
    }

    public List<ReforgerScenarioDto> getReforgerScenarios() {
        List<ReforgerScenarioDto> scenarios = new ArrayList<>();

        String executablePath = pathsFactory.getServerExecutableWithFallback(ServerType.REFORGER).getAbsolutePath();

        try {
            // adding "-logStats 1" makes the process flush it's output and not hang before printing the scenarios table
            Process process = new ProcessBuilder(executablePath, "-listScenarios", "-logStats", "1")
                    .directory(pathsFactory.getServerPath(ServerType.REFORGER).toAbsolutePath().toFile())
                    .start();

            // start a fail-safe in case the process hangs as it likes to do
            startWatchdogThread(process);

            //delimiter in the process output marks the list of scenarios in process output
            String delimiter = "--------------------------------------------------";
            int delimitersFound = 0;
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                if (line.contains(delimiter)) {
                    delimitersFound++;

                } else if (delimitersFound == 2 || delimitersFound == 4) {

                    // there should be scenarios listed on these lines
                    boolean isOfficialScenario = delimitersFound == 2;
                    scenarios.add(parseLineToScenarioDto(line, isOfficialScenario));

                } else if (delimitersFound == 5) {
                    // we're done, kill it with fire
                    break;
                }
            }
            process.destroyForcibly();
        } catch (IOException e) {
            log.error("Failed to get available Reforger scenarios", e);
            throw new RuntimeException(e);
        }

        return scenarios;
    }

    private void startWatchdogThread(Process process) {
        new Thread(() -> {
            try {
                process.waitFor(30, TimeUnit.SECONDS);
                process.destroyForcibly();
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private ReforgerScenarioDto parseLineToScenarioDto(String line, boolean official) {
        line = line.replaceAll(".*\\{", "{"); // remove leading log
        String[] split = line.split("\\s", 2); // there might be human-readable scenario name after first whitespace
        String value = split[0];
        String name = "";
        if (split.length > 1) {
            name = split[1].replaceAll("[()]", "");
        }
        return new ReforgerScenarioDto(value, name, official);
    }

    private void setScenarioFileCreationTime(File scenarioFile, Scenario scenarioDto) {
        try {
            BasicFileAttributes attr = Files.readAttributes(scenarioFile.toPath(), BasicFileAttributes.class);
            FileTime fileCreationTime = attr.creationTime();
            LocalDateTime dateTime = LocalDateTime.ofInstant(fileCreationTime.toInstant(), systemDefault());
            String formattedTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            scenarioDto.setCreatedOn(formattedTime);
        } catch (IOException e) {
            log.warn("Could not get file creation time for scenario file '{}'", scenarioFile.getName());
        }
    }
}
