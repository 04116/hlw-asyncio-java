package org.vertx.starter;

import io.vertx.core.AbstractVerticle;

public class WorkerVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("vertx.hlw.wrk", message -> {
      message.reply("Hi from Worker Verticle");
    });
  }
}
