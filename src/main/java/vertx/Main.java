package vertx;

import io.vertx.core.*;
import vertx.b.MyObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.HashSet;
import java.util.Set;

public class Main extends AbstractVerticle {
  static long start = System.currentTimeMillis();
  static int counter = 0;

  public static void main(String[] args) {
    System.out.println(System.getProperty("java.util.logging.config.file"));
    Set<String> threads = new HashSet<>();
    System.out.println("Threads:");
    Thread.getAllStackTraces().keySet().stream()
      .forEach(thread -> {
        threads.add(thread.toString());
        System.out.println("\t" + thread);
      });
//    Thread[Common-Cleaner,8,InnocuousThreadGroup] -> Java9 Cleaner mechanism
//    Thread[Finalizer,8,system] -> GC related (runs finalize on unref objs
//    Thread[Reference Handler,10,system] GC related (process ref objs)
//    Thread[main,5,main]
//    Thread[Signal Dispatcher,9,system] -> Handles native API SIGNAL-s
//    Thread[Monitor Ctrl-Break,5,main] -> IntelliJ magical agent?

//    https://vertx.io/docs/vertx-core/java/
    VertxOptions options = new VertxOptions();
    /**
     * In a standard reactor implementation there is a single event loop thread which runs around in a loop delivering all events to all handlers as they arrive.
     *
     * The trouble with a single thread is it can only run on a single core at any one time, so if you want your single threaded reactor application (e.g. your Node.js application) to scale over your multi-core server you have to start up and manage many different processes.
     *
     * Vert.x works differently here. Instead of a single event loop, each Vertx instance maintains several event loops. By default we choose the number based on the number of available cores on the machine, but this can be overridden.
     *
     * This means a single Vertx process can scale across your server, unlike Node.js.
     *
     * We call this pattern the Multi-Reactor Pattern to distinguish it from the single threaded reactor pattern.
     */
    options.setEventLoopPoolSize(1);
//    options.setMaxEventLoopExecuteTime(5);
//    options.setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS);
    options.setWorkerPoolSize(1);
    Vertx vertx = Vertx.vertx(options);

    System.out.println(Main.class + "\twith " + vertx);

    Promise first = Promise.promise();
    vertx.deployVerticle(
      "SimpleVerticle",
//      "SimpleButWiseVerticle",
      new DeploymentOptions().setInstances(1),
      first);

    first.future()
      .setHandler((ar) -> {
        System.out.println("Additional Threads:");
        Thread.getAllStackTraces().keySet().stream()
          .forEach(thread -> {
            if (!threads.contains(thread.toString())) {
              System.out.println("\t" + thread);
              threads.add(thread.toString());
            }
          });
      });

    MessageCodec<MyObject, MyObject> myObjCodec = new MessageCodec<MyObject, MyObject>() {
      @Override
      public void encodeToWire(Buffer buffer, MyObject myObject) {
        buffer.appendInt(myObject.toString().length());
        buffer.appendString(myObject.toString());
      }

      @Override
      public MyObject decodeFromWire(int i, Buffer buffer) {
        int _pos = i;
        int length = buffer.getInt(_pos);
        String msg = buffer.getString(_pos+=4, _pos+=length);
        return new MyObject(msg);
      }

      @Override
      public MyObject transform(MyObject myObject) {
        return myObject;
      }

      @Override
      public String name() {
        return this.getClass().getSimpleName();
      }

      @Override
      public byte systemCodecID() {
        return -1;
      }
    };
    vertx.eventBus().registerDefaultCodec(MyObject.class, myObjCodec);

    vertx.setPeriodic(500, id -> {
      System.out.println(Thread.currentThread() + " It is time to process #" + counter);
      vertx.eventBus()
        .send("cron",
          new MyObject(String.valueOf(counter++))
        );
    });
  }
}
