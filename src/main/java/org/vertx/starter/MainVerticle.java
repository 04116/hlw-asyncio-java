package org.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new WorkerVerticle());
    vertx.createHttpServer().requestHandler(req -> {
      vertx.eventBus().request("vertx.hlw.wrk", "", reply -> {
        req.response()
          .putHeader("content-type", "text/plain")
          .end(reply.result().body().toString());
      });
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
