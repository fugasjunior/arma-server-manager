package cz.forgottenempire.arma3servergui.creatorDLC.services;

import cz.forgottenempire.arma3servergui.creatorDLC.dtos.CreatorDlcDto;
import java.util.List;

public interface CreatorDLCsService {

    List<CreatorDlcDto> getAllCreatorDLCs();

    void updateDlc(CreatorDlcDto creatorDlc);
}
