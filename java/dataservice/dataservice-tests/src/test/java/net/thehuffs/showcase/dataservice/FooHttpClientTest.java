package net.thehuffs.showcase.dataservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import com.google.protobuf.util.JsonFormat;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.proto.Foos;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FooHttpClientTest {

  private HttpClient client = HttpClient.newBuilder().build();

  private Foo foo;
  private int initialCount = 0;

  @Test
  @Order(1)
  public void testNotFound() throws URISyntaxException, IOException, InterruptedException {
    String invalidId = UUID.randomUUID().toString();
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + invalidId))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(404, response.statusCode());
  }

  @Test
  @Order(2)
  public void testFindAll() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos"))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foos.Builder builder = Foos.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foos foos = builder.build();

    initialCount = foos.getFooCount();
    System.out.println(initialCount);
  }

  @Test
  @Order(3)
  public void testCreate() throws URISyntaxException, IOException, InterruptedException {
    String title = "Super cool foo";
    Foo createdFoo = Foo.newBuilder().setTitle(title).build();

    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos"))
        .header("accepts", "application/json").header("content-type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(JsonFormat.printer().print(createdFoo))).build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foo.Builder builder = Foo.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    createdFoo = builder.build();

    assertNotEquals("", createdFoo.getId());
    assertEquals(title, createdFoo.getTitle());
    this.foo = createdFoo;
  }

  @Test
  @Order(4)
  public void testFindAfterCreate() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + foo.getId()))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foo.Builder builder = Foo.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foo updatedFoo = builder.build();

    assertEquals(foo.getId(), updatedFoo.getId());
    assertEquals(foo.getTitle(), updatedFoo.getTitle());
  }

  @Test
  @Order(5)
  public void testFindAllAfterCreate() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos"))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foos.Builder builder = Foos.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foos foos = builder.build();

    assertEquals(initialCount + 1, foos.getFooCount());
  }

  @Test
  @Order(6)
  public void testUpdate() throws URISyntaxException, IOException, InterruptedException {
    String title = "Super awesome foo";
    Foo updatedFoo = Foo.newBuilder().setId(foo.getId()).setTitle(title).build();

    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + foo.getId()))
        .header("accepts", "application/json").header("content-type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(JsonFormat.printer().print(updatedFoo))).build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foo.Builder builder = Foo.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    updatedFoo = builder.build();

    assertEquals(foo.getId(), updatedFoo.getId());
    assertEquals(title, updatedFoo.getTitle());
    this.foo = updatedFoo;
  }

  @Test
  @Order(7)
  public void testFindAfterUpdate() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + foo.getId()))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foo.Builder builder = Foo.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foo updatedFoo = builder.build();

    assertEquals(foo.getId(), updatedFoo.getId());
    assertEquals(foo.getTitle(), updatedFoo.getTitle());
  }

  @Test
  @Order(8)
  public void testFindAllAfterUpdate() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos"))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foos.Builder builder = Foos.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foos foos = builder.build();

    assertEquals(initialCount + 1, foos.getFooCount());
  }

  @Test
  @Order(9)
  public void testDelete() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + foo.getId())).DELETE().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(204, response.statusCode());
  }

  @Test
  @Order(10)
  public void testFindAfterDelete() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos/" + foo.getId()))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(404, response.statusCode());
  }

  @Test
  @Order(11)
  public void testFindAllAfterDelete() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:8080/api/foos"))
        .header("accepts", "application/json").GET().build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    String responseBody = response.body();
    Foos.Builder builder = Foos.newBuilder();
    JsonFormat.parser().merge(responseBody, builder);
    Foos foos = builder.build();

    assertEquals(initialCount, foos.getFooCount());
  }
}
