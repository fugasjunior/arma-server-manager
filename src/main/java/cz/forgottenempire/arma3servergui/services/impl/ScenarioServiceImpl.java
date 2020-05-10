package cz.forgottenempire.arma3servergui.services.impl;

import cz.forgottenempire.arma3servergui.dtos.Scenario;
import cz.forgottenempire.arma3servergui.services.ScenarioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
        String scenarioName = file.getOriginalFilename();

        log.info("Handling scenario upload {} (size {})", scenarioName, file.getSize());
        try {
            System.out.println(file.getContentType());
            File scenarioFile = new File(getMissionsFolder() + File.separatorChar + scenarioName);
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
        for (Iterator<File> it = FileUtils.iterateFiles(missionsFolder, extensions, false); it.hasNext(); ) {
            File scenarioFile = it.next();
            Scenario scenarioDto = new Scenario(scenarioFile.getName(), scenarioFile.length());
            scenarios.add(scenarioDto);
        }

        return scenarios;
    }

    private String getMissionsFolder() {
        return serverDir + File.separatorChar + "mpmissions";
    }
}
