package com.example.centers.service;

import com.example.centers.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class SequenceService {
    private final DatabaseManager databaseManager;

    public SequenceService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String nextCode(String centerCode, int year) {
        int next = upsertAndGetNext(centerCode, year);
        return "%s-%d-%06d".formatted(centerCode, year, next);
    }

    private int upsertAndGetNext(String centerCode, int year) {
        String selectSql = "SELECT last_sequence FROM center_sequences WHERE center_code = ? AND year = ?";
        String insertSql = "INSERT INTO center_sequences (center_code, year, last_sequence, updated_at) VALUES (?, ?, ?, ?)";
        String updateSql = "UPDATE center_sequences SET last_sequence = ?, updated_at = ? WHERE center_code = ? AND year = ?";

        try (var connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            int next;
            try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                select.setString(1, centerCode);
                select.setInt(2, year);
                ResultSet resultSet = select.executeQuery();
                if (resultSet.next()) {
                    next = resultSet.getInt("last_sequence") + 1;
                    try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                        update.setInt(1, next);
                        update.setString(2, Instant.now().toString());
                        update.setString(3, centerCode);
                        update.setInt(4, year);
                        update.executeUpdate();
                    }
                } else {
                    next = 1;
                    try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                        insert.setString(1, centerCode);
                        insert.setInt(2, year);
                        insert.setInt(3, next);
                        insert.setString(4, Instant.now().toString());
                        insert.executeUpdate();
                    }
                }
            }

            connection.commit();
            return next;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to generate sequence", e);
        }
    }
}
