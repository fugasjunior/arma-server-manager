package cz.forgottenempire.arma3servergui.scenario.services.impl;

import cz.forgottenempire.arma3servergui.scenario.dtos.Scenario;
import cz.forgottenempire.arma3servergui.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.arma3servergui.scenario.services.ScenarioService;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class ScenarioServiceImpl implements ScenarioService {

    @Value("${serverDir}")
    private String serverDir;

    @Override
    public boolean uploadScenarioToServer(MultipartFile file) {
        File missionsFolder = new File(getMissionsFolder());
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

    @Override
    public List<Scenario> getAllScenarios() {
        List<Scenario> scenarios = new ArrayList<>();

        String[] extensions = new String[]{"pbo"};
        File missionsFolder = new File(getMissionsFolder());
        if (!missionsFolder.isDirectory()) {
            throw new ServerNotInitializedException();
        }
        for (Iterator<File> it = FileUtils.iterateFiles(missionsFolder, extensions, false); it.hasNext(); ) {
            File scenarioFile = it.next();
            Scenario scenarioDto = new Scenario(scenarioFile.getName(), scenarioFile.length());
            scenarios.add(scenarioDto);
        }
        scenarios.sort(Comparator.naturalOrder());
        return scenarios;
    }

    @Override
    public boolean deleteScenario(String name) {
        try {
            Files.delete(Path.of(getMissionsFolder(), name));
            log.info("Successfully deleted scenario {}", name);
        } catch (IOException e) {
            log.error("Could not delete scenario {} due to {}", name, e.toString());
            return false;
        }
        return true;
    }

    private String getMissionsFolder() {
        return serverDir + File.separatorChar + "mpmissions";
    }
}
