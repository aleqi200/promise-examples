package com.groupon.api.promise;

import com.groupon.promise.DefaultPromiseFuture;
import com.groupon.promise.Promise;
import com.groupon.promise.PromiseFuture;
import com.groupon.promise.PromiseImpl;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Promise<String> topPromise = new PromiseImpl<>();
        topPromise.thenSync(input -> input.split(","))
                .thenAsync(array -> {
                    PromiseFuture<List<String>> future = new DefaultPromiseFuture<>();
                    future.setResult(Arrays.asList(array));
                    return future;
                }).thenListSync(list -> list)
                .map()
                .thenSync(value -> {
                    try {
                        Integer.parseInt(value);
                        System.out.println("value = " + value + ", is a number");
                        return true;
                    } catch (NumberFormatException ex) {
                        System.out.println("value = " + value + ", is NOT a number");
                        return false;
                    }
                });
        topPromise.fulfill("a,b,c,d,1,2");
    }
}
