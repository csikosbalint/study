package vertx;

import io.vertx.core.AbstractVerticle;

public class SimpleVerticle extends AbstractVerticle {
  public SimpleVerticle() {
    System.out.println(this + "\tnew()");
  }

  @Override
  public void start() throws Exception {
    System.out.println(this + "\twith " + vertx);
    vertx.eventBus().consumer("cron", msg -> {
      System.out.println(Thread.currentThread() + " " + Thread.currentThread().getId() + " Received: #" + msg.body());
//      Thread.currentThread().interrupt();
//      Thread.currentThread().stop();
      TheUgly.handler((String) msg.body().toString()); // blocking
    });
  }
}
