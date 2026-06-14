package cz.forgottenempire.servermanager.steamcmd;

import java.util.ArrayList;
import java.util.List;

public class SteamCmdParameters {

    private final List<String> parameters;

    public SteamCmdParameters() {
        parameters = new ArrayList<>();
    }

    public List<String> get() {
        return new ArrayList<>(parameters);
    }

    private void add(String parameter) {
        parameters.add(parameter);
    }

    public static class Builder {

        private final SteamCmdParameters parameters;

        public Builder() {
            parameters = new SteamCmdParameters();
            addDefaultParameters();
        }

        private void addDefaultParameters() {
            parameters.add("+@NoPromptForPassword 1");
            parameters.add("+@ShutdownOnFailedCommand 1");
        }

        public Builder continueOnFailedCommand() {
            parameters.parameters.remove("+@ShutdownOnFailedCommand 1");
            parameters.add("+@ShutdownOnFailedCommand 0");
            return this;
        }

        /** Username-only login that reuses the cached session in config.vdf. Used by all jobs and the session probe. */
        public Builder withCachedLogin(String username) {
            parameters.add("+login " + username);
            return this;
        }

        /**
         * Full credentials login that primes the config.vdf cache. If {@code steamGuardCode} is non-blank,
         * emits {@code +set_steam_guard_code <code>} before {@code +login} so the code is accepted for
         * both email and mobile-authenticator (TOTP) accounts.
         */
        public Builder withCredentialsLogin(String username, String password, String steamGuardCode) {
            if (steamGuardCode != null && !steamGuardCode.isBlank()) {
                parameters.add("+set_steam_guard_code " + steamGuardCode);
            }
            parameters.add("+login " + username + " " + password);
            return this;
        }

        public Builder withInstallDir(String installDir) {
            parameters.add("+force_install_dir");
            parameters.add(installDir);
            return this;
        }

        public Builder withAppInstall(Long appId, boolean validate, String... args) {
            StringBuilder installParameter = new StringBuilder("+app_update " + appId);
            for (String arg : args) {
                if (arg != null) {
                    installParameter.append(" ").append(arg);
                }
            }
            if (validate) {
                installParameter.append(" validate");
            }
            parameters.add(installParameter.toString());
            return this;
        }

        public Builder withWorkshopItemInstall(Long appId, Long itemId,
                boolean validate) {
            String installParameter = "+workshop_download_item " + appId + " " + itemId;
            if (validate) {
                installParameter += " validate";
            }
            parameters.add(installParameter);
            return this;
        }

        public SteamCmdParameters build() {
            parameters.add("+quit");
            return parameters;
        }
    }
}
