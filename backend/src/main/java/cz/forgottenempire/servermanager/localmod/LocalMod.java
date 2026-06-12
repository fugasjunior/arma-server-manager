package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.ServerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "local_mod")
public class LocalMod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    private Long fileSize;

    @Column(name = "server_only")
    private boolean serverOnly;

    @Column(name = "load_on_headless_client")
    private boolean loadOnHeadlessClient = true;

    private LocalDateTime uploadedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "local_mod_bikey", joinColumns = @JoinColumn(name = "local_mod_id"))
    @Column(name = "bikey")
    private Set<String> biKeys = new HashSet<>();

    public void addBiKey(String biKey) {
        biKeys.add(biKey);
    }

}
