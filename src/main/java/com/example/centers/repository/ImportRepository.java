package com.example.centers.repository;

import com.example.centers.db.DatabaseManager;
import com.example.centers.model.ImportBatch;
import com.example.centers.model.StagingRecord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ImportRepository {
    private final DatabaseManager databaseManager;

    public ImportRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public long createBatch(String centerCode, String fileName, String status) {
        String sql = "INSERT INTO import_batches (center_code, file_name, status, created_at) VALUES (?, ?, ?, ?)";
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, centerCode);
            statement.setString(2, fileName);
            statement.setString(3, status);
            statement.setString(4, Instant.now().toString());
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getLong(1);
            }
            throw new IllegalStateException("No batch id generated");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create import batch", e);
        }
    }

    public void addStagingRecord(long batchId, String centerCode, String generatedCode, String payloadJson) {
        String sql = "INSERT INTO staging_records (batch_id, center_code, generated_code, payload_json, status) VALUES (?, ?, ?, ?, 'PENDING')";
        try (var connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, batchId);
            statement.setString(2, centerCode);
            statement.setString(3, generatedCode);
            statement.setString(4, payloadJson);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to add staging record", e);
        }
    }

    public List<StagingRecord> findPendingByCenter(String centerCode) {
        String sql = "SELECT id, batch_id, center_code, generated_code, payload_json, status FROM staging_records WHERE center_code = ? AND status = 'PENDING' ORDER BY id DESC";
        List<StagingRecord> records = new ArrayList<>();

        try (var connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, centerCode);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                records.add(new StagingRecord(
                        rs.getLong("id"),
                        rs.getLong("batch_id"),
                        rs.getString("center_code"),
                        rs.getString("generated_code"),
                        rs.getString("payload_json"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to list staging records", e);
        }

        return records;
    }

    public List<ImportBatch> findRecentBatches() {
        String sql = "SELECT id, center_code, file_name, status, created_at FROM import_batches ORDER BY id DESC LIMIT 30";
        List<ImportBatch> batches = new ArrayList<>();
        try (var connection = databaseManager.getConnection();
             var statement = connection.prepareStatement(sql);
             var rs = statement.executeQuery()) {
            while (rs.next()) {
                batches.add(new ImportBatch(
                        rs.getLong("id"),
                        rs.getString("center_code"),
                        rs.getString("file_name"),
                        rs.getString("status"),
                        Instant.parse(rs.getString("created_at"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to list import batches", e);
        }
        return batches;
    }
}
