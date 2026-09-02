# Build agent image for Jenkins: JDK 17 + Android SDK 36 + Gradle Profiler
FROM eclipse-temurin:17-jdk-jammy

ARG ANDROID_PLATFORM=36
ARG ANDROID_BUILD_TOOLS=36.0.0
ARG ANDROID_CMDLINE_TOOLS=commandlinetools-linux-11076708_latest.zip
ARG GRADLE_PROFILER_VERSION=0.25.2

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_ROOT=/opt/android-sdk \
    GRADLE_USER_HOME=/root/.gradle \
    PATH=/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools:/opt/gradle-profiler-0.25.2/bin:$PATH

RUN apt-get update \
    && apt-get install -y --no-install-recommends git unzip wget curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Android SDK: platform 36 (compileSdk), build-tools, platform-tools
RUN mkdir -p "$ANDROID_HOME/cmdline-tools" \
    && wget -q "https://dl.google.com/android/repository/${ANDROID_CMDLINE_TOOLS}" -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools \
    && mv /tmp/cmdline-tools/cmdline-tools "$ANDROID_HOME/cmdline-tools/latest" \
    && rm -rf /tmp/cmdline-tools /tmp/cmdline-tools.zip \
    && yes | sdkmanager --licenses > /dev/null \
    && sdkmanager "platforms;android-${ANDROID_PLATFORM}" "build-tools;${ANDROID_BUILD_TOOLS}" "platform-tools"

# Gradle Profiler CLI (nightly build performance benchmarks)
RUN wget -q "https://github.com/gradle/gradle-profiler/releases/download/v${GRADLE_PROFILER_VERSION}/gradle-profiler-${GRADLE_PROFILER_VERSION}.zip" -O /tmp/gradle-profiler.zip \
    && unzip -q /tmp/gradle-profiler.zip -d /opt \
    && rm /tmp/gradle-profiler.zip

# Pre-warm the Gradle distribution cache so agents don't re-download it per build
WORKDIR /workspace
COPY gradlew gradlew.bat ./
COPY gradle/wrapper gradle/wrapper
RUN chmod +x gradlew && ./gradlew --version
