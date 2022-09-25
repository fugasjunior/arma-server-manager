package cz.forgottenempire.arma3servergui.workshop.repositories;

import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import cz.forgottenempire.arma3servergui.model.ModListPreset;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModPresetsRepository extends CrudRepository<ModListPreset, Long> {
    Optional<ModListPreset> findModListPresetByName(String name);
    void deleteByName(String name);
}
