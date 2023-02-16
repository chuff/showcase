package net.thehuffs.showcase.dataservice.service;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import net.thehuffs.showcase.dataservice.entity.FooEntity;
import net.thehuffs.showcase.dataservice.mapper.FooMapper;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.repository.jpa.FooJpaRepository;

@Service
public class FooService {

  private Logger logger = LoggerFactory.getLogger(FooService.class);

  private FooJpaRepository fooJpaRepository;

  public FooService(FooJpaRepository fooJpaRepository) {
    this.fooJpaRepository = fooJpaRepository;
  }

  public Optional<Foo> findById(String id) {
    Optional<FooEntity> optionalEntity = fooJpaRepository.findById(UUID.fromString(id));
    if (optionalEntity.isEmpty()) {
      logger.debug("foo " + id + " not found in db");
      return Optional.empty();
    } else {
      logger.debug("foo " + id + " found in db");
      Foo.Builder protoBuilder = Foo.newBuilder();
      FooMapper.entityToProto(optionalEntity.get(), protoBuilder);
      Foo proto = protoBuilder.build();
      Optional<Foo> optionalProto = Optional.of(proto);
      return optionalProto;
    }
  }

  public Iterator<Foo> findAll() {
    Iterator<FooEntity> entityIterator = fooJpaRepository.findAll().iterator();

    Iterable<Foo> protoIterable = new Iterable<Foo>() {

      @Override
      public Iterator<Foo> iterator() {
        return new Iterator<Foo>() {

          @Override
          public boolean hasNext() {
            return entityIterator.hasNext();
          }

          @Override
          public Foo next() {
            Foo.Builder protoBuilder = Foo.newBuilder();
            FooMapper.entityToProto(entityIterator.next(), protoBuilder);
            return protoBuilder.build();
          }

        };
      }

    };

    return protoIterable.iterator();
  }

  public Foo create(Foo proto) {
    FooEntity entity = new FooEntity();
    FooMapper.protoToEntity(proto, entity);
    entity = fooJpaRepository.save(entity);
    logger.debug("foo " + entity.getId() + " saved to db");
    Foo.Builder protoBuilder = Foo.newBuilder();
    FooMapper.entityToProto(entity, protoBuilder);
    proto = protoBuilder.build();
    return proto;
  }

  public Optional<Foo> update(Foo proto) {
    String id = proto.getId();
    Optional<FooEntity> optionalEntity = fooJpaRepository.findById(UUID.fromString(id));
    if (optionalEntity.isEmpty()) {
      logger.debug("foo " + id + " not found in db");
      return Optional.empty();
    } else {
      logger.debug("foo " + id + " found in db");
      FooEntity entity = optionalEntity.get();
      FooMapper.protoToEntity(proto, entity);
      fooJpaRepository.save(entity);
      logger.debug("foo " + id + " saved to db");

      Foo.Builder protoBuilder = Foo.newBuilder();
      FooMapper.entityToProto(entity, protoBuilder);
      proto = protoBuilder.build();

      return Optional.of(proto);
    }
  }

  public void delete(String id) {
    fooJpaRepository.deleteById(UUID.fromString(id));
    logger.debug("foo " + id + " deleted from db");
  }

}
