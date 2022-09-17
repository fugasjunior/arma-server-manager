package cz.forgottenempire.arma3servergui.creatorDLC.controllers;

import cz.forgottenempire.arma3servergui.creatorDLC.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.creatorDLC.dtos.CreatorDlcsDto;
import cz.forgottenempire.arma3servergui.creatorDLC.services.CreatorDLCsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/creatordlcs")
public class CreatorDLCsController {

    private CreatorDLCsService creatorDLCsService;

    @GetMapping
    public ResponseEntity<CreatorDlcsDto> getCreatorDlcs() {
        List<CreatorDlcDto> allCreatorDLCs = creatorDLCsService.getAllCreatorDLCs();
        return ResponseEntity.ok(new CreatorDlcsDto(allCreatorDLCs));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCreatorDlc(@RequestBody CreatorDlcDto creatorDlcDto) {
        creatorDLCsService.updateDlc(creatorDlcDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setCreatorDLCsService(CreatorDLCsService creatorDLCsService) {
        this.creatorDLCsService = creatorDLCsService;
    }
}
