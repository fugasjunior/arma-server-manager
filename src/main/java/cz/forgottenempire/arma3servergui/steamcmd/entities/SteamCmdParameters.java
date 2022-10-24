package cz.forgottenempire.arma3servergui.steamcmd.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Embeddable
public class SteamCmdParameters {

    private static final String STEAM_CREDENTIALS_PLACEHOLDER = "<{STEAM_CREDENTIALS_PLACEHOLDER}>";

    @ElementCollection
    private final List<String> parameters;

    public SteamCmdParameters() {
        parameters = new ArrayList<>();
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

    private void addParameter(String parameter) {
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

        public Builder withLogin() {
            String loginParameter = "+login " + STEAM_CREDENTIALS_PLACEHOLDER;
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

        public SteamCmdParameters build() {
            parameters.addParameter("+quit");
            return parameters;
        }
    }
}
