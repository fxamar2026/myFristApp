package com.example.centers.service;

import com.example.centers.db.DatabaseManager;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class ImportService {
    private final DatabaseManager databaseManager;

    public ImportService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void registerImport(String centerCode, Path filePath) {
        String sql = "INSERT INTO import_batches (center_code, file_name, status, created_at) VALUES (?, ?, ?, ?)";

        try (var connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, centerCode);
            statement.setString(2, filePath.getFileName().toString());
            statement.setString(3, "STAGED");
            statement.setString(4, Instant.now().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to register import file", e);
        }
    }
}
