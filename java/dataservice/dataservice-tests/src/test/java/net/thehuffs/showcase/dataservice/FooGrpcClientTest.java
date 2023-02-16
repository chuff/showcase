package net.thehuffs.showcase.dataservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import com.google.protobuf.Empty;
import io.grpc.Status;
import net.thehuffs.showcase.dataservice.client.FooClient;
import net.thehuffs.showcase.dataservice.proto.Foo;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FooGrpcClientTest {

  private FooClient fooClient = new FooClient("localhost", 8081);

  private Foo foo;
  private int initialCount = 0;

  @Test
  @Order(1)
  public void testNotFound() {
    String invalidId = UUID.randomUUID().toString();
    Future<Foo> future = fooClient.findById(invalidId);
    try {
      this.foo = future.get(5, TimeUnit.SECONDS);
      fail("Expected ExecutionException");
    } catch (ExecutionException e) {
      assertEquals(Status.NOT_FOUND, Status.fromThrowable(e));
    } catch (InterruptedException | TimeoutException e) {
      fail(e);
    }

  }

  @Test
  @Order(2)
  public void testFindAll() {
    int count = 0;
    Iterator<Foo> iterator = fooClient.findAll();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    initialCount = count;
    System.out.println(initialCount);
  }

  @Test
  @Order(3)
  public void testCreate() {
    String title = "Super cool foo";
    Foo createdFoo = Foo.newBuilder().setTitle(title).build();
    Future<Foo> future = fooClient.create(createdFoo);
    try {
      createdFoo = future.get(5, TimeUnit.SECONDS);
      assertNotEquals("", createdFoo.getId());
      assertEquals(title, createdFoo.getTitle());
      this.foo = createdFoo;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(4)
  public void testFindAfterCreate() {
    Future<Foo> future = fooClient.findById(this.foo.getId());
    try {
      Foo foundFoo = future.get(5, TimeUnit.SECONDS);
      assertEquals(this.foo.getId(), foundFoo.getId());
      assertEquals(this.foo.getTitle(), foundFoo.getTitle());
      this.foo = foundFoo;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(5)
  public void testFindAllAfterCreate() {
    int count = 0;
    Iterator<Foo> iterator = fooClient.findAll();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    assertEquals(initialCount + 1, count);
  }

  @Test
  @Order(6)
  public void testUpdate() {
    String title = "Super awesome foo";
    Foo updatedFoo = this.foo.toBuilder().setTitle(title).build();
    Future<Foo> future = fooClient.update(updatedFoo);
    try {
      updatedFoo = future.get(5, TimeUnit.SECONDS);
      assertEquals(this.foo.getId(), updatedFoo.getId());
      assertEquals(title, updatedFoo.getTitle());
      this.foo = updatedFoo;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(7)
  public void testFindAfterUpdate() {
    Future<Foo> future = fooClient.findById(this.foo.getId());
    try {
      Foo foundFoo = future.get(5, TimeUnit.SECONDS);
      assertEquals(this.foo.getId(), foundFoo.getId());
      assertEquals(this.foo.getTitle(), foundFoo.getTitle());
      this.foo = foundFoo;
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(8)
  public void testFindAllAfterUpdate() {
    int count = 0;
    Iterator<Foo> iterator = fooClient.findAll();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    assertEquals(initialCount + 1, count);
  }

  @Test
  @Order(9)
  public void testDelete() {
    Future<Empty> future = fooClient.delete(this.foo.getId());
    try {
      future.get(5, TimeUnit.SECONDS);
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(10)
  public void testFindAfterDelete() {
    Future<Foo> future = fooClient.findById(this.foo.getId());
    try {
      this.foo = future.get(5, TimeUnit.SECONDS);
      fail("Expected ExecutionException");
    } catch (ExecutionException e) {
      assertEquals(Status.NOT_FOUND, Status.fromThrowable(e));
    } catch (InterruptedException | TimeoutException e) {
      fail(e);
    }
  }

  @Test
  @Order(11)
  public void testFindAllAfterDelete() {
    int count = 0;
    Iterator<Foo> iterator = fooClient.findAll();
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    }
    assertEquals(initialCount, count);
  }
}
