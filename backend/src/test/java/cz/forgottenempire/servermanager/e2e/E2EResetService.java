package cz.forgottenempire.servermanager.e2e;

import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessRepository;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.support.fakes.FakeProcessFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Profile("e2e")
class E2EResetService {

    private static final Logger log = LoggerFactory.getLogger(E2EResetService.class);

    private static final List<String> TABLES_TO_DELETE_IN_ORDER = List.of(
            "arma3server_active_mods",
            "arma3server_activedlcs",
            "dayzserver_active_mods",
            "reforger_server_active_mods",
            "arma3_network_settings",
            "arma3difficulty_settings",
            "launch_parameter",
            "arma3server",
            "dayzserver",
            "reforger_server",
            "server",
            "workshop_mod_bikey",
            "preset_mod",
            "workshop_mod",
            "mod_preset",
            "steam_auth",
            "additional_server"
    );

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FakeProcessFactory fakeProcessFactory;

    @Autowired
    private ServerProcessRepository serverProcessRepository;

    @Autowired
    private SteamAuthService steamAuthService;

    @Value("${directory.servers}")
    private String serversDir;

    @Value("${directory.mods}")
    private String modsDir;

    @Value("${directory.logs}")
    private String logsDir;

    @Transactional
    public void reset(boolean seedSteamAuth) {
        truncateDatabase();
        if (seedSteamAuth) {
            seedFakeSteamAuth();
        }
        fakeProcessFactory.reset();
        serverProcessRepository.clear();
        wipeDirs(serversDir, modsDir, logsDir);
        new File(serversDir, "ARMA3/mpmissions").mkdirs();
    }

    private void seedFakeSteamAuth() {
        SteamAuthDto dto = new SteamAuthDto();
        dto.setUsername("e2e-fake-steam-user");
        dto.setPassword("e2e-fake-steam-pass");
        steamAuthService.setAuthAccount(dto);
    }

    @Transactional
    public void markInstalled(String type) {
        entityManager.createNativeQuery(
                "UPDATE server_installation SET installation_status = 'FINISHED' WHERE type = :type"
        ).setParameter("type", type).executeUpdate();
    }

    private void truncateDatabase() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        for (String table : TABLES_TO_DELETE_IN_ORDER) {
            entityManager.createNativeQuery("DELETE FROM `" + table + "`").executeUpdate();
        }
        entityManager.createNativeQuery(
                "UPDATE server_installation SET installation_status = NULL, error_status = NULL, version = NULL, last_updated_at = NULL"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT IGNORE INTO server_installation (type, branch) VALUES " +
                "('ARMA3','CREATORDLC'),('DAYZ','PUBLIC'),('DAYZ_EXP','PUBLIC'),('REFORGER','PUBLIC')"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT IGNORE INTO available_branches (type, branch) VALUES " +
                "('ARMA3','CREATORDLC'),('ARMA3','PUBLIC'),('ARMA3','PROFILING'),('ARMA3','CONTACT')," +
                "('DAYZ','PUBLIC'),('DAYZ_EXP','PUBLIC'),('REFORGER','PUBLIC')"
        ).executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    private void wipeDirs(String... paths) {
        for (String path : paths) {
            try {
                File dir = new File(path);
                if (dir.exists()) {
                    FileUtils.cleanDirectory(dir);
                }
            } catch (IOException e) {
                log.warn("Failed to clean directory {}: {}", path, e.getMessage());
            }
        }
    }
}
