package cz.forgottenempire.arma3servergui.system;

import cz.forgottenempire.arma3servergui.util.SystemUtils.OSType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerOSDto {

    private OSType osType;
}
