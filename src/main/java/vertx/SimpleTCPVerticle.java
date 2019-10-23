package vertx;

import io.vertx.core.AbstractVerticle;

public class SimpleTCPVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
    System.out.println(this + "\twith " + vertx);
    vertx
      .createNetServer()
      .connectHandler( socket -> {
        socket.handler( buffer -> {
          System.out.println(buffer.getString(0, buffer.length()));
        });
      })
      .listen(8080);
  }
}
