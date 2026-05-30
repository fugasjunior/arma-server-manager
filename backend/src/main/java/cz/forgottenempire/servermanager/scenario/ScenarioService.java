package cz.forgottenempire.servermanager.scenario;

import static java.time.ZoneId.systemDefault;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import cz.forgottenempire.servermanager.api.model.Arma3ScenarioDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenarioDto;
import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final Supplier<List<ReforgerScenarioDto>> memoizedReforgerScenarios = Suppliers.memoizeWithExpiration(
            this::getReforgerScenariosFromServerExecutable, 5, TimeUnit.MINUTES);

    private final Arma3InstancePaths arma3InstancePaths;
    private final ProcessFactory processFactory;
    private final PathsFactory pathsFactory;

    @Autowired
    public ScenarioService(Arma3InstancePaths arma3InstancePaths, ProcessFactory processFactory, PathsFactory pathsFactory) {
        this.arma3InstancePaths = arma3InstancePaths;
        this.processFactory = processFactory;
        this.pathsFactory = pathsFactory;
    }

    public void uploadScenarioToServer(long serverId, MultipartFile file) {
        uploadScenarioToDir(arma3InstancePaths.getInstanceMpmissionsPath(serverId), file);
    }

    public List<Arma3ScenarioDto> getAllScenarios(long serverId) {
        return listScenariosInDir(arma3InstancePaths.getInstanceMpmissionsPath(serverId));
    }

    public boolean deleteScenario(long serverId, String name) {
        return deleteScenarioFromDir(arma3InstancePaths.getInstanceMpmissionsPath(serverId), name);
    }

    public List<ReforgerScenarioDto> getReforgerScenarios() {
        return memoizedReforgerScenarios.get();
    }

    private void uploadScenarioToDir(Path dir, MultipartFile file) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            log.error("Could not create scenarios directory {}", dir, e);
            throw new RuntimeException(e);
        }
        File missionsFolder = dir.toFile();

        String scenarioName = file.getOriginalFilename();
        if (scenarioName == null) {
            return;
        }

        scenarioName = UriUtils.decode(scenarioName, Charset.defaultCharset());

        log.info("Handling scenario upload {} (size {})", scenarioName, file.getSize());
        try {
            File scenarioFile = new File(missionsFolder, scenarioName);
            file.transferTo(scenarioFile);
            log.info("Successfully downloaded scenario {}", scenarioName);
        } catch (IOException e) {
            log.error("Could not download scenario {}", scenarioName, e);
            throw new RuntimeException(e);
        }
    }

    private List<Arma3ScenarioDto> listScenariosInDir(Path dir) {
        List<Arma3ScenarioDto> scenarioDtos = new ArrayList<>();

        String[] extensions = new String[]{"pbo"};
        File missionsFolder = dir.toFile();
        if (!missionsFolder.isDirectory()) {
            return scenarioDtos;
        }
        for (Iterator<File> it = FileUtils.iterateFiles(missionsFolder, extensions, false); it.hasNext(); ) {
            File scenarioFile = it.next();
            Arma3ScenarioDto dto = new Arma3ScenarioDto()
                    .name(scenarioFile.getName())
                    .fileSize(scenarioFile.length());
            setScenarioFileCreationTime(scenarioFile, dto);
            scenarioDtos.add(dto);
        }
        return scenarioDtos;
    }

    private boolean deleteScenarioFromDir(Path dir, String name) {
        try {
            Files.delete(dir.resolve(name));
            log.info("Successfully deleted scenario {}", name);
        } catch (IOException e) {
            log.error("Could not delete scenario {}", name, e);
            return false;
        }
        return true;
    }

    private List<ReforgerScenarioDto> getReforgerScenariosFromServerExecutable() {
        List<ReforgerScenarioDto> scenarios = new ArrayList<>();

        File executable = pathsFactory.getServerExecutableWithFallback(ServerType.REFORGER);

        try {
            Process process = processFactory.startProcess(executable, List.of("-listScenarios", "-logStats", "1"));
            startWatchdogThread(process);

            String delimiter = "--------------------------------------------------";
            int delimitersFound = 0;
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                if (line.contains(delimiter)) {
                    delimitersFound++;
                } else if (delimitersFound == 2 || delimitersFound == 4) {
                    boolean isOfficialScenario = delimitersFound == 2;
                    scenarios.add(parseLineToScenarioDto(line, isOfficialScenario));
                } else if (delimitersFound == 5) {
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
        line = line.replaceAll(".*\\{", "{");
        String[] split = line.split("\\s", 2);
        String value = split[0];
        String name = "";
        if (split.length > 1) {
            name = split[1].replaceAll("[()]", "");
        }
        return new ReforgerScenarioDto().value(value).name(name).official(official);
    }

    private void setScenarioFileCreationTime(File scenarioFile, Arma3ScenarioDto dto) {
        try {
            BasicFileAttributes attr = Files.readAttributes(scenarioFile.toPath(), BasicFileAttributes.class);
            FileTime fileCreationTime = attr.creationTime();
            LocalDateTime dateTime = LocalDateTime.ofInstant(fileCreationTime.toInstant(), systemDefault());
            dto.createdOn(dateTime.atZone(systemDefault()).toOffsetDateTime());
        } catch (IOException e) {
            log.warn("Could not get file creation time for scenario file '{}'", scenarioFile.getName());
        }
    }
}
