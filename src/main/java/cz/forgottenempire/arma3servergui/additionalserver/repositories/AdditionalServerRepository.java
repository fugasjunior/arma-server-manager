package cz.forgottenempire.arma3servergui.additionalserver.repositories;

import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalServerRepository extends JpaRepository<AdditionalServer, Long> {

}
