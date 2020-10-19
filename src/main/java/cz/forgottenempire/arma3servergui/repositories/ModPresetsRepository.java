package cz.forgottenempire.arma3servergui.repositories;

import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import cz.forgottenempire.arma3servergui.model.ModListPreset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModPresetsRepository extends CrudRepository<ModListPreset, String> {}
