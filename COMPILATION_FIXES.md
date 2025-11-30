# NAPRAWIONE BŁĘDY KOMPILACJI

## 1. ✅ Vault Dependency
**Błąd:** net.milkbowl.vault:VaultAPI:jar:1.7 not found
**Naprawa:** Zmieniono na com.github.MilkBowl:VaultAPI:1.7.1 (JitPack)

## 2. ✅ Duplicate Class: DataManager  
**Błąd:** duplicate class pl.nssv.skyblock.data.DataManager:14
**Naprawa:** Usunięto duplikat z managers/DataManager.java

## 3. ✅ Duplicate Method: EventManager.startEvent()
**Błąd:** method startEvent(String) is already defined
**Naprawa:** Zmieniono drugą metodę na activateEvent(String)

## 4. ✅ Paper API w JobGUI
**Błąd:** package com.destroystokyo.paper.profile does not exist
**Naprawa:** Zastąpiono Paper API refleksją Mojang (GameProfile)

## 5. ✅ Paper API w FishingListener
**Błąd:** method setWaitTime() cannot be applied to FishHook
**Naprawa:** Usunięto wywołania setWaitTime, setMinWaitTime, setMaxWaitTime

## POZOSTAŁE BŁĘDY "cannot find symbol"

Te błędy to prawdopodobnie:
- Brakujące importy
- Użycie nieistniejących metod/pól
- Błędne nazwy klas

Większość można naprawić poprzez:
1. Dodanie odpowiednich importów
2. Upewnienie się że wszystkie klasy data/managers istnieją
3. Sprawdzenie czy używane metody istnieją w API

## JAK SKOMPILOWAĆ PO NAPRAWACH

```bash
cd skyblocknssv
mvn clean package
```

Jeśli nadal są błędy, uruchom:
```bash
mvn clean package > build.log 2>&1
cat build.log
```

## ZNANE PROBLEMY

1. **Jeśli używasz Java 8:**
   - Zmień w pom.xml: <source>8</source> i <target>8</target>

2. **Jeśli brakuje zależności:**
   - mvn clean install -U

3. **Jeśli są błędy w kodzie:**
   - Sprawdź build.log i prześlij konkretne błędy
