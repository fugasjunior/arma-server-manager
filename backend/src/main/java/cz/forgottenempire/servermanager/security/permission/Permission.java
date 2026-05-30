package cz.forgottenempire.servermanager.security.permission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(name = "code", length = 64)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    public Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
