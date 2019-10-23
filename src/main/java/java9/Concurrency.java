package java9;

import java.math.BigDecimal;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Concurrency {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(
                () -> {
                    BigDecimal a = BigDecimal.valueOf(Math.pow(999,102));
                    System.out.println(Thread.currentThread());
                    System.out.println(a);
                }
        );
        System.out.println(Thread.currentThread());
    }
}
