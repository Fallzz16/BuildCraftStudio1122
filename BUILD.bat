@echo off
setlocal
cd /d "%~dp0"
echo BUILDCRAFT STUDIO M1 - FORGE 1.12.2
where javac >nul 2>&1
if errorlevel 1 (
  echo ERRO: JDK 8 nao encontrado. Instale o JDK 8 e configure JAVA_HOME.
  pause
  exit /b 1
)
java -version
javac -version
javac -version 2>&1 | findstr /C:"1.8." >nul
if errorlevel 1 (
  echo ERRO: esta compilacao requer JDK 8 ^(javac 1.8.x^), nao Java 11/17/21.
  pause
  exit /b 1
)
echo.
echo Se o Gradle 4.9 estiver no PATH, este script usa o Gradle instalado.
where gradle >nul 2>&1
if errorlevel 1 (
  echo ERRO: gradle nao encontrado. Use o GitHub Actions incluido ou instale Gradle 4.9.
  pause
  exit /b 1
)
call gradle --no-daemon --console=plain clean build > BUILD_LOG.txt 2>&1
if errorlevel 1 (
  type BUILD_LOG.txt
  echo.
  echo BUILD FAILED: envie BUILD_LOG.txt
  pause
  exit /b 1
)
echo BUILD SUCCESSFUL
if not exist "build\libs\buildcraft-studio-forge1122-0.1.0.jar" (
  echo ERRO: JAR esperado nao encontrado!
  pause
  exit /b 1
)
echo Resultado: build\libs\buildcraft-studio-forge1122-0.1.0.jar
pause
