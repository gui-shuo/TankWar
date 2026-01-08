#!/bin/bash

# 坦克大战 - 编译运行脚本 (Linux/Mac)

echo "================================"
echo "  坦克大战 - 创新版"
echo "================================"
echo ""

# 创建输出目录
mkdir -p out

echo "[1/2] 编译中..."

# 编译所有Java文件
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt

if [ $? -ne 0 ]; then
    echo "编译失败！请检查错误信息。"
    rm -f sources.txt
    exit 1
fi

rm -f sources.txt

echo "[2/2] 启动游戏..."
echo ""

# 运行游戏
java -cp out com.tankwar.Main
