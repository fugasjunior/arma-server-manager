package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ServerInstallation {

    @Id
    @Enumerated(EnumType.STRING)
    private ServerType type;

    private String version;
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;

    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    @Column(name = "branch", nullable = false)
    @Enumerated(EnumType.STRING)
    private Branch branch;

    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Branch.class)
    @CollectionTable(name = "available_branches", joinColumns = @JoinColumn(name = "type"))
    private Set<Branch> availableBranches;

    public ServerInstallation(ServerType type) {
        this.type = type;
    }

    public enum Branch {
        PUBLIC,
        PROFILING,
        CONTACT,
        CREATORDLC
    }
}
