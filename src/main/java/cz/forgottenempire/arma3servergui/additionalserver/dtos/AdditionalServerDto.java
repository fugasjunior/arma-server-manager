package cz.forgottenempire.arma3servergui.additionalserver.dtos;

import cz.forgottenempire.arma3servergui.additionalserver.entities.AdditionalServer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdditionalServerDto implements Comparable<AdditionalServerDto>{
    private Long id;
    private String name;
    private boolean alive;
    private String imageUrl;

    public static AdditionalServerDto fromModel(AdditionalServer model, boolean alive) {
        AdditionalServerDto ret = new AdditionalServerDto();
        ret.setId(model.getId());
        ret.setName(model.getName());
        ret.setAlive(alive);
        ret.setImageUrl(model.getImageUrl());
        return ret;
    }

    public AdditionalServerDto fromModel(AdditionalServer model) {
        return fromModel(model, false);
    }

    @Override
    public int compareTo(AdditionalServerDto server) {
        return this.name.compareTo(server.getName());
    }
}
