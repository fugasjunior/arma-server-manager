package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.model.Arma3ServerDto;
import cz.forgottenempire.servermanager.api.model.DayZServerDto;
import cz.forgottenempire.servermanager.api.model.ReforgerServerDto;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
class ServerSecretsMasker {

    void maskIfUnauthorized(ServerDto dto) {
        if (canViewSecrets()) return;
        if (dto instanceof Arma3ServerDto a3) {
            a3.setPassword(null);
            a3.setAdminPassword(null);
        } else if (dto instanceof DayZServerDto dz) {
            dz.setPassword(null);
            dz.setAdminPassword(null);
        } else if (dto instanceof ReforgerServerDto rf) {
            rf.setPassword(null);
            rf.setAdminPassword(null);
        }
    }

    boolean canViewSecrets() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> PermissionCode.SERVER_SECRETS_VIEW.equals(a.getAuthority()));
    }
}
