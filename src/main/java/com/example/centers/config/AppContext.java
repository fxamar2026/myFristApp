package com.example.centers.config;

import com.example.centers.db.DatabaseManager;
import com.example.centers.repository.CenterRepository;
import com.example.centers.service.CenterService;
import com.example.centers.service.ImportService;
import com.example.centers.service.SequenceService;

import java.nio.file.Path;

public class AppContext {
    private final DatabaseManager databaseManager;
    private final CenterService centerService;
    private final ImportService importService;
    private final SequenceService sequenceService;

    public AppContext(DatabaseManager databaseManager,
                      CenterService centerService,
                      ImportService importService,
                      SequenceService sequenceService) {
        this.databaseManager = databaseManager;
        this.centerService = centerService;
        this.importService = importService;
        this.sequenceService = sequenceService;
    }

    public static AppContext bootstrap() {
        DatabaseManager databaseManager = new DatabaseManager(Path.of("data/central.db"));
        databaseManager.initialize();

        CenterRepository centerRepository = new CenterRepository(databaseManager);
        CenterService centerService = new CenterService(centerRepository);
        ImportService importService = new ImportService(databaseManager);
        SequenceService sequenceService = new SequenceService(databaseManager);

        return new AppContext(databaseManager, centerService, importService, sequenceService);
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
}
