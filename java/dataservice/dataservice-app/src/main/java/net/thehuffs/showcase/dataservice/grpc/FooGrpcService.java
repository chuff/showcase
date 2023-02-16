package net.thehuffs.showcase.dataservice.grpc;

import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.StringValue;
import com.google.protobuf.util.JsonFormat;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.annotation.Timed;
import net.devh.boot.grpc.server.service.GrpcService;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.proto.FooServiceGrpc.FooServiceImplBase;
import net.thehuffs.showcase.dataservice.service.FooService;

@GrpcService
public class FooGrpcService extends FooServiceImplBase {

  private Logger logger = LoggerFactory.getLogger(FooGrpcService.class);

  private FooService fooService;

  public FooGrpcService(FooService fooService) {
    this.fooService = fooService;
  }

  @Override
  @Timed("grpc.foo.findById")
  public void findById(StringValue id, StreamObserver<Foo> responseObserver) {
    logger.debug("findById: " + id.getValue());

    try {
      Optional<Foo> optionalProto = fooService.findById(id.getValue());
      if (optionalProto.isEmpty()) {
        responseObserver.onError(Status.NOT_FOUND.asException());
      } else {
        responseObserver.onNext(optionalProto.get());
        responseObserver.onCompleted();
      }
    } catch (Exception e) {
      logger.error(e.toString(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asException());
    }
  }

  @Override
  @Timed("grpc.foo.findAll")
  public void findAll(Empty empty, StreamObserver<Foo> responseObserver) {
    logger.debug("findAll");

    try {
      Iterator<Foo> protoIterator = fooService.findAll();
      while (protoIterator.hasNext()) {
        responseObserver.onNext(protoIterator.next());
      }
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error(e.toString(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asException());
    }
  }

  @Override
  @Timed("grpc.foo.create")
  public void create(Foo proto, StreamObserver<Foo> responseObserver) {
    try {
      logger.debug("create: " + JsonFormat.printer().print(proto));
    } catch (InvalidProtocolBufferException e) {
      logger.error(e.toString(), e);
    }

    try {
      proto = fooService.create(proto);
      responseObserver.onNext(proto);
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error(e.toString(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asException());
    }
  }

  @Override
  @Timed("grpc.foo.update")
  public void update(Foo proto, StreamObserver<Foo> responseObserver) {
    try {
      logger.debug("update: " + JsonFormat.printer().print(proto));
    } catch (InvalidProtocolBufferException e) {
      logger.error(e.toString(), e);
    }

    try {
      Optional<Foo> optionalProto = fooService.update(proto);
      if (optionalProto.isEmpty()) {
        responseObserver.onError(Status.NOT_FOUND.asException());
      } else {
        responseObserver.onNext(proto);
        responseObserver.onCompleted();
      }
    } catch (Exception e) {
      logger.error(e.toString(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asException());
    }
  }

  @Override
  @Timed("grpc.foo.delete")
  public void delete(StringValue id, StreamObserver<Empty> responseObserver) {
    logger.debug("delete: " + id.getValue());

    try {
      fooService.delete(id.getValue());
      responseObserver.onNext(Empty.getDefaultInstance());
      responseObserver.onCompleted();
    } catch (Exception e) {
      logger.error(e.toString(), e);
      responseObserver.onError(Status.INTERNAL.withCause(e).asException());
    }
  }
}
