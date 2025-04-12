package com.instruction.emploiapp.model;

public record FilterCondition(String field, String operator, String value) {

    @Override
    public String toString() {
        return field + "  " + operator + "  " + value;
    }
}