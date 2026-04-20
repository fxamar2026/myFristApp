package com.example.centers.service;

import com.example.centers.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class MergeService {
    private final DatabaseManager databaseManager;

    public MergeService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public int approvePendingRecords(String centerCode) {
        String selectSql = "SELECT id, center_code, generated_code, payload_json FROM staging_records WHERE center_code = ? AND status = 'PENDING'";
        String insertMasterSql = "INSERT OR IGNORE INTO master_records (center_code, generated_code, payload_json, merged_at) VALUES (?, ?, ?, ?)";
        String updateStagingSql = "UPDATE staging_records SET status = 'APPROVED' WHERE id = ?";
        String auditSql = "INSERT INTO audit_log (event_type, details, created_at) VALUES (?, ?, ?)";

        try (var connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);
            int count = 0;

            try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                select.setString(1, centerCode);
                var rs = select.executeQuery();

                while (rs.next()) {
                    try (PreparedStatement insertMaster = connection.prepareStatement(insertMasterSql);
                         PreparedStatement updateStaging = connection.prepareStatement(updateStagingSql)) {
                        insertMaster.setString(1, rs.getString("center_code"));
                        insertMaster.setString(2, rs.getString("generated_code"));
                        insertMaster.setString(3, rs.getString("payload_json"));
                        insertMaster.setString(4, Instant.now().toString());
                        insertMaster.executeUpdate();

                        updateStaging.setLong(1, rs.getLong("id"));
                        updateStaging.executeUpdate();
                        count++;
                    }
                }
            }

            try (PreparedStatement audit = connection.prepareStatement(auditSql)) {
                audit.setString(1, "MERGE_APPROVED");
                audit.setString(2, "Approved " + count + " staging records for center " + centerCode);
                audit.setString(3, Instant.now().toString());
                audit.executeUpdate();
            }

            connection.commit();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to merge staging records", e);
        }
    }
}
