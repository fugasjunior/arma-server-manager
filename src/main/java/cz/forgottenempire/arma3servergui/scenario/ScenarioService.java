package cz.forgottenempire.arma3servergui.scenario;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

interface ScenarioService {

    boolean uploadScenarioToServer(MultipartFile file);

    boolean deleteScenario(String name);

    List<Scenario> getAllScenarios();
}
