@echo off
echo Setting up AutoLogin Mod build...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java not found. Install JDK 21 from https://adoptium.net/
    pause
    exit /b 1
)

where gradle >nul 2>nul
if %errorlevel% neq 0 (
    echo Gradle not found, downloading wrapper...
    curl -L "https://services.gradle.org/distributions/gradle-8.8-bin.zip" -o gradle.zip
    tar -xf gradle.zip
    move gradle-8.8 gradle-dist
    set GRADLE_CMD=gradle-dist\bin\gradle.bat
) else (
    set GRADLE_CMD=gradle
)

echo Generating Gradle wrapper...
%GRADLE_CMD% wrapper

echo Building mod...
gradlew.bat build

if %errorlevel% neq 0 (
    echo BUILD FAILED. Check errors above.
    pause
    exit /b 1
)

echo.
echo BUILD SUCCESS!
echo Your mod jar is in: build\libs\
echo Copy it to your .minecraft\mods\ folder.
pause
