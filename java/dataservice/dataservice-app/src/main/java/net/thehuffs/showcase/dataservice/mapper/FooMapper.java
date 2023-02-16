package net.thehuffs.showcase.dataservice.mapper;

import java.util.UUID;
import net.thehuffs.showcase.dataservice.entity.FooEntity;
import net.thehuffs.showcase.dataservice.proto.Foo;

public class FooMapper {

  public static void entityToProto(FooEntity entity, Foo.Builder protoBuilder) {
    if (entity.getId() != null) {
      protoBuilder.setId(entity.getId().toString());
    }

    protoBuilder.setTitle(entity.getTitle());
  }

  public static void protoToEntity(Foo proto, FooEntity entity) {
    if (proto.getId() != null && !proto.getId().isEmpty()) {
      entity.setId(UUID.fromString(proto.getId()));
    }

    entity.setTitle(proto.getTitle());
  }
}
