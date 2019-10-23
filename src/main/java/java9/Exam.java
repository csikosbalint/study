package java9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Exam {
    public static void main(String[] args) {
        Exam exam = new Exam();
//        List res = exam.prioritizedOrders(5,
//                Arrays.asList(
//                        "mi2 jog mid pet",
//                        "wz3 34 54 398",
//                        "a1 alps cow bar",
//                        "a2 alps cow bar",
//                        "x4 45 21 7"
//                ));
//        res.stream().forEach(System.out::println);

    }

    int minimumDistance(int numRows, int numColumns, List<List<Integer>> area)
    {
        try {
            return CompletableFuture.supplyAsync( () -> {
                int counter = 0;
                try {
                    boolean working = isTargetHasAccessToRoadOrTruck(area, counter);
                    while ( working ) {
                        working = isTargetHasAccessToRoadOrTruck(area, counter);
                    }
                } catch (RuntimeException e) {
                    //found the shortest way
                    return Integer.valueOf(e.getMessage());
                }
                return -1;
            }).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This algorithm would start from the target (marked by a 9) and change all negihbouring cells with 1 to 9. Doing
     * it in a loop (exception can interrupt it only) finally a 9 will have a neighbour cell with value of 8 (truck at
     * the top left).
     * @param originalArea
     * @param counter
     * @return always true; if some of the 9s (visited cells from target) reached 8 (truck) then an exception with the
     * counter value has been thrown
     */
    private boolean isTargetHasAccessToRoadOrTruck(List<List<Integer>> originalArea, int counter) {
        if ( originalArea.stream().allMatch( l -> l.stream().allMatch(e -> e == 5 ))) {
            throw new RuntimeException(String.valueOf(counter));
        }
        List<List<Integer>> temp = new ArrayList<>();
        for (List<Integer> list : originalArea) {
            temp.add(Collections.unmodifiableList(list));
        }
        List<List<Integer>> area = Collections.unmodifiableList(temp);

        // mark truck
        if ( area.get(0).get(0) != 8 ) area.get(0).set(0, 8);

        for ( int x = 0; x < area.size(); x++ ) {
            for ( int y = 0; y < area.get(x).size(); y++ ) {
                // this will modify
            }
        }
        return true;
    }

    public List<String> prioritizedOrders(int numOrders, List<String> orderList) {
        if (numOrders != orderList.size() || !orderList.stream().allMatch(e -> e.contains(" "))) {
            throw new IllegalArgumentException("List or order number contain error");
        }
        if (orderList.isEmpty()) return new ArrayList<String>();

        List<String> ret = new ArrayList<>();
        CompletableFuture<List<String>> prime = CompletableFuture.supplyAsync(() -> orderList.stream()
                .filter(e -> {
                    String[] meta = Arrays.copyOfRange(e.split(" "), 1, e.split(" ").length);
                    return Arrays.asList(meta).stream().allMatch(s -> s.matches("[a-z]+"));
                })
                .collect(Collectors.toList()));
        CompletableFuture<List<String>> other = CompletableFuture.supplyAsync(() -> orderList.stream()
                .filter(e -> {
                    String[] meta = Arrays.copyOfRange(e.split(" "), 1, e.split(" ").length);
                    return Arrays.asList(meta).stream().allMatch(s -> s.matches("[0-9]+"));
                })
                .collect(Collectors.toList()));

        try {
            ret = prime.thenCombine(other, (p, o) -> {
                p.sort((o1, o2) -> {

                    String str1 = String.join(" ",Arrays.copyOfRange(o1.split(" "), 1, o1.split(" ").length));
                    String str2 = String.join(" ",Arrays.copyOfRange(o2.split(" "), 1, o2.split(" ").length));
                    for (int i = 0; i < str1.length() &&
                            i < str2.length(); i++) {
                        if ((int)str1.charAt(i) ==
                                (int)str2.charAt(i)) {
                            continue;
                        }
                        else {
                            return (int)str1.charAt(i) -
                                    (int)str2.charAt(i);
                        }
                    }

                    // Edge case for strings like
                    // String 1="Geeky" and String 2="Geekyguy"
                    if (str1.length() < str2.length()) {
                        return (str1.length()-str2.length());
                    }
                    else if (str1.length() > str2.length()) {
                        return (str1.length()-str2.length());
                    }

                    // If none of the above conditions is true,
                    // it implies both the strings are equal
                    else {
                        String id1 = o1.split(" ")[0];
                        String id2 = o1.split(" ")[0];
                        return id1.compareTo(id2);
                    }
                });
                p.addAll(o);
                return p;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ret;
    }
}
