package com.instruction.emploiapp.db;

import com.instruction.emploiapp.model.Emploi;
import com.instruction.emploiapp.model.Seance;
import com.instruction.emploiapp.model.Suivi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SuiviDAO implements GenericDAO<Suivi> {

    @Override
    public boolean exists(Suivi item) throws SQLException {
        return false;
    }

    @Override
    public void insert(Suivi item) {

    }

    @Override
    public void delete(Suivi item) {

    }

    public ObservableList<Suivi> getAll() throws SQLException {
        ObservableList<Suivi> suivis = FXCollections.observableArrayList();
        String query = "SELECT * FROM Suivi";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Suivi suivi = new Suivi(
                        rs.getInt("idCie"),
                        rs.getString("matiere"),
                        rs.getFloat("volumeHoraire")
                );
                suivis.add(suivi);
            }
        }
        return suivis;
    }

    public static void update(Suivi suivi) throws SQLException {
        String query = "UPDATE Suivi SET volumeHoraire = ? WHERE idCie = ? AND matiere = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setFloat(1, suivi.getVolumeHoraire());
            stmt.setInt(2, suivi.getIdCie());
            stmt.setString(3, suivi.getMatiere());
            stmt.executeUpdate();
        }
    }

    private static Suivi trouverSuivi(int idCie, String matiere) throws SQLException {
        Suivi suivi = null;
        String query = "SELECT * FROM Suivi WHERE idCie = ? AND matiere = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idCie);
            stmt.setString(2, matiere);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                suivi = new Suivi(
                        rs.getInt("idCie"),
                        rs.getString("matiere"),
                        rs.getFloat("volumeHoraire")
                );
            }
        }
        return suivi;
    }

    public static void decrementerVolumesHoraires(Emploi emploi) throws SQLException {
        for (Seance seance : emploi.getSeances()) {
            Suivi suivi = trouverSuivi(Integer.parseInt(seance.getCie().split(" ")[1]), seance.getMatiere());
            if (suivi != null) {
                float nouveauVolumeHoraire = (float) (suivi.getVolumeHoraire() - 1.5);
                suivi.setVolumeHoraire(nouveauVolumeHoraire);
                try {
                    SuiviDAO.update(suivi);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}