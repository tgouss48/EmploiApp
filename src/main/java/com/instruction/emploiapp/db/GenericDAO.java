package com.instruction.emploiapp.db;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {
    List<T> getAll() throws SQLException;
    boolean exists(T item) throws SQLException;
    void insert(T item) throws SQLException;
    void delete(T item) throws SQLException;
}

