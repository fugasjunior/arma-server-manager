package cz.forgottenempire.servermanager.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class Arma3InstancePaths {

    private final PathsFactory pathsFactory;

    @Autowired
    public Arma3InstancePaths(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    /** Base: {@code servers/ARMA3/profiles/ARMA3_<id>} */
    public Path getInstanceBasePath(long id) {
        return pathsFactory.getServerPath(ServerType.ARMA3).resolve("profiles").resolve(instanceDirName(id));
    }

    /** Maps to {@code -profiles} flag. */
    public Path getInstanceProfilesPath(long id) {
        return getInstanceBasePath(id);
    }

    /** Maps to {@code -config} / {@code -cfg} flag parent dir. */
    public Path getInstanceConfigsPath(long id) {
        return getInstanceBasePath(id).resolve("configs");
    }

    /** Maps to {@code -keysFolder} flag. */
    public Path getInstanceKeysPath(long id) {
        return getInstanceBasePath(id).resolve("keys");
    }

    /** Maps to {@code -mpmissions} flag. */
    public Path getInstanceMpmissionsPath(long id) {
        return getInstanceBasePath(id).resolve("mpmissions");
    }

    /** Single source of truth for the on-disk per-instance dir name. */
    public static String instanceDirName(long id) {
        return "ARMA3_" + id;
    }
}
