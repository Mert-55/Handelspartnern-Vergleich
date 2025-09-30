# Font Awesome to Material Icons Migration Map

Diese Datei dokumentiert das Mapping von Font Awesome Icons zu Google Material Icons für die Handelspartner-Anwendung.

## Icon Mapping Tabelle

| Font Awesome Class | Material Icon Name | Beschreibung | Verwendung |
|-------------------|--------------------|--------------|------------|
| `fas fa-plus` | `add` | Plus/Hinzufügen Icon | Neue Partner hinzufügen, neue Einträge |
| `fas fa-plus-circle` | `add_circle` | Plus mit Kreis | Bestätigte Hinzufügungen |
| `fas fa-minus-circle` | `remove_circle` | Minus mit Kreis | Entfernungen, Stornierungen |
| `fas fa-times` | `close` | X/Schließen Icon | Modal schließen, Aktionen abbrechen |
| `fas fa-filter` | `filter_list` | Filter Icon | Listen filtern |
| `fas fa-search` | `search` | Lupe/Suche Icon | Suchfelder |
| `fas fa-spinner fa-spin` | `refresh` + Animation | Lade-Spinner | Ladevorgang anzeigen |
| `fas fa-users` | `group` | Mehrere Personen | Partnerliste |
| `fas fa-user-plus` | `person_add` | Person hinzufügen | Neuen Partner hinzufügen |
| `fas fa-user-circle` | `account_circle` | Benutzer mit Kreis | Benutzerdetails |
| `fas fa-sort` | `sort` | Sortierung | Tabellen sortieren |
| `fas fa-euro-sign` | `euro_symbol` | Euro Symbol | Finanzinformationen |
| `fas fa-save` | `save` | Speichern Icon | Formulare speichern |
| `fas fa-edit` | `edit` | Bearbeiten Icon | Datensätze bearbeiten |
| `fas fa-trash` | `delete` | Löschen Icon | Datensätze löschen |
| `fas fa-eye` | `visibility` | Auge/Anzeigen | Details anzeigen |
| `fas fa-clock` | `schedule` | Uhr/Zeit | Zeitstempel, Termine |
| `fas fa-arrow-up` | `keyboard_arrow_up` | Pfeil nach oben | Positive Werte, Navigation |
| `fas fa-arrow-down` | `keyboard_arrow_down` | Pfeil nach unten | Negative Werte, Navigation |
| `fas fa-download` | `download` | Download Icon | Dateien herunterladen |
| `fas fa-list-alt` | `list` | Liste Icon | Listenansicht |
| `fas fa-triangle-exclamation` | `warning` | Warnung | Fehlermeldungen |
| `fas fa-exclamation-circle` | `error` | Fehler mit Kreis | Validierungsfehler |
| `fas fa-info-circle` | `info` | Information | Hilfe und Tipps |
| `fas fa-file-contract` | `description` | Dokument | Verträge, Vereinbarungen |
| `fas fa-clipboard` | `assignment` | Klemmbrett | Notizen, Aufgaben |
| `fas fa-undo` | `undo` | Rückgängig | Aktionen rückgängig machen |
| `fas fa-check` | `check` | Häkchen | Bestätigungen |
| `fas fa-clipboard-check` | `assignment_turned_in` | Abgehakt | Erledigte Aufgaben |
| `fas fa-inbox` | `inbox` | Posteingang | Unbearbeitete Elemente |

## CSS-Implementierung

### Alte Font Awesome Syntax:
```html
<i class="fas fa-plus"></i>
```

### Neue Material Icons Syntax:
```html
<span class="material-icons">add</span>
```

## CSS-Klassen für Styling

### Größen:
```css
.material-icons.md-18 { font-size: 18px; }
.material-icons.md-24 { font-size: 24px; }
.material-icons.md-36 { font-size: 36px; }
.material-icons.md-48 { font-size: 48px; }
```

### Farben (entsprechend Tailwind):
```css
.material-icons.text-green-600 { color: #059669; }
.material-icons.text-red-600 { color: #dc2626; }
.material-icons.text-blue-600 { color: #2563eb; }
.material-icons.text-gray-400 { color: #9ca3af; }
```

## Animation für Spinner

```css
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.material-icons.spin {
  animation: spin 1s linear infinite;
}
```

## Lizenz Information

**Google Material Icons**
- **Lizenz**: Apache License 2.0
- **Kommerzielle Nutzung**: ✅ Vollständig erlaubt
- **Modifikation**: ✅ Erlaubt
- **Distribution**: ✅ Erlaubt
- **Private Nutzung**: ✅ Erlaubt
- **Wissenschaftliche Arbeit**: ✅ Konform