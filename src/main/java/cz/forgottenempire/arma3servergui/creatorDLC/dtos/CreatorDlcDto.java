package cz.forgottenempire.arma3servergui.creatorDLC.dtos;

import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatorDlcDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;

    public static CreatorDlcDto fromModel(CreatorDLC model) {
        return new CreatorDlcDto(model.getId(), model.getName(), model.getDescription(), model.getImageUrl());
    }
}
