package cz.forgottenempire.arma3servergui.workshop.repositories;

import cz.forgottenempire.arma3servergui.workshop.entities.ModListPreset;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModPresetsRepository extends CrudRepository<ModListPreset, String> {
    Optional<ModListPreset> findModListPresetByName(String name);
}
