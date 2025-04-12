package com.instruction.emploiapp.db;

import com.instruction.emploiapp.model.Matiere;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.instruction.emploiapp.algo.Valeurs.nbCompagnies;
import static com.instruction.emploiapp.db.DatabaseConnection.getConnection;

@SuppressWarnings("SqlNoDataSourceInspection")
public class MatiereDAO implements GenericDAO<Matiere> {



    public void insert(Matiere matiere) throws SQLException {
        String query = "INSERT INTO Matiere (nom, volumeHoraire) VALUES (?, ?)";

         try (Connection conn = getConnection();
              PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, matiere.getNom());
                stmt.setFloat(2, matiere.getVolumeHoraire());

                stmt.executeUpdate();

             for(int i=1;i<=nbCompagnies;i++){
                 String querySuivi = "INSERT INTO Suivi (idCie, matiere,volumeHoraire) VALUES (?, ?, ?)";
                 try (PreparedStatement stmtSuivi = conn.prepareStatement(querySuivi)) {
                     stmtSuivi.setInt(1, i);
                     stmtSuivi.setString(2, matiere.getNom());
                     stmtSuivi.setFloat(3, matiere.getVolumeHoraire());

                     stmtSuivi.executeUpdate();
                 }
             }
         }
    }

    public List<Matiere> getAll() throws SQLException {
        List<Matiere> matieres = new ArrayList<>();
        String query = "SELECT * FROM Matiere";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Matiere matiere = new Matiere(
                        rs.getString("nom"),
                        rs.getFloat("volumeHoraire")
                );
                matieres.add(matiere);
            }
        }
        return matieres;
    }

    public void update(Matiere matiere, String OriginalNom) throws SQLException {
        String query1 = "UPDATE Matiere SET nom = ?, volumeHoraire = ? WHERE nom = ?";
        String query2 = "UPDATE Suivi SET matiere = ?, volumeHoraire = ? WHERE matiere = ?";
        String query3 = "UPDATE Instructeur SET matiere = ? WHERE matiere = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(query1);
                 PreparedStatement stmt2 = conn.prepareStatement(query2);
                 PreparedStatement stmt3 = conn.prepareStatement(query3)) {

                stmt1.setString(1, matiere.getNom());
                stmt1.setFloat(2, matiere.getVolumeHoraire());
                stmt1.setString(3, OriginalNom);
                stmt1.executeUpdate();

                stmt2.setString(1, matiere.getNom());
                stmt2.setFloat(2, matiere.getVolumeHoraire());
                stmt2.setString(3, OriginalNom);
                stmt2.executeUpdate();

                stmt3.setString(1, matiere.getNom());
                stmt3.setString(2, OriginalNom);
                stmt3.executeUpdate();

                conn.commit();
            }
        }
    }

    public void delete(Matiere item) throws SQLException {
        String nom = item.getNom();

        String deleteMatiere = "DELETE FROM Matiere WHERE nom = ?";
        String deleteSuivi = "DELETE FROM Suivi WHERE matiere = ?";
        String deleteInstructeur = "DELETE FROM Instructeur WHERE matiere = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteMatiere);
                 PreparedStatement stmt2 = conn.prepareStatement(deleteSuivi);
                 PreparedStatement stmt3 = conn.prepareStatement(deleteInstructeur)) {

                stmt1.setString(1, nom);
                stmt1.executeUpdate();

                stmt2.setString(1, nom);
                stmt2.executeUpdate();

                stmt3.setString(1, nom);
                stmt3.executeUpdate();

                conn.commit();
            }
        }
    }

    public boolean exists(Matiere matiere) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Matiere WHERE nom = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matiere.getNom());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static ObservableList<String> getDistinctMatiereNames() throws SQLException {
        ObservableList<String> matiereList = FXCollections.observableArrayList();
        String query = "SELECT DISTINCT nom FROM Matiere";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                matiereList.add(rs.getString("nom"));
            }
        }
        return matiereList;
    }
}