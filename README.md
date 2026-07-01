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
<img width="293" height="291" alt="Снимок экрана 2026-05-28 в 15 41 05" src="https://github.com/user-attachments/assets/d6541a0f-b886-44a8-884b-6c24a6161889" />

![Screen 1](screenshots/top-releases.png)
![Screen 2](screenshots/search-results.png)
![Screen 3](screenshots/favorites.png)
![Screen 4](screenshots/detailed-release.png)
![Screen 5](screenshots/settings.png)

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
| Build             | AGP 8.13.2, Gradle Version Catalog   |
| Target SDK        | 35 (Vanilla Ice Cream) / minSdk 24   |

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
