package cz.forgottenempire.arma3servergui.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
    private String name;
    private Long fileSize;
}
