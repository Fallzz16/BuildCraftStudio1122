@echo off
setlocal
set "ROOT=%~dp0"
set "OUT=%TEMP%\buildcraft-math-tests"
if not exist "%OUT%" mkdir "%OUT%"
cd /d "%ROOT%"
javac -encoding UTF-8 -source 8 -target 8 -d "%OUT%" "src\main\java\dev\buildcraftstudio\core\EditorMath.java" "tests\EditorMathTest.java" "tests\stubs\net\minecraft\util\math\BlockPos.java" "tests\stubs\net\minecraft\block\state\IBlockState.java" "src\main\java\dev\buildcraftstudio\core\BuildDocument.java" "tests\BuildDocumentTest.java" "tests\SafeSaveStreamTest.java"
if errorlevel 1 (echo TEST FAILED & pause & exit /b 1)
java -cp "%OUT%" EditorMathTest
java -cp "%OUT%" BuildDocumentTest
java -cp "%OUT%" SafeSaveStreamTest
pause
