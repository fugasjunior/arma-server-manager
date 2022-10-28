package cz.forgottenempire.arma3servergui.steamcmd;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

public class SteamCmdParameters {

    private static final String STEAM_CREDENTIALS_PLACEHOLDER = "<{STEAM_CREDENTIALS_PLACEHOLDER}>";

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

        public Builder withLogin() {
            String loginParameter = "+login " + STEAM_CREDENTIALS_PLACEHOLDER;
            parameters.add(loginParameter);
            return this;
        }

        public Builder withAnonymousLogin() {
            parameters.add("+login anonymous");
            return this;
        }

        public Builder withInstallDir(String installDir) {
            parameters.add("+force_install_dir");
            parameters.add(installDir);
            return this;
        }

        public Builder withAppInstall(@NotNull Long appId, boolean validate, String... args) {
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

        public Builder withWorkshopItemInstall(@NotNull Long appId, @NotNull Long itemId,
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
