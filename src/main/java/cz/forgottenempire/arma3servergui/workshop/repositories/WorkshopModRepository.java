package cz.forgottenempire.arma3servergui.workshop.repositories;

import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopModRepository extends JpaRepository<WorkshopMod, Long> {
}
