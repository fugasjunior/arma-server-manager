package cz.forgottenempire.servermanager.system;

import cz.forgottenempire.servermanager.util.SystemUtils.OSType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerOSDto {

    private OSType osType;
}
