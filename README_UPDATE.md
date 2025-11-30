# SkyblocknNSSV - Update Utils3 & Utils4

## Nowe Systemy

### 1. System Klanów (Utils3)
Pełny system klanów z tworzeniem, zapraszaniem, wyrzucaniem i zarządzaniem.

**Komendy:**
- `/clan create <tag>` - Utwórz klan (poziom 15+, tag 3-4 znaki)
- `/clan delete` - Usuń klan (tylko lider)
- `/clan invite <gracz>` - Zaproś gracza (limit 8 członków)
- `/clan join <tag>` - Dołącz do klanu
- `/clan leave` - Opuść klan
- `/clan kick <gracz>` - Wyrzuć gracza
- `/clan info [tag]` - Informacje o klanie
- `/clan color` - Zmień kolor (w budowie)

### 2. Eventy Globalne (Utils3)
Globalne eventy zwiększające nagrody.

**Komendy:**
- `/event start DoubleXP` - Podwójne XP
- `/event start JobBoost` - Zwiększone zarobki
- `/event stop <typ>` - Zatrzymaj event

**Permisja:** `*` (tylko dla adminów)

### 3. Ulepszenia Wyspy (Utils3)
System upgrade'ów wyspy.

**Komenda:** `/upgrades`

**Dostępne ulepszenia:**
- Hopper Limit
- Spawner Limit  
- Island Size
- Members Limit
- Minions Limit

### 4. System Kitów (Utils3)
Integracja z PlayerKits2.

**Komendy:** `/kit`, `/kits`, `/claimkit`

**Dostępne kity:**
- Food (wszyscy)
- Default (Default+)
- Gold (Gold+)
- Diamond (Diamond+)
- Emerald (Emerald+)
- Netherite (Netherite+)
- Keys (wszyscy)

### 5. Rangi VIP (Utils3)
Prezentacja rang VIP i ich funkcji.

**Komendy:** `/rank`, `/ranks`

**Rangi:**
| Ranga | Cooldown | Domy | Funkcje |
|-------|----------|------|---------|
| Gold | 24h | +2 | Kit, /heal |
| Diamond | 20h | +4 | Kit, /heal, kolorowy chat |
| Emerald | 16h | +6 | Kit, /heal, chat, emoji |
| Netherite | 12h | +8 | Kit, /heal, chat, emoji, priorytet |

### 6. System Poziomów (Utils4)
GUI z poziomami i nagrodami.

**Komenda:** `/level`

**Funkcje:**
- 5 stron GUI
- Poziomy: 1-10, 15, 20, 25, 50
- Wyświetlanie nagród
- Paginacja

### 7. Customizacja Czatu (Utils4)
Personalizacja wyglądu czatu.

**Komenda:** `/nick`

**Funkcje:**
- Kolory nicku
- Kolory wiadomości
- Pogrubienie (Bold)
- Gradienty
- Płatność: Gems

### 8. Mining Boost (Utils4)
Automatyczne spawny boost padów w kopalni.

**Funkcje:**
- Spawn co 3 minuty
- Haste II (30s) lub Haste III (90s - rzadki)
- Efekty wizualne
- Dźwięki

### 9. Inne Komendy
- `/store` - Link do sklepu
- `/heal` - Uleczenie (cooldown 30s)
- `/top` - Teleportacja na top

## Pełna Lista Komend (31)

1. `/xqeffects` - Efekty
2. `/emoji` - Emoji
3. `/job` - Prace
4. `/mute` - Wyciszanie
5. `/unmute` - Odciszanie
6. `/chatgame` - Gra czatowa
7. `/vote` - Głosowanie
8. `/discord` - Link do Discorda
9. `/keys` - Klucze
10. `/votereward` - Nagroda za głosy
11. `/coinflip` - Coinflip
12. `/home` - Teleportacja do domu
13. `/sethome` - Ustawienie domu
14. `/delhome` - Usunięcie domu
15. `/helpop` - Pomoc od admina
16. `/gamma` - Night vision
17. `/gperm` - Nadawanie uprawnień
18. `/staffchat` - Chat administracji
19. `/quests` - Questy
20. `/clan` - **NOWE** - System klanów
21. `/event` - **NOWE** - Eventy globalne
22. `/upgrades` - **NOWE** - Ulepszenia wyspy
23. `/kit` - **NOWE** - Kity
24. `/kits` - **NOWE** - Kity
25. `/rank` - **NOWE** - Rangi
26. `/ranks` - **NOWE** - Rangi
27. `/store` - **NOWE** - Sklep
28. `/heal` - **NOWE** - Leczenie
29. `/level` - **NOWE** - Poziomy
30. `/nick` - **NOWE** - Customizacja
31. `/top` - **NOWE** - Teleportacja

## Pełna Lista Managerów (16)

1. DataManager - Dane graczy
2. JobManager - System prac
3. HologramManager - Hologramy
4. ChatGameManager - Gra czatowa
5. MuteManager - Wyciszanie
6. VoteManager - Głosowanie
7. CoinflipManager - Coinflip
8. HomeManager - Domy
9. EffectsManager - Efekty
10. QuestManager - Questy
11. ClanManager - **NOWY** - Klany
12. EventManager - **NOWY** - Eventy
13. IslandManager - **NOWY** - Wyspy
14. LevelManager - **NOWY** - Poziomy
15. ChatManager - **NOWY** - Chat
16. BoostManager - **NOWY** - Boosty

## Struktura Projektu

```
skyblocknssv/
├── src/main/
│   ├── java/pl/nssv/skyblock/
│   │   ├── SkyblocknNSSV.java (główna klasa)
│   │   ├── commands/ (31 komend)
│   │   ├── managers/ (16 managerów)
│   │   ├── listeners/ (13 listenerów)
│   │   ├── data/ (PlayerData)
│   │   ├── gui/ (JobGUI)
│   │   └── utils/ (ChatUtil)
│   └── resources/
│       ├── plugin.yml
│       └── config.yml
├── pom.xml
├── README.md
├── CHANGELOG.md
├── FEATURES.md
└── INSTALL.md
```

## Konfiguracja

### Nowe Sekcje

**Utils3:** (Klany, Eventy, Ulepszenia, Rangi)
```yaml
Utils3:
  ClanHelp: [...]
  EventName: [...]
  UpgradeSuccess: "..."
  ClickStore: "..."
  RanksJoinMessage: true
  JoinMessage: "..."
  Healed: "..."
```

**Utils4:** (Poziomy, Welcome, Animacje)
```yaml
Utils4:
  IslandGeneration: "..."
  IslandProgress: "..."
  IslandDone: "..."
  WelcomeMess1-7: [...]
```

**GUIS:** (Upgrades, Kits, Ranks, Level)
```yaml
GUIS:
  Upgrades: [...]
  Kits: [...]
  Ranks: [...]
  Level: [...]
```

## Status Implementacji

✅ **100% Gotowe:**
- System klanów (podstawy)
- Eventy globalne
- Wszystkie komendy (szkielety)
- Wszystkie managery (szkielety)
- Konfiguracja
- Dokumentacja

⚠️ **Szkielety (do uzupełnienia):**
- GUI kitów (szczegóły)
- GUI rang (szczegóły)
- GUI poziomów (szczegóły)
- GUI customizacji czatu (szczegóły)
- Animacje wyspy (szczegóły)
- Mining boost pads (szczegóły)
- Zapisywanie klanów do plików

## Kompilacja

```bash
# Import do IntelliJ IDEA
File → Open → wybierz folder skyblocknssv

# Kompilacja Maven
mvn clean package

# Output
target/SkyblocknNSSV-1.1.0.jar
```

## Instalacja

1. Skopiuj JAR do `plugins/`
2. Zrestartuj serwer
3. Skonfiguruj config.yml
4. Gotowe!

## Zależności

Bez zmian:
- Spigot 1.20.4
- Vault
- PlaceholderAPI
- LuckPerms
- PlayerKits2 (nowe)
- BSkyBlock (nowe)

## Co Nowego

- +12 komend
- +6 managerów
- +20 funkcji
- System klanów 
- Eventy globalne
- Ulepszenia wyspy
- System kitów
- Rangi VIP
- System poziomów
- Customizacja czatu
- Mining boost
- Welcome messages

---
**Wersja:** 1.1.0  
**Data:** 29.11.2024  
**Autor:** pvsky
