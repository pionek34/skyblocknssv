# SkyblocknNSSV - Plugin Minecraft 1.20+

Kompletny plugin Skyblock Utils dla serwera Minecraft 1.20+ stworzony na podstawie skryptów Skript.

## Funkcje

### 1. System Prac (Jobs)
- 9 różnych prac: Górnik, Budowniczy, Rolnik, Drwal, Rzemieślnik, Rybak, Łowca, Zaklinacz, Alchemik
- System poziomów i doświadczenia
- Automatyczne nagrody za milestone (poziomy 20, 40, 60, 80, 100)
- Mnożnik zarobków rośnie wraz z poziomem
- GUI z podglądem postępów i nagród
- Możliwość zmiany pracy (cooldown 10 sekund)

### 2. System Czatu
- Formatowanie czatu z poziomami, rangami, klanami
- Wsparcie dla gradientów kolorów
- Emoji w czacie (:tableflip:, :shrug:, :lenny:, :yes:, :no:, <3)
- Integracja z LuckPerms i PlaceholderAPI

### 3. System Wyciszania (Mute)
- Komendy /mute i /unmute
- Wsparcie dla różnych jednostek czasu (s, m, h, d)
- Automatyczne odliczanie czasu wyciszenia

### 4. Hologramy
- Item Display hologram z animacjami
- Automatyczna naprawa pozycji
- Pokazywanie/ukrywanie na podstawie dystansu
- Komendy /xqeffects holo i /xqeffects killholo

### 5. ChatGame
- Automatyczna gra matematyczna co 10 minut
- Nagrody za poprawne odpowiedzi (pieniądze + shards)
- 3 typy pytań: dodawanie, odejmowanie, mnożenie

### 6. Inne Funkcjonalności
- Custom MOTD
- Lista komend dla graczy
- Emoji command (/emoji)
- Teleportacja do spawn przy pierwszym dołączeniu
- System immunity od fall damage
- Obsługa śmierci z jobami

## Instalacja

### Wymagania
- Minecraft Server 1.20+
- Java 17+
- Vault (opcjonalnie, dla ekonomii)
- PlaceholderAPI (opcjonalnie, dla placeholderów)
- LuckPerms (opcjonalnie, dla rang)

### Kompilacja
1. Otwórz projekt w IntelliJ IDEA
2. Poczekaj aż Maven pobierze zależności
3. Uruchom: `Maven -> Lifecycle -> package`
4. Plugin znajdziesz w folderze `target/SkyblocknNSSV-1.0.0.jar`

### Instalacja na serwerze
1. Skopiuj `SkyblocknNSSV-1.0.0.jar` do folderu `plugins/`
2. Zainstaluj wymagane pluginy (Vault, PlaceholderAPI, LuckPerms)
3. Uruchom serwer
4. Skonfiguruj `config.yml` w folderze `plugins/SkyblocknNSSV/`

## Konfiguracja

### Config.yml
Wszystkie wiadomości i ustawienia są w pliku `config.yml`. Domyślnie wszystko jest po polsku.

### Ważne ścieżki
- `Global.ChatFormat` - format czatu
- `Global.Motd1` i `Global.Motd2` - MOTD serwera
- `Utils.JobName.*` - polskie nazwy prac
- `GUIS.Jobs.*` - konfiguracja GUI prac

### Ustawianie spawn
Spawn nie jest ustawiony domyślnie. Musisz go ustawić ręcznie w pliku config.yml:

```yaml
Spawn:
  world: "world"
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0
```

## Komendy

| Komenda | Aliasy | Opis | Permisja |
|---------|--------|------|----------|
| /job | jobs, miner, builder, farmer, etc. | Otwiera GUI prac | - |
| /emoji | emotes, emote | Pokazuje listę emoji | - |
| /mute <gracz> <czas> | - | Wycisza gracza | gens.mute |
| /unmute <gracz> | - | Odcisza gracza | gens.mute |
| /xqeffects holo | - | Tworzy hologram | * |
| /xqeffects killholo | - | Usuwa hologram | * |
| /chatgame | - | Startuje grę na chacie | * |

## Uprawnienia

- `*` - Pełny dostęp do wszystkich funkcji
- `gens.mute` - Dostęp do wyciszania
- `ChatFormat.Colored` - Kolorowanie wiadomości
- `ChatFormat.Emojis` - Używanie emoji

## Dane graczy

Dane graczy są zapisywane w folderze `plugins/SkyblocknNSSV/playerdata/` w formacie YAML.

Każdy gracz ma:
- Poziom
- Aktualna praca
- Poziomy i doświadczenie we wszystkich pracach
- Status wyciszenia
- Shards (kryształy)
- Customizacja czatu

## Integracja z innymi pluginami

### Vault
Plugin automatycznie wykrywa Vault i używa go do ekonomii (nagrody za prace, chatgame).

### PlaceholderAPI
Wspierane placeholdery:
- `%luckperms_prefix%` - prefix gracza
- `%Level_bskyblock_island_level%` - poziom wyspy

### LuckPerms
Rangi są automatycznie wyświetlane w czacie poprzez PlaceholderAPI.

## Eventy globalne

W configu możesz ustawić `CurrentEvent`:
- `DoubleXP` - podwojone doświadczenie w pracach
- `JobBoost` - podwojony zarobek z prac

## Różnice od skryptów Skript

1. **Lepsza wydajność** - Java jest znacznie szybsza niż Skript
2. **Mniej błędów** - Kompilator wykrywa błędy przed uruchomieniem
3. **Łatwiejsza konfiguracja** - Wszystko w config.yml
4. **Persistent Data** - Dane są bezpiecznie zapisywane
5. **Async Chat** - Chat nie laguje serwera

## Wsparcie

W razie problemów:
1. Sprawdź logi w `logs/latest.log`
2. Upewnij się że masz najnowszą wersję Java 17+
3. Sprawdź czy wszystkie wymagane pluginy są zainstalowane

## Autor

Plugin stworzony przez pvsky dla serwera NSSV.PL
