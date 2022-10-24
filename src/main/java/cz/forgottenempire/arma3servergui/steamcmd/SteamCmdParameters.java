package cz.forgottenempire.arma3servergui.steamcmd;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

public class SteamCmdParameters {

    private final List<String> parameters;

    protected SteamCmdParameters() {
        parameters = new ArrayList<>();
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

    protected void addParameter(String parameter) {
        parameters.add(parameter);
    }

    public static class Builder {

        private final SteamCmdParameters parameters;

        public Builder() {
            parameters = new SteamCmdParameters();
            addDefaultParameters();
        }

        private void addDefaultParameters() {
            parameters.addParameter("+@NoPromptForPassword 1");
            parameters.addParameter("+@ShutdownOnFailedCommand 1");
        }

        public Builder withSteamGuardToken(@NotNull String token) {
            parameters.addParameter("+set_steam_guard_code " + token);
            return this;
        }

        public Builder withLogin(@NotNull String username, @Nullable String password) {
            String loginParameter = "+login " + username;
            if (StringUtils.isNotBlank(password)) {
                loginParameter += " " + password;
            }
            parameters.addParameter(loginParameter);
            return this;
        }

        public Builder withLogin(@NotNull String username, @Nullable String password,
                @Nullable String steamGuardToken) {
            String loginParameter = "+login " + username;
            if (StringUtils.isNotBlank(password)) {
                loginParameter += " " + password;
            }
            if (StringUtils.isNotBlank(steamGuardToken)) {
                loginParameter += " " + steamGuardToken;
            }
            parameters.addParameter(loginParameter);
            return this;
        }

        public Builder withAnonymousLogin() {
            parameters.addParameter("+login anonymous");
            return this;
        }

        public Builder withInstallDir(String installDir) {
            parameters.addParameter("+force_install_dir");
            parameters.addParameter(installDir);
            return this;
        }

        public Builder withAppInstall(@NotNull Long appId, boolean validate, String... args) {
            StringBuilder installParameter = new StringBuilder("+app_update " + appId);
            for (String arg : args) {
                installParameter.append(" ").append(arg);
            }
            if (validate) {
                installParameter.append(" validate");
            }
            parameters.addParameter(installParameter.toString());
            return this;
        }

        public Builder withWorkshopItemInstall(@NotNull Long appId, @NotNull Long itemId,
                boolean validate) {
            String installParameter = "+workshop_download_item " + appId + " " + itemId;
            if (validate) {
                installParameter += " validate";
            }
            parameters.addParameter(installParameter);
            return this;
        }

        public Builder withCustomParameter(@NotNull String parameter) {
            parameters.addParameter(parameter);
            return this;
        }

        public SteamCmdParameters build() {
            parameters.addParameter("+quit");
            return parameters;
        }
    }
}
