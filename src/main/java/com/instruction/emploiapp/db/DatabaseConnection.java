package com.instruction.emploiapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:DataBase.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        createTablesIfNotExist(conn);
        return conn;
    }

    private static void createTablesIfNotExist(Connection conn) {
        String createSalleTable = "CREATE TABLE IF NOT EXISTS Salle (" +
                "idSalle INTEGER PRIMARY KEY, " +
                "nom VARCHAR NOT NULL," +
                "type VARCHAR NOT NULL" +
                ");";

        String createMatiereTable = "CREATE TABLE IF NOT EXISTS Matiere (" +
                "nom VARCHAR PRIMARY KEY, " +
                "volumeHoraire REAL NOT NULL" +
                ");";

        String createInstructeurTable = "CREATE TABLE IF NOT EXISTS Instructeur (" +
                "id INTEGER PRIMARY KEY, " +
                "nom VARCHAR NOT NULL, " +
                "grade VARCHAR NOT NULL, " +
                "matiere VARCHAR NOT NULL, " +
                "idCie VARCHAR NOT NULL, " +
                "FOREIGN KEY (matiere) REFERENCES Matiere(nom)" +
                ");";

        String createSuiviTable = "CREATE TABLE IF NOT EXISTS Suivi (" +
                "idCie INTEGER, " +
                "matiere VARCHAR NOT NULL, " +
                "volumeHoraire REAL NOT NULL, " +
                "PRIMARY KEY (idCie, matiere), " +
                "FOREIGN KEY (matiere) REFERENCES Matiere(nom)" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSalleTable);
            stmt.executeUpdate(createMatiereTable);
            stmt.executeUpdate(createInstructeurTable);
            stmt.executeUpdate(createSuiviTable);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables : " + e.getMessage());
        }
    }
}