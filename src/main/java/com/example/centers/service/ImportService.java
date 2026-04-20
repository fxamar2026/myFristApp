package com.example.centers.service;

import com.example.centers.model.ImportBatch;
import com.example.centers.model.StagingRecord;
import com.example.centers.repository.ImportRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;

public class ImportService {
    private final ImportRepository importRepository;
    private final SequenceService sequenceService;
    private final CryptoService cryptoService;

    public ImportService(ImportRepository importRepository, SequenceService sequenceService, CryptoService cryptoService) {
        this.importRepository = importRepository;
        this.sequenceService = sequenceService;
        this.cryptoService = cryptoService;
    }

    public long registerImport(String centerCode, Path filePath, String centerPassword) {
        if (centerCode == null || centerCode.isBlank()) {
            throw new IllegalArgumentException("Center code is required");
        }
        if (filePath == null || !Files.exists(filePath)) {
            throw new IllegalArgumentException("Import file not found");
        }

        String normalized = centerCode.trim().toUpperCase();
        long batchId = importRepository.createBatch(normalized, filePath.getFileName().toString(), "STAGED");

        String payload = "{\"file\":\"%s\",\"size\":%d}".formatted(filePath.getFileName(), filePath.toFile().length());
        String encryptedPayload = cryptoService.encryptToBase64(payload.getBytes(), centerPassword, normalized.getBytes());
        String generatedCode = sequenceService.nextCode(normalized, Year.now().getValue());
        importRepository.addStagingRecord(batchId, normalized, generatedCode, encryptedPayload);

        return batchId;
    }

    public List<ImportBatch> recentBatches() {
        return importRepository.findRecentBatches();
    }

    public List<StagingRecord> pendingForCenter(String centerCode) {
        return importRepository.findPendingByCenter(centerCode.trim().toUpperCase());
    }
}
