package cz.forgottenempire.arma3servergui.steamcmd;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

public class SteamCmdParameterBuilder {

    private final SteamCmdParameters parameters;

    public SteamCmdParameterBuilder() {
        parameters = new SteamCmdParameters();
        addDefaultParameters();
    }

    private void addDefaultParameters() {
        parameters.addParameter("+@NoPromptForPassword 1");
        parameters.addParameter("+@ShutdownOnFailedCommand 1");
    }

    public SteamCmdParameterBuilder withSteamGuardToken(@NotNull String token) {
        parameters.addParameter("+set_steam_guard_code " + token);
        return this;
    }

    public SteamCmdParameterBuilder withLogin(@NotNull String username, @Nullable String password) {
        String loginParameter = "+login " + username;
        if (StringUtils.isNotBlank(password)) {
            loginParameter += " " + password;
        }
        parameters.addParameter(loginParameter);
        return this;
    }

    public SteamCmdParameterBuilder withLogin(@NotNull String username, @Nullable String password, @Nullable String steamGuardToken) {
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

    public SteamCmdParameterBuilder withAnonymousLogin() {
        parameters.addParameter("+login anonymous");
        return this;
    }

    public SteamCmdParameterBuilder withInstallDir(String installDir) {
        parameters.addParameter("+force_install_dir");
        parameters.addParameter(installDir);
        return this;
    }

    public SteamCmdParameterBuilder withAppInstall(@NotNull Long appId, boolean validate, String... args) {
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

    public SteamCmdParameterBuilder withWorkshopItemInstall(@NotNull Long appId, @NotNull Long itemId,
            boolean validate) {
        String installParameter = "+workshop_download_item " + appId + " " + itemId;
        if (validate) {
            installParameter += " validate";
        }
        parameters.addParameter(installParameter);
        return this;
    }

    public SteamCmdParameterBuilder withCustomParameter(String parameter) {
        parameters.addParameter(parameter);
        return this;
    }

    public SteamCmdParameters build() {
        parameters.addParameter("+quit");
        return parameters;
    }
}
