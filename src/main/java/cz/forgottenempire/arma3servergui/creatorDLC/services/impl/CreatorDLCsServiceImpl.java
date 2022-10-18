package cz.forgottenempire.arma3servergui.creatorDLC.services.impl;

import cz.forgottenempire.arma3servergui.creatorDLC.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import cz.forgottenempire.arma3servergui.creatorDLC.repositories.CreatorDLCRepository;
import cz.forgottenempire.arma3servergui.creatorDLC.services.CreatorDLCsService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreatorDLCsServiceImpl implements CreatorDLCsService {

    private CreatorDLCRepository creatorDLCRepository;

    @Override
    public List<CreatorDlcDto> getAllCreatorDLCs() {
        return StreamSupport.stream(creatorDLCRepository.findAll().spliterator(), false)
                .map(CreatorDlcDto::fromModel)
                .collect(Collectors.toList());
    }

    @Override
    public void updateDlc(CreatorDlcDto dlcDto) {
        CreatorDLC dlc = creatorDLCRepository.findById(dlcDto.getId())
                .orElseThrow(NotFoundException::new);

        creatorDLCRepository.save(dlc);
    }

    @Autowired
    public void setCreatorDLCRepository(CreatorDLCRepository creatorDLCRepository) {
        this.creatorDLCRepository = creatorDLCRepository;
    }
}
