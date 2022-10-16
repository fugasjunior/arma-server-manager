package cz.forgottenempire.arma3servergui.additionalserver.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdditionalServerDto {
    private Long id;
    private String name;
    private boolean alive;
    private String startedAt;
    private String imageUrl;
}
