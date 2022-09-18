package cz.forgottenempire.arma3servergui.workshop.repositories;

import cz.forgottenempire.arma3servergui.workshop.entities.SteamAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAuthRepository extends CrudRepository<SteamAuth, Long> {

}
