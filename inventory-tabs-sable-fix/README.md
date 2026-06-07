# Inventory Tabs – Sable Physics Fix

Патч-мод для **NeoForge 1.21.1**, исправляющий отсутствие вкладок Inventory Tabs
на контейнерах, которые находятся на летящей конструкции Create Aeronautics (физика Sable).

## Проблема

Inventory Tabs ищет `BlockEntity` по мировым координатам через `level.getBlockEntity(pos)`.
Когда блок является частью конструкции Sable, он живёт не в обычном уровне, а в
`ContraptionWorld` с локальными координатами — поэтому стандартный вызов возвращает `null`
и вкладка не появляется.

## Решение

Mixin (`MixinTabManager`) перехватывает каждый вызов `Level#getBlockEntity` внутри метода
`TabManager#getTabForBlockEntity` и заменяет его на
`ContraptionBlockEntityHelper#getBlockEntity`, который:
1. Сначала выполняет обычный поиск.
2. Если результат `null` — запрашивает `ContraptionWorld` через рефлексию (без жёсткой
   зависимости на Sable/Aeronautics).

Если Sable не установлен, рефлексивный слой пропускается целиком — нулевой оверхед.

## Сборка

```bash
./gradlew build
```

Готовый `.jar` появится в `build/libs/`.

### Зависимости для компиляции

Если у тебя есть jar Inventory Tabs, положи его в папку `libs/` и раскомментируй строку в
`build.gradle`:
```groovy
compileOnly files('libs/inventorytabs-1.21.1-x.x.x.jar')
```

## Возможные правки

| Ситуация | Что менять |
|---|---|
| Inventory Tabs сменил пакет/имя класса `TabManager` | `targets` в `@Mixin` |
| Метод определения блока переименован | `method` в `@Redirect` |
| Sable сменил имя класса `ContraptionWorld` | строка `Class.forName(...)` в `SableCompat` |
| Sable сменил сигнатуру `getContraptionWorldAt` | `getMethod(...)` в `SableCompat` |

## Совместимость

- Minecraft **1.21.1**
- NeoForge **21.1.x**
- Inventory Tabs **любая версия для 1.21.1**
- Create Aeronautics / Sable — **опциональная зависимость** (мод работает и без неё)
