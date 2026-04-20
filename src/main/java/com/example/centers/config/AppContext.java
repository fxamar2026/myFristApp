package com.example.centers.config;

import com.example.centers.db.DatabaseManager;
import com.example.centers.repository.CenterRepository;
import com.example.centers.repository.ImportRepository;
import com.example.centers.service.*;

import java.nio.file.Path;

public class AppContext {
    private final DatabaseManager databaseManager;
    private final CenterService centerService;
    private final ImportService importService;
    private final SequenceService sequenceService;
    private final MergeService mergeService;

    public AppContext(DatabaseManager databaseManager,
                      CenterService centerService,
                      ImportService importService,
                      SequenceService sequenceService,
                      MergeService mergeService) {
        this.databaseManager = databaseManager;
        this.centerService = centerService;
        this.importService = importService;
        this.sequenceService = sequenceService;
        this.mergeService = mergeService;
    }

    public static AppContext bootstrap() {
        DatabaseManager databaseManager = new DatabaseManager(Path.of("data/central.db"));
        databaseManager.initialize();

        CenterRepository centerRepository = new CenterRepository(databaseManager);
        SequenceService sequenceService = new SequenceService(databaseManager);
        CryptoService cryptoService = new CryptoService();
        ImportRepository importRepository = new ImportRepository(databaseManager);

        CenterService centerService = new CenterService(centerRepository);
        ImportService importService = new ImportService(importRepository, sequenceService, cryptoService);
        MergeService mergeService = new MergeService(databaseManager);

        return new AppContext(databaseManager, centerService, importService, sequenceService, mergeService);
    }

    public DatabaseManager databaseManager() {
        return databaseManager;
    }

    public CenterService centerService() {
        return centerService;
    }

    public ImportService importService() {
        return importService;
    }

    public SequenceService sequenceService() {
        return sequenceService;
    }

    public MergeService mergeService() {
        return mergeService;
    }
}
