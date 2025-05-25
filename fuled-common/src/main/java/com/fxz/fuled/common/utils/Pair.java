package com.fxz.fuled.common.utils;

public class Pair<A, B> {

    private final A first;

    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> with(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
