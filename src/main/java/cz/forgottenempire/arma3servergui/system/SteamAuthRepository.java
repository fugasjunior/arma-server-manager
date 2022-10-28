package cz.forgottenempire.arma3servergui.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SteamAuthRepository extends JpaRepository<SteamAuth, Long> {

}
