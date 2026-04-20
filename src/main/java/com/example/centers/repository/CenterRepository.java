package com.example.centers.repository;

import com.example.centers.db.DatabaseManager;
import com.example.centers.model.Center;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CenterRepository {
    private final DatabaseManager databaseManager;

    public CenterRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Center> findAll() {
        String sql = "SELECT id, code, name_ar, name_en, active, created_at FROM centers ORDER BY code";
        List<Center> centers = new ArrayList<>();

        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                centers.add(map(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to load centers", e);
        }

        return centers;
    }

    public void save(String code, String nameAr, String nameEn) {
        String sql = "INSERT INTO centers (code, name_ar, name_en, active, created_at) VALUES (?, ?, ?, 1, ?)";
        try (var connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, nameAr);
            statement.setString(3, nameEn);
            statement.setString(4, Instant.now().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to save center", e);
        }
    }

    private Center map(ResultSet resultSet) throws SQLException {
        return new Center(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("name_ar"),
                resultSet.getString("name_en"),
                resultSet.getInt("active") == 1,
                Instant.parse(resultSet.getString("created_at"))
        );
    }
}
