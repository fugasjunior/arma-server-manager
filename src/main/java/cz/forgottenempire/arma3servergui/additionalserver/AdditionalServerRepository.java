package cz.forgottenempire.arma3servergui.additionalserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AdditionalServerRepository extends JpaRepository<AdditionalServer, Long> {

}
