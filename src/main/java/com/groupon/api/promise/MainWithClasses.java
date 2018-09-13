package com.groupon.api.promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.groupon.promise.AsyncPromiseFunction;
import com.groupon.promise.DefaultPromiseFuture;
import com.groupon.promise.Promise;
import com.groupon.promise.PromiseFuture;
import com.groupon.promise.PromiseImpl;
import com.groupon.promise.SyncPromiseFunction;
import com.groupon.promise.SyncPromiseListFunction;

public class MainWithClasses {

    public static void main(String[] args) {
        while (true) {
            List<Integer> numbers = new ArrayList<>();
            List<String> letters = new ArrayList<>();
            AtomicInteger sum = new AtomicInteger();
            Promise<String> topPromise = new PromiseImpl<>(); // the type is the input there's
            Promise<List<String>> plainValues = topPromise
                    .then(new Splitter()) // then will evaluate if the input is sync or async by checking the interface of the class
                    .then(new ArrayToList());

            Promise<String> valuesAsString = plainValues
                    .thenList(new ListOutFunction())
                    .map();

            valuesAsString
                    .then(new IsItANumberAsync()).optional(true) // you can change this to the IsItANumberSync function
                    .thenSync(e -> {
                        numbers.add(e);
                        return e;
                    }).thenSync(v -> {
                sum.addAndGet(v);
                return "";
            });

            valuesAsString.then((SyncPromiseFunction<String, String>) input -> {
                if (!isNumber(input)) {
                    letters.add(input);
                }
                return input;
            });
            topPromise.after().thenSync(
                    success -> {
                        System.out.println("numbers = " + numbers);
                        System.out.println("letters = " + letters);
                        System.out.println(plainValues.value());
                        System.out.println("sum = " + sum);
                        System.exit(0);
                        return null;
                    }, error -> {
                        error.printStackTrace();
                        System.err.println("error!!!!");
                        System.exit(1);
                        return null;
                    });
            topPromise.fulfill("a,b,c,d,1,2,3,4,5,6,7,8,9,g,h,i"); //this is the input
        }
    }

    /*
    Function classes
    */

    public static class Splitter implements // SyncPromiseFunction<T, O>
            SyncPromiseFunction<String, String[]> {

        // public O handle(T input) throws Throwable {
        @Override
        public String[] handle(String input) throws Throwable { //sync functions take an input T and return the output O
            return input.split(",");
        }
    }

    public static class ArrayToList implements
            AsyncPromiseFunction<String[], List<String>> {

        @Override
        public PromiseFuture<? extends List<String>> handle(String[] array) {
            PromiseFuture<List<String>> future = new DefaultPromiseFuture<>(); // async functions return a future
            future.setResult(Arrays.asList(array));
            return future;
        }
    }

    public static class ListOutFunction implements SyncPromiseListFunction<List<String>, String> {

        @Override
        public Collection<String> handle(List<String> strings) throws Throwable {
            return strings;
        }
    }

    public static class IsItANumberAsync implements AsyncPromiseFunction<String, Integer> {

        @Override
        public PromiseFuture<Integer> handle(String value) {
            PromiseFuture<Integer> future = new DefaultPromiseFuture<>();

            CompletableFuture.runAsync(() -> { // simulate an async call with completable future
                boolean number = isNumber(value);
                if (number) {
                    future.setResult(Integer.parseInt(value));
                } else {
                    future.setFailure(new NumberFormatException("invalid number: " + value));
                }
            });
            return future;
        }
    }

    public static class IsItANumberSync implements SyncPromiseFunction<String, Boolean> {
        @Override
        public Boolean handle(String value) {
            return isNumber(value);
        }
    }

    /*
    helper method
     */

    static final Pattern PATTERN = Pattern.compile("\\d+");

    private static boolean isNumber(String value) {
        return PATTERN.matcher(value).matches();
    }
}
