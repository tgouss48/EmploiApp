package com.instruction.emploiapp.algo.excel;

@FunctionalInterface
public interface Inserer<T> {
    void insert(T obj) throws Exception;
}
