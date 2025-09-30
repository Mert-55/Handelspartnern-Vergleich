# Vendor Libraries Information

## Verzeichnisstruktur

```
/static/vendor/
├── htmx/
│   └── htmx.min.js (47KB) - HTMX 1.9.6
├── tailwindcss/
│   └── tailwind.js (350KB) - TailwindCSS Latest
├── fontawesome/
│   ├── all.min.css (78KB) - Font Awesome 6.4.0 CSS
│   └── webfonts/
│       ├── fa-solid-900.woff2 - Solid Icons
│       ├── fa-regular-400.woff2 - Regular Icons
│       └── fa-brands-400.woff2 - Brand Icons
├── fonts/
│   ├── inter.css - Inter Font CSS Definitions
│   ├── inter-300.ttf - Inter Light
│   ├── inter-400.ttf - Inter Regular
│   ├── inter-500.ttf - Inter Medium
│   ├── inter-600.ttf - Inter SemiBold
│   └── inter-700.ttf - Inter Bold
└── bootstrap/
    └── bootstrap.min.css (200KB) - Bootstrap 5.3.0
```

## HTML Integration

### index.html
```html
<script th:src="@{/vendor/htmx/htmx.min.js}"></script>
<script th:src="@{/vendor/tailwindcss/tailwind.js}"></script>
<link rel="stylesheet" th:href="@{/vendor/fontawesome/all.min.css}">
<style>
    @import url('/vendor/fonts/inter.css');
</style>
```

### error.html
```html
<link rel="stylesheet" th:href="@{/vendor/bootstrap/bootstrap.min.css}">
```

## Lizenz-Compliance

✅ **Alle Abhängigkeiten sind für kommerzielle Nutzung lizenziert**
✅ **Wissenschaftliche Arbeit konform**
✅ **Vollständig lokal gehostet (keine CDN-Dependencies)**

## Migration Completed

- [x] HTMX 1.9.6 → Lokal installiert
- [x] TailwindCSS → Lokal installiert  
- [x] Font Awesome 6.4.0 → Lokal installiert (CSS + Fonts)
- [x] Inter Font → Lokal installiert (alle Gewichte)
- [x] Bootstrap 5.3.0 → Lokal installiert
- [x] HTML Templates → Aktualisiert mit lokalen Pfaden
- [x] Lizenz-Dokumentation → Vollständig erstellt

**Status**: ✅ **MIGRATION ERFOLGREICH ABGESCHLOSSEN**
**Datum**: 30. September 2025