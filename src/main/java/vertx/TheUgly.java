package vertx;

import java.util.concurrent.TimeUnit;

public class TheUgly {
  public static void handler(String message) {
    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
//    Thread.currentThread().stop();
    System.out.println(Thread.currentThread() + " Processed #" + message);
    if (message.equals("-1")) {
      long end = System.currentTimeMillis();
      System.out.println((end - Main.start) / 1000 + " seconds");
      System.exit(0);
    }
  }
}
