package net.thehuffs.showcase.dataservice.repository.jpa;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import net.thehuffs.showcase.dataservice.entity.FooEntity;

@Repository
public interface FooJpaRepository extends CrudRepository<FooEntity, UUID> {

}
