package cz.forgottenempire.servermanager.additionalserver;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class AdditionalServerDto {

    private Long id;
    private String name;
    private boolean alive;
    private String startedAt;
    private String imageUrl;
}
