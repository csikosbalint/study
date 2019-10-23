package vertx;

import io.vertx.core.AbstractVerticle;

/**
 * The Golden Rule - Don’t Block the Event Loop
 */
public class SimpleButWiseVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    System.out.println(this + "\twith " + vertx);
    vertx.eventBus().consumer("cron", (msg) -> {
      System.out.println(Thread.currentThread() + " Received: #" + msg.body());
      vertx.executeBlocking(promise -> {
          TheUgly.handler((String) msg.body()); //blocking
          promise.complete(msg.body());
        },
        true,
        asyncResult -> {
          System.out.println(Thread.currentThread() + " The result was: #" + asyncResult.result());
        });
    });
  }
}
