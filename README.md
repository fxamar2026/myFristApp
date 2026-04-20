# Center DB Manager (JavaFX + SQLite)

هذا المشروع يمثل **المرحلة الأولى** من مخطط نظام إدارة قواعد المراكز:

- تهيئة مشروع JavaFX على Java 21 وGradle.
- إنشاء قاعدة مركزية SQLite بشكل تلقائي.
- توفير شاشة رئيسية حديثة (Dashboard + Centers + Import).
- بناء وحدة إدارة المراكز (إضافة + عرض).
- بناء وحدة استيراد تجريبية (تسجيل دفعة استيراد في staging).

## التشغيل

```bash
gradle run
```

> يمكن تعديل مسار القاعدة المركزية من `AppContext` (حاليًا: `data/central.db`).

## ما تم تجهيزه

- طبقة قاعدة البيانات: `DatabaseManager`
- الكيانات الأساسية: `Center`
- مستودع المراكز: `CenterRepository`
- الخدمات:
  - `CenterService`
  - `SequenceService`
  - `ImportService`
- الواجهة:
  - `MainView`
  - `DashboardView`
  - `CentersView`
  - `ImportView`

## الخطوات التالية المقترحة

1. إضافة خدمة التشفير وفك التشفير (AES/GCM + إدارة مفاتيح).
2. دعم مراجعة سجلات staging قبل الدمج النهائي.
3. إضافة شاشة تدقيق وسجل عمليات متقدم.
4. استكمال i18n وRTL/LTR وثيمات Dark/Light.
