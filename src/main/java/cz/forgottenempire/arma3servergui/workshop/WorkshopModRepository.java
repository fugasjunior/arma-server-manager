package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.Collection;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
interface WorkshopModRepository extends JpaRepository<WorkshopMod, Long> {

    @Override
    @CacheEvict(value = "workshopModsResponse", allEntries = true)
    @NonNull
    <S extends WorkshopMod> List<S> saveAll(@NonNull Iterable<S> entities);

    @Override
    @CacheEvict(value = "workshopModsResponse", allEntries = true)
    @NonNull
    <S extends WorkshopMod> S save(@NonNull S entity);

    @Override
    @CacheEvict(value = "workshopModsResponse", allEntries = true)
    @NonNull
    void delete(@NonNull WorkshopMod entity);

    Collection<WorkshopMod> findAllByServerType(ServerType serverType);
}
