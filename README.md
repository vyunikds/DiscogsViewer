# DiscogsViewer

Приложение для просмотра музыкальных релизов через Discogs API.

Токен можно получить [здесь](https://www.discogs.com/settings/developers).

## Описание
Выпускной проект по Android курсу от Otus.

DiscogsViewer — Android-приложение, позволяющее просматривать топ релизов, осуществлять поиск, а также добавлять релизы в избранное.

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
| UI-Testing        | Kaspresso |


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

## CI/CD

Сборка и анализ производительности — на Jenkins в Docker.

```
ci/Dockerfile           — образ билд-агента: JDK 17 + Android SDK 36 + Gradle Profiler
ci/Dockerfile.jenkins   — образ Jenkins-мастера: jenkins/lts + docker CLI (подъём агент-контейнеров)
ci/profiler.scenarios   — сценарий бенчмарка для ночного джоба
Jenkinsfile             — сборка по веткам: main → release, остальные → debug
Jenkinsfile-profiler    — ночной бенчмарк сборок (gradle-profiler, cron H 2 * * *)
ci/docker-compose.yml   — запуск самого Jenkins
```

### 1. Образы

```bash
# билд-агент (используется как docker-агент в pipeline)
docker buildx build --platform linux/amd64 -f ci/Dockerfile -t discogsviewer-android-builder:17 --load .
# мастер Jenkins (нужен до compose up)
docker buildx build --platform linux/amd64 -f ci/Dockerfile.jenkins -t discogsviewer-jenkins:17 --load .
```

Образ **обязательно amd64**: aapt2 (из AGP) существует только для linux x86_64.
На Apple Silicon включите в Docker Desktop «Use Rosetta for x86_64 emulation» —
иначе сборка пойдёт через QEMU и будет кратно медленнее.

### 2. Jenkins

```bash
cd ci
docker compose up -d        # UI: http://localhost:8080
```

Настройка (один раз):
1. Wizard: разблокировать токеном из `docker logs jenkins` (строка `... initialAdminPassword`);
   логин — `admin` / этот же токен
2. Плагины: **Docker**, **Docker Pipeline**, **Pipeline**, **Credentials Binding**, **Git**,
   **Timestamper** (`Manage Jenkins → Plugins`)
3. Credential: `discogs-token` (secret text) — значение `auth.token`;
   в CI оно материализуется в gitignored-файл `core/network/network.properties`
4. `Manage Jenkins → Clouds → Docker`: агент-шаблон
   - Image: `discogsviewer-android-builder:17`, Labels: `android-builder`
   - Volumes: `/tmp/dv-ci-gradle:/root/.gradle` (общий кэш зависимостей;
     в проде — именованный Docker-том)

### 3. Джобы

- **discogsviewer-ci** — pipeline из `Jenkinsfile`:
  secrets → unit-тесты → detekt + ktlint → сборка → артефакты
  - `main`: `assembleRelease` + `publishApkToLocalMaven`;
    артефакты: release APK и maven-local репозиторий
  - остальные ветки: `assembleDebug`; артефакт: debug APK
  - в plain-джобе `BRANCH_NAME` не задан — в `environment` он дефолтится к `main`
- **discogsviewer-gradle-profiler** — pipeline из `Jenkinsfile-profiler`, cron `H 2 * * *`:
  бенчмарк по сценарию `ci/profiler.scenarios` (1 warmup + 3 замера
  `clean assembleRelease`, общий gradle-user-home, чтобы скачивание зависимостей
  не мешало измерению). В `profile-out/benchmark.csv` — длительность каждой
  итерации; история артефактов джобы — тренд производительности сборки.
  Скачок между ночными проганами — сигнал к инспекции (по задачам — `benchmark.html`).

### Особенности

- `JAVA_OPTS` в compose: `-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true` —
  только для локального E2E (checkout из смонтированного каталога).
- `-Dorg.gradle.workers.max=2` в pipeline — ограничение параллельности kapt:
  при памяти Docker Desktop ~8 ГБ без лимита gradle-демон убивался OOM.

### Локальный E2E

В `ci/docker-compose.yml` репо смонтировано в Jenkins (`/repos/DiscogsViewer`),
джобы берут pipeline и исходники из локального git — GitHub не нужен.
Для продакшена замените URL в джобе на `https://github.com/<you>/DiscogsViewer.git`
и используйте multibranch (GitHub) job.
