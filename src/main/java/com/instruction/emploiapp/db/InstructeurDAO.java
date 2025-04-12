package com.instruction.emploiapp.db;

import com.instruction.emploiapp.model.Instructeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.instruction.emploiapp.db.DatabaseConnection.getConnection;

@SuppressWarnings("SqlNoDataSourceInspection")
public class InstructeurDAO implements GenericDAO<Instructeur> {

    public void insert(Instructeur instructeur) throws SQLException {
        try (Connection conn = getConnection()) {

            String query = "INSERT INTO Instructeur (id, nom, grade, matiere, idCie) VALUES (?, ?, ?, ?, ?)";
            int id = getNextAvailableId();
            instructeur.setID(id);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.setString(2, instructeur.getNom());
                stmt.setString(3, instructeur.getGrade());
                stmt.setString(4, instructeur.getMatiere());
                stmt.setString(5, instructeur.getIdCie());

                stmt.executeUpdate();
            }
        }
    }

    public List<Instructeur> getAll() throws SQLException {
        List<Instructeur> instructeurs = new ArrayList<>();
        String query = "SELECT * FROM Instructeur";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Instructeur instructeur = new Instructeur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("grade"),
                        rs.getString("matiere"),
                        rs.getString("idCie")
                );
                instructeurs.add(instructeur);
            }
        }
        return instructeurs;
    }

    public void update(Instructeur instructeur) throws SQLException {
        String query = "UPDATE Instructeur SET nom = ?, grade = ?, matiere = ?, idCie = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, instructeur.getNom());
            stmt.setString(2, instructeur.getGrade());
            stmt.setString(3, instructeur.getMatiere());
            stmt.setString(4, instructeur.getIdCie());
            stmt.setInt(5, instructeur.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Instructeur item) throws SQLException {
        String query = "DELETE FROM Instructeur WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, item.getId());
            stmt.executeUpdate();
        }
    }

    public boolean exists(Instructeur inst) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Instructeur WHERE nom = ? AND grade = ? AND matiere = ? AND idCie = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, inst.getNom());
            ps.setString(2, inst.getGrade());
            ps.setString(3, inst.getMatiere());
            ps.setString(4, inst.getIdCie());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    public static int getNextAvailableId() throws SQLException {
        String query = "SELECT id FROM Instructeur ORDER BY id";
        List<Integer> existingIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id"));
            }
        }

        int newId = 1;
        for (int i = 1; i <= existingIds.size(); i++) {
            if (!existingIds.contains(i)) {
                newId = i;
                break;
            } else {
                newId = existingIds.size() + 1;
            }
        }
        return newId;
    }
}