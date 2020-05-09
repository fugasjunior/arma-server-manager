package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.Scenario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScenarioService {
    boolean uploadScenarioToServer(MultipartFile file);

    List<Scenario> getAllScenarios();
}
