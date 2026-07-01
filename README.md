# DiscogsViewer

Приложение для просмотра музыкальных релизов через Discogs API.
токен можно получить [здесь](https://www.discogs.com/settings/developers).

## Описание
Выпускной проект по Android курсу от Otus.

DiscogsViewer — Android-приложение, позволяющее просматривать топ релизов, осуществлять поиск, а также добавлять релизы в избранное. Приложение построено на модульной архитектуре с четким разделением ответственности между слоями.

Основные экраны:

- **Top Releases** — список топовых релизов с поддержкой pull-to-refresh и пагинации
- **Search** — поиск релизов с историей поисковых запросов
- **Favorites** — избранное с фильтрацией по жанрам и сортировкой
- **Details** — детальный просмотр выбранного релиза
- **Settings** — настройки приложения (тема)

## Скриншоты
<img width="270" height="585" alt="top-releases" src="https://github.com/user-attachments/assets/92f0cc0c-eb24-436a-b991-f016bbf74b72" />

<img width="270" height="585" alt="search-result" src="https://github.com/user-attachments/assets/4fa4cac4-34e1-4f06-b507-75306e18b124" />

<img width="270" height="585" alt="favorites" src="https://github.com/user-attachments/assets/ba96569c-0095-4c96-bec6-ade321a6a205" />

<img width="270" height="585" alt="detailed" src="https://github.com/user-attachments/assets/de0f4ee7-2c64-421a-803c-b4c4a0ebdfcc" />

<img width="270" height="585" alt="settings" src="https://github.com/user-attachments/assets/b835c79a-1947-4691-8e8c-7259dc8e5283" />


## Использованный стек

| Область           | Технология                           |
|------------------ |--------------------------------------|
| Language          | Kotlin                               |
| UI                | Jetpack Compose + Material 3         |
| Architecture      | Модульная (core / data / feature)    |
| DI                | Hilt                          |
| Network           | Ktor + kotlinx-serialization  |
| Database          | Room             |
| Storage           | DataStore Preferences                |
| Image loading     | Coil                     |
| Navigation        | Jetpack Navigation Compose   |
| Concurrency       | Kotlin Coroutines                    |
| Testing           | JUnit 4, Mockk, Turbine, Coroutines Test |

## Схема модулей

```
DiscogsViewer
┌──────────────────────────────────────────────────────┐
│  :app                                                │
│  └── MainActivity, MainNavigation, Theme, ScreenRoute│
└────┬──────────┬──────────┬──────────┬──────────┬─────┘
     │          │          │          │          │
     ▼          ▼          ▼          ▼          ▼
┌─────────┐┌────────┐┌─────────┐┌──────────┐┌──────────┐
│:feature:││:feature││:feature:││:feature: ││:feature: │
│releases ││search  ││favorites││details   ││settings  │
└────┬────┘└───┬────┘└────┬────┘└────┬─────┘└────┬─────┘
     │         │          │          │           │
     └─────┬───┴──────┬───┴──────────┴───────────┘
           │          │
     ┌─────┴────┐ ┌───┴─────────────────────────┐
     │ :data:   │ │ :core:basepresentation      │
     │ releases │ │ (ScreenRouter,              │
     │ search   │ │  ReleaseCardState,          │
     │ favorite │ │  SharedTheme)               │
     │ settings │ └─────────────────────────────┘
     └────┬─────┘
          │
     ┌────┴──────────────────────────┐
     │  :core:                       │
     │  network  (Ktor, DTOs)        │
     │  database (Room, DAOs, DBOs)  │
     │  di       (Hilt modules)      │
     └───────────────────────────────┘

Dependency direction:
  app → feature/* → data/* → core/*
                     ↳ core:basepresentation
```

### Описание слоев

- **core/** — общедоступная инфраструктура: сетевой клиент, база данных, DI-конфигурация, общие UI-модели
- **data/** — репозитории с маппингом DTO/DBO → domain-модели; данные не утекают в feature-слой
- **feature/** — UI-экраны, ViewModels, use cases и навигационные entry-points
- **app/** — точка входа: MainActivity, NavHost, навигационные маршруты, тема

## Запуск

```bash
./gradlew assembleDebug
```
