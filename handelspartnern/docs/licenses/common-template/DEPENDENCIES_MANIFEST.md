# Dependencies Manifest - Common Template Module

Dieses Dokument listet alle externen Dependencies und deren Versionen auf, die im `common-template` Modul verwendet werden.

## Frontend Dependencies

### 1. HTMX
- **Version**: 1.9.6
- **Typ**: JavaScript Library
- **Zweck**: AJAX, CSS Transitions, WebSockets und Server Sent Events
- **Lizenz**: BSD 2-Clause License
- **Quelle**: https://unpkg.com/htmx.org@1.9.6/dist/htmx.min.js
- **Lokaler Pfad**: `/static/vendor/htmx/htmx.min.js`
- **Größe**: ~47KB (minified)

### 2. TailwindCSS
- **Version**: Latest (CDN)
- **Typ**: CSS Framework
- **Zweck**: Utility-first CSS Framework für schnelles UI Development
- **Lizenz**: MIT License
- **Quelle**: https://cdn.tailwindcss.com
- **Lokaler Pfad**: `/static/vendor/tailwindcss/tailwind.js`
- **Größe**: ~350KB (with all utilities)

### 3. Font Awesome
- **Version**: 6.4.0
- **Typ**: Icon Library
- **Zweck**: SVG und Font Icons für UI Elements
- **Lizenz**: 
  - Icons: CC BY 4.0 License
  - Fonts: SIL OFL 1.1 License
  - Code: MIT License
- **Quelle**: https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/
- **Lokale Pfade**:
  - CSS: `/static/vendor/fontawesome/all.min.css`
  - Fonts: `/static/vendor/fontawesome/webfonts/`
- **Größe**: ~78KB CSS + ~580KB Fonts

### 4. Inter Font Family
- **Version**: v20 (Google Fonts)
- **Typ**: Web Font
- **Zweck**: Primary Typography für die Anwendung
- **Lizenz**: SIL Open Font License 1.1
- **Quelle**: https://fonts.googleapis.com/css2?family=Inter
- **Lokale Pfade**:
  - CSS: `/static/vendor/fonts/inter.css`
  - Fonts: `/static/vendor/fonts/inter-*.ttf`
- **Gewichte**: 300, 400, 500, 600, 700
- **Größe**: ~180KB (alle Gewichte)

### 5. Bootstrap (Error Pages)
- **Version**: 5.3.0
- **Typ**: CSS Framework
- **Zweck**: Styling für Error Pages
- **Lizenz**: MIT License
- **Quelle**: https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/
- **Lokaler Pfad**: `/static/vendor/bootstrap/bootstrap.min.css`
- **Größe**: ~200KB

## Kompatibilität und Browser-Support

### Browser-Kompatibilität
- **Chrome**: 90+
- **Firefox**: 88+
- **Safari**: 14+
- **Edge**: 90+

### Mobile Support
- **iOS Safari**: 14+
- **Chrome Mobile**: 90+
- **Samsung Internet**: 14+

## Performance Metriken

### Geschätzte Bundle-Größe
- **Total CSS**: ~358KB
- **Total JavaScript**: ~397KB
- **Total Fonts**: ~760KB
- **Gesamtgröße**: ~1.5MB

### Loading-Optimierungen
- Alle Ressourcen sind lokal gehostet (keine CDN-Latenz)
- Font-Display: swap für bessere Performance
- Minified und komprimierte Dateien

## Sicherheitsaspekte

### Keine externen Abhängigkeiten
- Alle Ressourcen sind lokal gehostet
- Keine Third-Party CDN Dependencies
- Keine Cross-Origin Requests
- CSP (Content Security Policy) kompatibel

### Lizenz-Compliance
- Alle Lizenzen erlauben kommerzielle Nutzung
- Attribution wird korrekt in Lizenz-Dateien geführt
- Wissenschaftliche Arbeit konform

## Wartung und Updates

### Update-Strategie
1. Regelmäßige Überprüfung auf Security-Updates
2. Version-Pinning für Stabilität
3. Testing vor Updates in Produktionsumgebung

### Letzte Aktualisierung
**Datum**: 30. September 2025
**Verantwortlich**: Automatisierte Abhängigkeits-Migration
**Status**: Instabil