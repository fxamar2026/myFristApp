package com.example.centers.model;

import java.time.Instant;

public record Center(Long id, String code, String nameAr, String nameEn, boolean active, Instant createdAt) {
}
