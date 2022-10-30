package cz.forgottenempire.arma3servergui.scenario;

import static java.time.ZoneId.systemDefault;

import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.exceptions.ServerNotInitializedException;
import java.io.File;
import java.io.IOException;
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

    public boolean uploadScenarioToServer(MultipartFile file) {
        File missionsFolder = pathsFactory.getScenariosBasePath().toFile();
        if (!missionsFolder.isDirectory()) {
            throw new ServerNotInitializedException();
        }

        String scenarioName = file.getOriginalFilename();
        if (scenarioName != null) {
            // as some files may already be URI-endoded, decode them
            scenarioName = UriUtils.decode(scenarioName, Charset.defaultCharset());
        } else {
            return false;
        }

        log.info("Handling scenario upload {} (size {})", scenarioName, file.getSize());
        try {
            File scenarioFile = new File(missionsFolder, scenarioName);
            file.transferTo(scenarioFile);
            log.info("Successfully downloaded scenario {}", scenarioName);
        } catch (IOException e) {
            log.error("Could not download scenario {} due to {}", scenarioName, e.toString());
            return false;
        }

        return true;
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
