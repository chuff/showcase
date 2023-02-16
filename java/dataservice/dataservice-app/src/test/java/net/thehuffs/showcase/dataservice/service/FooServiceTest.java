package net.thehuffs.showcase.dataservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import net.thehuffs.showcase.dataservice.entity.FooEntity;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.repository.jpa.FooJpaRepository;

public class FooServiceTest {

  @Test
  public void testFindAll() {
    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);
    when(fooJpaRepository.findAll()).thenReturn(new ArrayList<>());

    FooService fooService = new FooService(fooJpaRepository);

    assertEquals(false, fooService.findAll().hasNext());

    verify(fooJpaRepository, times(1)).findAll();

  }

  @Test
  public void testFindByIdFound() {
    UUID id = UUID.randomUUID();
    String title = "Super cool foo";

    FooEntity entity = new FooEntity();
    entity.setId(id);
    entity.setTitle(title);

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);
    when(fooJpaRepository.findById(id)).thenReturn(Optional.of(entity));

    FooService fooService = new FooService(fooJpaRepository);

    Optional<Foo> optionalProto = fooService.findById(id.toString());
    assertEquals(false, optionalProto.isEmpty());
    assertEquals(id.toString(), optionalProto.get().getId());
    assertEquals(title, optionalProto.get().getTitle());

    verify(fooJpaRepository, times(1)).findById(id);
  }

  @Test
  public void testFindByIdNotFound() {
    UUID id = UUID.randomUUID();

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);
    when(fooJpaRepository.findById(id)).thenReturn(Optional.empty());

    FooService fooService = new FooService(fooJpaRepository);

    assertEquals(true, fooService.findById(id.toString()).isEmpty());

    verify(fooJpaRepository, times(1)).findById(id);
  }

  @Test
  public void testCreate() {
    String title = "Super cool foo";
    Foo proto = Foo.newBuilder().setTitle(title).build();

    UUID id = UUID.randomUUID();
    FooEntity entity = new FooEntity();
    entity.setId(id);
    entity.setTitle(title);

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);

    when(fooJpaRepository.save((any(FooEntity.class)))).thenReturn(entity);

    FooService fooService = new FooService(fooJpaRepository);

    fooService.create(proto);

    verify(fooJpaRepository, times(1)).save(any(FooEntity.class));
  }

  @Test
  public void testUpdateFound() {
    UUID id = UUID.randomUUID();
    String title = "Super cool foo";
    Foo proto = Foo.newBuilder().setId(id.toString()).setTitle(title).build();

    FooEntity entity = new FooEntity();
    entity.setId(id);
    entity.setTitle(title);

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);

    when(fooJpaRepository.save((any(FooEntity.class)))).thenReturn(entity);

    FooService fooService = new FooService(fooJpaRepository);

    fooService.create(proto);

    verify(fooJpaRepository, times(1)).save(any(FooEntity.class));
  }

  @Test
  public void testUpdateNotFound() {
    UUID id = UUID.randomUUID();
    String title = "Super cool foo";
    Foo proto = Foo.newBuilder().setId(id.toString()).setTitle(title).build();

    FooEntity entity = new FooEntity();
    entity.setId(id);
    entity.setTitle(title);

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);

    when(fooJpaRepository.save((any(FooEntity.class)))).thenReturn(entity);

    FooService fooService = new FooService(fooJpaRepository);

    fooService.create(proto);

    verify(fooJpaRepository, times(1)).save(any(FooEntity.class));
  }

  @Test
  public void testDelete() {
    UUID id = UUID.randomUUID();

    FooJpaRepository fooJpaRepository = mock(FooJpaRepository.class);

    FooService fooService = new FooService(fooJpaRepository);

    fooService.delete(id.toString());

    verify(fooJpaRepository, times(1)).deleteById(id);
  }

}
