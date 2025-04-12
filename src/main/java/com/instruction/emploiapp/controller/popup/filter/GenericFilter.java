package com.instruction.emploiapp.controller.popup.filter;

import com.instruction.emploiapp.model.FilterCondition;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GenericFilter<T> {

    private final TableView<T> table;
    private final ObservableList<T> masterData;

    private final Map<String, Function<T, Object>> fieldExtractors = new HashMap<>();

    public GenericFilter(TableView<T> table, ObservableList<T> masterData) {
        this.table = table;
        this.masterData = masterData;
    }

    public void registerField(String fieldName, Function<T, Object> extractor) {
        fieldExtractors.put(fieldName, extractor);
    }

    public void applyFilters(ObservableList<FilterCondition> filters) {
        if (filters == null || filters.isEmpty()) {
            table.setItems(masterData);
            return;
        }

        ObservableList<T> filtered = masterData.filtered(item -> {
            for (FilterCondition fc : filters) {
                if (!matchesCondition(item, fc)) {
                    return false;
                }
            }
            return true;
        });

        table.setItems(filtered);
        table.refresh();
    }

    private boolean matchesCondition(T item, FilterCondition fc) {
        Function<T, Object> extractor = fieldExtractors.get(fc.field());
        if (extractor == null) {
            return false;
        }

        Object fieldValue = extractor.apply(item);
        if (fieldValue == null) {
            fieldValue = "";
        }

        String operator = fc.operator();
        String filterValue = fc.value();

        switch (operator) {
            case "Contient":
                return fieldValue.toString().toLowerCase().contains(filterValue.toLowerCase());

            case "=":
                return fieldValue.toString().equalsIgnoreCase(filterValue);

            case ">":
                try {
                    double itemVal = Double.parseDouble(fieldValue.toString());
                    double filterVal = Double.parseDouble(filterValue);
                    return itemVal > filterVal;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "<":
                try {
                    double itemVal = Double.parseDouble(fieldValue.toString());
                    double filterVal = Double.parseDouble(filterValue);
                    return itemVal < filterVal;
                } catch (NumberFormatException e) {
                    return false;
                }

            default:
                return false;
        }
    }
}
