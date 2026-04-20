# Center DB Manager (JavaFX + SQLite)

نسخة محدثة من المرحلة الأولى مع توجه **أقوى + أحدث**:

- واجهة JavaFX محسّنة (Dashboard + Centers + Import + Review/Merge).
- تبديل اللغة الفوري (Arabic/English) مع RTL/LTR.
- تبديل الوضع الليلي/النهاري.
- Staging workflow: تسجيل دفعات الاستيراد + عرض المعلّق + اعتماد الدمج.
- خدمة تشفير AES/GCM مبدئية لتغليف payload قبل دخوله staging.
- مخطط قاعدة موسّع يشمل الجداول الأمنية والإدارية الأساسية.

## التشغيل

```bash
gradle run
```

## الوحدات الحالية

- Core bootstrap: `AppContext`, `DatabaseManager`
- Services:
  - `CenterService`
  - `SequenceService`
  - `ImportService`
  - `MergeService`
  - `CryptoService`
- UI:
  - `MainView`
  - `DashboardView`
  - `CentersView`
  - `ImportView`
  - `ReviewMergeView`

## ملاحظات

- التشفير المطبق حاليًا تجهيزي لتدفق العمل، ويمكن ترقيته لاحقًا لدمج إدارة مفاتيح احترافية.
- واجهة المراجعة/الدمج تعمل على السجلات `PENDING` وتدعم اعتمادها إلى `master_records` مع تسجيل `audit_log`.
