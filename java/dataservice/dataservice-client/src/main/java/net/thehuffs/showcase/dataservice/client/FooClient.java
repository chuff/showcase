package net.thehuffs.showcase.dataservice.client;

import java.util.Iterator;
import java.util.concurrent.Future;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.proto.FooServiceGrpc;
import net.thehuffs.showcase.dataservice.proto.FooServiceGrpc.FooServiceBlockingStub;
import net.thehuffs.showcase.dataservice.proto.FooServiceGrpc.FooServiceFutureStub;

public class FooClient {

  private FooServiceBlockingStub blockingStub;
  private FooServiceFutureStub futureStub;

  public FooClient(String host, Integer port) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress(host, port).usePlaintext().defaultLoadBalancingPolicy("round_robin").build();

    blockingStub = FooServiceGrpc.newBlockingStub(channel);
    futureStub = FooServiceGrpc.newFutureStub(channel);
  }

  public Future<Foo> findById(String id) {
    return futureStub.findById(StringValue.of(id));
  }

  public Iterator<Foo> findAll() {
    return blockingStub.findAll(Empty.getDefaultInstance());
  }

  public Future<Foo> create(Foo foo) {
    return futureStub.create(foo);
  }

  public Future<Foo> update(Foo foo) {
    return futureStub.update(foo);
  }

  public Future<Empty> delete(String id) {
    return futureStub.delete(StringValue.of(id));
  }
}
