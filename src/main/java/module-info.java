module com.instruction.emploiapp {
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.fxml;
    requires javafx.controls;
    requires java.desktop;

    opens com.instruction.emploiapp to javafx.fxml;
    exports com.instruction.emploiapp;
    exports com.instruction.emploiapp.controller;
    opens com.instruction.emploiapp.controller to javafx.fxml;
    exports com.instruction.emploiapp.model;
    opens com.instruction.emploiapp.model to javafx.fxml;
    exports com.instruction.emploiapp.db;
    opens com.instruction.emploiapp.db to javafx.fxml;
    exports com.instruction.emploiapp.serialisation;
    opens com.instruction.emploiapp.serialisation to javafx.fxml;
    opens com.instruction.emploiapp.controller.popup.toast to javafx.fxml;
    exports com.instruction.emploiapp.controller.popup.filter;
    opens com.instruction.emploiapp.controller.popup.filter to javafx.fxml;
    exports com.instruction.emploiapp.controller.popup.form;
    opens com.instruction.emploiapp.controller.popup.form to javafx.fxml;
    exports com.instruction.emploiapp.algo.excel;
    exports com.instruction.emploiapp.controller.table;
    opens com.instruction.emploiapp.controller.table to javafx.fxml;
}