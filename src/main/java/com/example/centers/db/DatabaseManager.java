package com.example.centers.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class DatabaseManager {
    private final Path dbPath;

    public DatabaseManager(Path dbPath) {
        this.dbPath = dbPath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void initialize() {
        try {
            Path parent = dbPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create database folder", e);
        }

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS centers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        code TEXT NOT NULL UNIQUE,
                        name_ar TEXT NOT NULL,
                        name_en TEXT,
                        active INTEGER NOT NULL DEFAULT 1,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS center_keys (
                        center_code TEXT PRIMARY KEY,
                        key_ref TEXT NOT NULL,
                        encrypted_secret TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS center_sequences (
                        center_code TEXT NOT NULL,
                        year INTEGER NOT NULL,
                        last_sequence INTEGER NOT NULL,
                        updated_at TEXT NOT NULL,
                        PRIMARY KEY (center_code, year)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS import_batches (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        center_code TEXT NOT NULL,
                        file_name TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS export_batches (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        center_code TEXT NOT NULL,
                        type TEXT NOT NULL,
                        status TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS staging_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        batch_id INTEGER NOT NULL,
                        center_code TEXT NOT NULL,
                        generated_code TEXT NOT NULL,
                        payload_json TEXT NOT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        FOREIGN KEY (batch_id) REFERENCES import_batches(id)
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS master_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        center_code TEXT NOT NULL,
                        generated_code TEXT NOT NULL UNIQUE,
                        payload_json TEXT NOT NULL,
                        merged_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        active INTEGER NOT NULL DEFAULT 1,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS roles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        role_name TEXT NOT NULL UNIQUE,
                        description TEXT
                    );
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS audit_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        event_type TEXT NOT NULL,
                        details TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    );
                    """);

            statement.execute("INSERT OR IGNORE INTO roles (id, role_name, description) VALUES (1, 'ADMIN', 'System administrator')");
            statement.execute("INSERT OR IGNORE INTO audit_log (id, event_type, details, created_at) VALUES (1, 'INIT', 'Database initialized', '" + Instant.now() + "')");
        } catch (SQLException e) {
            throw new RuntimeException("Unable to initialize database", e);
        }
    }
}
