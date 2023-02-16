package net.thehuffs.showcase.dataservice.rest;

import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.google.protobuf.util.JsonFormat;
import net.thehuffs.showcase.dataservice.proto.Foo;
import net.thehuffs.showcase.dataservice.proto.Foos;
import net.thehuffs.showcase.dataservice.service.FooService;
import reactor.core.publisher.Mono;

@RestController
public class FooRestController {

  private Logger logger = LoggerFactory.getLogger(FooRestController.class);

  private FooService fooService;

  public FooRestController(FooService fooService) {
    this.fooService = fooService;
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/foos/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<?>> findById(@PathVariable String id) {
    logger.debug("findById: " + id);

    try {
      Optional<Foo> optionalProto = fooService.findById(id);
      if (optionalProto.isEmpty()) {
        return Mono.just(ResponseEntity.notFound().build());
      } else {
        return Mono.just(ResponseEntity.ok().body(JsonFormat.printer().print(optionalProto.get())));
      }
    } catch (Exception e) {
      logger.error(e.toString(), e);
      return Mono.just(ResponseEntity.internalServerError().body(e.toString()));
    }
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/foos", produces = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<?>> findAll() {
    logger.debug("findAll");

    try {
      Foos.Builder foosBuilder = Foos.newBuilder();
      Iterator<Foo> protoIterator = fooService.findAll();
      while (protoIterator.hasNext()) {
        foosBuilder.addFoo(protoIterator.next());
      }
      return Mono.just(ResponseEntity.ok().body(JsonFormat.printer().print(foosBuilder.build())));
    } catch (Exception e) {
      logger.error(e.toString(), e);
      return Mono.just(ResponseEntity.internalServerError().body(e.toString()));
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/foos", produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<?>> create(@RequestBody String body) {
    logger.debug("create: " + body);

    try {
      Foo.Builder builder = Foo.newBuilder();
      JsonFormat.parser().ignoringUnknownFields().merge(body, builder);
      Foo proto = fooService.create(builder.build());
      return Mono.just(ResponseEntity.ok().body(JsonFormat.printer().print(proto)));
    } catch (Exception e) {
      logger.error(e.toString(), e);
      return Mono.just(ResponseEntity.internalServerError().body(e.toString()));
    }
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/api/foos/{id}", produces = {MediaType.APPLICATION_JSON_VALUE},
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Mono<ResponseEntity<?>> update(@RequestBody String body) {
    logger.debug("update: " + body);

    try {
      Foo.Builder builder = Foo.newBuilder();
      JsonFormat.parser().ignoringUnknownFields().merge(body, builder);
      Optional<Foo> optionalProto = fooService.update(builder.build());
      if (optionalProto.isEmpty()) {
        return Mono.just(ResponseEntity.noContent().build());
      } else {
        return Mono.just(ResponseEntity.ok().body(JsonFormat.printer().print(optionalProto.get())));
      }
    } catch (Exception e) {
      logger.error(e.toString(), e);
      return Mono.just(ResponseEntity.internalServerError().body(e.toString()));
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/api/foos/{id}")
  public Mono<ResponseEntity<?>> deleteById(@PathVariable String id) {
    logger.debug("delete: " + id);

    try {
      fooService.delete(id);
      return Mono.just(ResponseEntity.noContent().build());
    } catch (Exception e) {
      logger.error(e.toString(), e);
      return Mono.just(ResponseEntity.internalServerError().body(e.toString()));
    }
  }

}
