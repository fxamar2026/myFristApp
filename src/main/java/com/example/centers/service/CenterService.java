package com.example.centers.service;

import com.example.centers.model.Center;
import com.example.centers.repository.CenterRepository;

import java.util.List;

public class CenterService {
    private final CenterRepository centerRepository;

    public CenterService(CenterRepository centerRepository) {
        this.centerRepository = centerRepository;
    }

    public List<Center> listCenters() {
        return centerRepository.findAll();
    }

    public void createCenter(String code, String nameAr, String nameEn) {
        String normalizedCode = code == null ? "" : code.trim().toUpperCase();
        if (normalizedCode.isBlank()) {
            throw new IllegalArgumentException("Center code is required");
        }
        if (nameAr == null || nameAr.isBlank()) {
            throw new IllegalArgumentException("Arabic name is required");
        }

        centerRepository.save(normalizedCode, nameAr.trim(), nameEn == null ? "" : nameEn.trim());
    }
}
