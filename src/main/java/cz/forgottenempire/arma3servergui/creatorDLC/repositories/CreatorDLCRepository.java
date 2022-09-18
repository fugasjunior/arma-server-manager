package cz.forgottenempire.arma3servergui.creatorDLC.repositories;

import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreatorDLCRepository extends CrudRepository<CreatorDLC, Long> {

//    List<CreatorDLC> findAllByEnabledTrue();
}
