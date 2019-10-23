package java9;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
    static int counter = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] arr = IntStream.range(1, 13).toArray();
        Long start = System.currentTimeMillis();
        int r = gcd(arr, arr.length);
        Long stop = System.currentTimeMillis();
        System.out.println("result " + (stop - start) + " ms: " + r);
        System.out.println("---");
        start = System.currentTimeMillis();
        ForkJoinPool pool = new ForkJoinPool(8);
        r = gcdAsync(arr, arr.length, pool).get();
        stop = System.currentTimeMillis();
        System.out.println("result " + (stop - start) + " ms: " + r);
//        int a = IntStream.range(0, 12).boxed()
//                .parallel()
//                .filter(i -> i % 2 == 0)
//                .findAny()
//        .get();
//        System.out.println(a);
//                .forEachOrdered(System.out::println);
    }

    private static int gcd(int[] ints, int i) {
        System.out.println(i);
        if (i == 1) {
            System.out.println("a:" + ints[0]);
            return ints[0];
        }
        if (i > 2) {
            int a = gcd(
                    Arrays.copyOfRange(ints, 0, ((i / 2 + i % 2))),
                    Arrays.copyOfRange(ints, 0, ((i / 2 + i % 2))).length);
            int b = gcd(
                    Arrays.copyOfRange(ints, ((i / 2 + i % 2)), i),
                    Arrays.copyOfRange(ints, ((i / 2 + i % 2)), i).length);
            return gcd(new int[]{a, b}, 2);
        }
        // 2
        try {
            TimeUnit.MILLISECONDS.sleep(1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("a:" + ints[0] + " b:" + ints[1]);
        return gcd(ints[0], ints[1]);
    }


    private static int gcd(int a, int b) {
        // EUCLIDEAN ALGORITHM
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        if (a > b) {
            return gcd(b, a % b);
        } else {
            return gcd(a, b % a);
        }
    }


    private static CompletableFuture<Integer> gcdAsync(int[] ints, int i, ForkJoinPool pool) throws ExecutionException, InterruptedException {
//        System.out.println(i);
        if (i == 1) {
            System.out.println(++counter + ") a:" + ints[0]
                    + " " + Thread.currentThread().getName());
            return CompletableFuture.supplyAsync(() -> ints[0], pool);
        }
        if (i > 2) {
            return CompletableFuture.supplyAsync(() -> {
                        try {
                            CompletableFuture<Integer> a = gcdAsync(
                                    Arrays.copyOfRange(ints, 0, ((i / 2 + i % 2))),
                                    Arrays.copyOfRange(ints, 0, ((i / 2 + i % 2))).length, pool);
                            CompletableFuture<Integer> b = gcdAsync(
                                    Arrays.copyOfRange(ints, ((i / 2 + i % 2)), i),
                                    Arrays.copyOfRange(ints, ((i / 2 + i % 2)), i).length, pool);
                            CompletableFuture.allOf(a, b);
//                            a.thenCombine(b, (x, y) -> {
//                                try {
//                                    return gcdAsync(new int[]{x, y}, 2, pool);
//                                } catch (ExecutionException e) {
//                                    e.printStackTrace();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                return null;
//                            });
//                            return a.get();
//                             wait for both and return
                            return gcdAsync(new int[]{a.get(), b.get()}, 2, pool).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    },
                    pool
            );
        }
        // 2
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(++counter + ") a:" + ints[0] + " b:" + ints[1]
                + " " + Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() -> gcd(ints[0], ints[1]), pool);
    }
}