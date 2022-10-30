package cz.forgottenempire.arma3servergui.scenario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Scenario implements Comparable<Scenario> {

    private String name;
    private Long fileSize;
    private String createdOn;

    @Override
    public int compareTo(Scenario scenario) {
        return this.getName().compareTo(scenario.getName());
    }
}
