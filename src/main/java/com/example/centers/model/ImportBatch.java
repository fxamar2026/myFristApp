package com.example.centers.model;

import java.time.Instant;

public record ImportBatch(Long id, String centerCode, String fileName, String status, Instant createdAt) {
}
