package com.groupon.api.promise;

import java.util.regex.Pattern;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MainWithRX {

    public static void main(String[] args) {
        Single.just("a")
                .subscribe(v -> System.out.println("v = " + v));
        Completable.fromAction(() -> {})
        .subscribe(() -> System.out.println("complete"));
    }

    /*
    helper method
     */

    static final Pattern PATTERN = Pattern.compile("\\d+");

    private static boolean isNumber(String value) {
        return PATTERN.matcher(value).matches();
    }
}
