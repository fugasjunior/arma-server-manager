package cz.forgottenempire.servermanager.steamauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SteamAuthRepository extends JpaRepository<SteamAuth, Long> {

}
