@echo off
chcp 65001 >nul
echo ================================
echo   坦克大战 - 创新版
echo ================================
echo.

REM 创建输出目录
if not exist out mkdir out

echo [1/2] 编译中...

REM 编译所有Java文件
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d out @sources.txt

if %ERRORLEVEL% neq 0 (
    echo 编译失败！请检查错误信息。
    del sources.txt
    pause
    exit /b 1
)

del sources.txt

echo [2/2] 启动游戏...
echo.

REM 运行游戏
java -cp out com.tankwar.Main

pause
