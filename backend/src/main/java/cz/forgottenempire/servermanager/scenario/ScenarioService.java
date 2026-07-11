package cz.forgottenempire.servermanager.scenario;

import static java.time.ZoneId.systemDefault;

import cz.forgottenempire.servermanager.api.model.Arma3ScenarioDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenarioDto;
import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // Official scenarios from BI wiki (Arma Reforger 1.6.0+).
    // Source: https://community.bistudio.com/wiki/Arma_Reforger:Server_Config#scenarioId
    private static final List<ReforgerScenarioDto> OFFICIAL_REFORGER_SCENARIOS = List.of(
            scenario("{ECC61978EDCC2B5A}Missions/23_Campaign.conf", "Conflict - Everon"),
            scenario("{002AF7323E0129AF}Missions/Tutorial.conf", "Training"),
            scenario("{59AD59368755F41A}Missions/21_GM_Eden.conf", "Game Master - Everon"),
            scenario("{2BBBE828037C6F4B}Missions/22_GM_Arland.conf", "Game Master - Arland"),
            scenario("{F45C6C15D31252E6}Missions/27_GM_Cain.conf", "Game Master - Kolguyev"),
            scenario("{C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf", "Conflict - Northern Everon"),
            scenario("{28802845ADA64D52}Missions/23_Campaign_SWCoast.conf", "Conflict - Southern Everon"),
            scenario("{94992A3D7CE4FF8A}Missions/23_Campaign_Western.conf", "Conflict - Western Everon"),
            scenario("{FDE33AFE2ED7875B}Missions/23_Campaign_Montignac.conf", "Conflict - Montignac"),
            scenario("{DAA03C6E6099D50F}Missions/24_CombatOps.conf", "Combat Ops - Arland"),
            scenario("{C41618FD18E9D714}Missions/23_Campaign_Arland.conf", "Conflict - Arland"),
            scenario("{DFAC5FABD11F2390}Missions/26_CombatOpsEveron.conf", "Combat Ops - Everon"),
            scenario("{3F2E005F43DBD2F8}Missions/CAH_Briars_Coast.conf", "Capture & Hold - Briars"),
            scenario("{F1A1BEA67132113E}Missions/CAH_Castle.conf", "Capture & Hold - Montfort Castle"),
            scenario("{589945FB9FA7B97D}Missions/CAH_Concrete_Plant.conf", "Capture & Hold - Concrete Plant"),
            scenario("{9405201CBD22A30C}Missions/CAH_Factory.conf", "Capture & Hold - Almara Factory"),
            scenario("{1CD06B409C6FAE56}Missions/CAH_Forest.conf", "Capture & Hold - Simon's Wood"),
            scenario("{7C491B1FCC0FF0E1}Missions/CAH_LeMoule.conf", "Capture & Hold - Le Moule"),
            scenario("{6EA2E454519E5869}Missions/CAH_Military_Base.conf", "Capture & Hold - Camp Blake"),
            scenario("{2B4183DF23E88249}Missions/CAH_Morton.conf", "Capture & Hold - Morton"),
            scenario("{C47A1A6245A13B26}Missions/SP01_ReginaV2.conf", "Elimination"),
            scenario("{0648CDB32D6B02B3}Missions/SP02_AirSupport.conf", "Air Support"),
            scenario("{0220741028718E7F}Missions/23_Campaign_HQC_Everon.conf", "Conflict: HQ Commander - Everon"),
            scenario("{68D1240A11492545}Missions/23_Campaign_HQC_Arland.conf", "Conflict: HQ Commander - Arland"),
            scenario("{BB5345C22DD2B655}Missions/23_Campaign_HQC_Cain.conf", "Conflict: HQ Commander - Kolguyev"),
            scenario("{10B8582BAD9F7040}Missions/Scenario01_Intro.conf", "Operation Omega 01: Over The Hills And Far Away"),
            scenario("{1D76AF6DC4DF0577}Missions/Scenario02_Steal.conf", "Operation Omega 02: Radio Check"),
            scenario("{D1647575BCEA5A05}Missions/Scenario03_Villa.conf", "Operation Omega 03: Light In The Dark"),
            scenario("{6D224A109B973DD8}Missions/Scenario04_Sabotage.conf", "Operation Omega 04: Red Silence"),
            scenario("{FA2AB0181129CB16}Missions/Scenario05_Hill.conf", "Operation Omega 05: Cliffhanger"),
            scenario("{CB347F2F10065C9C}Missions/CombatOpsCain.conf", "Combat Ops - Kolguyev")
    );

    private final Arma3InstancePaths arma3InstancePaths;

    @Autowired
    public ScenarioService(Arma3InstancePaths arma3InstancePaths) {
        this.arma3InstancePaths = arma3InstancePaths;
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
        return OFFICIAL_REFORGER_SCENARIOS;
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

    private static ReforgerScenarioDto scenario(String value, String name) {
        return new ReforgerScenarioDto().value(value).name(name).official(true);
    }
}
