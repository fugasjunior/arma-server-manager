package cz.forgottenempire.arma3servergui.scenario.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario implements Comparable<Scenario> {
    private String name;
    private Long fileSize;

    @Override
    public int compareTo(Scenario scenario) {
        return this.getName().compareTo(scenario.getName());
    }
}
