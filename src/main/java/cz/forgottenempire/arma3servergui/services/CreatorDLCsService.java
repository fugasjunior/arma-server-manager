package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.CreatorDlcDto;
import java.util.List;

public interface CreatorDLCsService {

    List<CreatorDlcDto> getAllCreatorDLCs();

    void updateDlc(CreatorDlcDto creatorDlc);
}
