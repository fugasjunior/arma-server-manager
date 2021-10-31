package cz.forgottenempire.arma3servergui.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CreatorDlcsDto {

    private List<CreatorDlcDto> creatorDLCs;

    public CreatorDlcsDto() {
        creatorDLCs = new ArrayList<>();
    }

    public CreatorDlcsDto(List<CreatorDlcDto> creatorDLCs) {
        this.creatorDLCs = creatorDLCs;
    }

    public void add(CreatorDlcDto creatorDlcDto) {
        creatorDLCs.add(creatorDlcDto);
    }
}
