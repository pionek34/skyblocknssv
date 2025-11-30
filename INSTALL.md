# Instrukcja Importu do IntelliJ IDEA

## Krok 1: Otwórz IntelliJ IDEA
1. Uruchom IntelliJ IDEA
2. Wybierz `File` -> `Open...`
3. Wskaż folder `skyblocknssv` (ten z plikiem `pom.xml`)
4. Kliknij `OK`

## Krok 2: Poczekaj na Maven
1. IntelliJ automatycznie wykryje projekt Maven
2. W prawym dolnym rogu zobaczysz postęp pobierania zależności
3. Poczekaj aż Maven pobierze wszystkie biblioteki (może to potrwać 1-3 minuty)

## Krok 3: Skonfiguruj JDK
1. Jeśli nie masz skonfigurowanego JDK 17:
   - `File` -> `Project Structure` -> `Project`
   - `SDK:` -> `Add SDK` -> `Download JDK...`
   - Wybierz wersję 17 (np. Temurin 17)
   - Kliknij `Download`

## Krok 4: Skompiluj Plugin
1. Po prawej stronie otwórz zakładkę `Maven`
2. Rozwiń `SkyblocknNSSV` -> `Lifecycle`
3. Kliknij dwukrotnie na `clean`
4. Następnie kliknij dwukrotnie na `package`
5. Poczekaj na kompilację

## Krok 5: Znajdź Plugin
Po udanej kompilacji plik JAR znajdziesz w:
```
skyblocknssv/target/SkyblocknNSSV-1.0.0.jar
```

## Krok 6: Zainstaluj na Serwerze
1. Skopiuj `SkyblocknNSSV-1.0.0.jar` do folderu `plugins/` na serwerze
2. Uruchom serwer
3. Plugin automatycznie utworzy folder `plugins/SkyblocknNSSV/` z `config.yml`

## Rozwiązywanie Problemów

### Problem: "Cannot resolve symbol 'Bukkit'"
**Rozwiązanie:** Maven nie pobrał zależności. Kliknij prawym na `pom.xml` -> `Maven` -> `Reload Project`

### Problem: "Target level '17' is incompatible"
**Rozwiązanie:** Upewnij się że masz zainstalowane JDK 17. Zobacz Krok 3.

### Problem: Plugin nie uruchamia się na serwerze
**Rozwiązanie:** 
1. Sprawdź wersję serwera - musi być 1.20+
2. Sprawdź czy masz Java 17+ na serwerze
3. Sprawdź logi w `logs/latest.log`

### Problem: Brak Vault/PlaceholderAPI
**Rozwiązanie:** Te pluginy są opcjonalne. Plugin będzie działał bez nich, ale:
- Bez Vault: nie będzie nagród pieniężnych
- Bez PlaceholderAPI: nie będzie rang i island level w czacie

## Testowanie na Lokalnym Serwerze

1. Pobierz Paper/Spigot 1.20+
2. Uruchom serwer jeden raz aby wygenerował pliki
3. Skopiuj plugin do `plugins/`
4. (Opcjonalnie) Zainstaluj Vault, PlaceholderAPI, LuckPerms
5. Uruchom serwer
6. Sprawdź `plugins/SkyblocknNSSV/config.yml`
7. Ustaw spawn i inne konfiguracje
8. Reload serwera lub użyj `/reload confirm`

## Konfiguracja Po Instalacji

### 1. Ustaw Spawn
Edytuj `config.yml`:
```yaml
Spawn:
  world: "world"
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0
```

### 2. Dostosuj Wiadomości
Wszystkie wiadomości są w `config.yml` w sekcjach:
- `Global` - ogólne
- `Utils` - komendy i funkcje
- `GUIS.Jobs` - GUI prac

### 3. Ustaw Ekonomię (Vault)
Plugin używa Vault do ekonomii. Zainstaluj:
1. Vault
2. Plugin ekonomiczny (np. EssentialsX)

### 4. Ustaw Rangi (LuckPerms)
Plugin automatycznie pobiera rangi z LuckPerms przez PlaceholderAPI.

## Struktura Komend

Po instalacji dostępne komendy:
- `/job` - menu prac
- `/emoji` - lista emoji
- `/mute <gracz> <czas>` - wycisz (wymaga gens.mute)
- `/unmute <gracz>` - odcisz (wymaga gens.mute)
- `/xqeffects holo` - stwórz hologram (wymaga *)
- `/xqeffects killholo` - usuń hologram (wymaga *)
- `/chatgame` - wystartuj grę (wymaga *)

## Uprawnienia

Domyślnie gracze mają dostęp do:
- `/job` i aliasów
- `/emoji`

Admini potrzebują:
- `gens.mute` - do mute/unmute
- `*` - do xqeffects i chatgame

## Kontakt

W razie problemów:
1. Sprawdź logi w `logs/latest.log`
2. Sprawdź console podczas startu serwera
3. Upewnij się że wszystkie zależności są zainstalowane

---

**Powodzenia z uruchomieniem pluginu na serwerze NSSV.PL!** 🎮
