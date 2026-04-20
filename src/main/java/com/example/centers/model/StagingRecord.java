package com.example.centers.model;

public record StagingRecord(Long id, Long batchId, String centerCode, String generatedCode, String payloadJson, String status) {
}
