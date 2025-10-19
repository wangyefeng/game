#!/bin/bash

# 检查hotfix目录是否存在
if [ ! -d "hotfix" ]; then
    echo "错误: hotfix目录不存在"
    exit 1
fi

echo "正在查找class文件..."
classpath=""

# 只查找hotfix根目录下的class文件
for file in hotfix/*.class; do
    if [ -f "$file" ]; then
        classpath="$classpath $file"
        echo "找到: $file"
    fi
done

if [ -z "$classpath" ]; then
    echo "没有找到热更新文件"
    echo "请在hotfix目录中直接放置.class文件，或安装find命令"
    exit 1
fi

echo "正在热更新以下文件:"
echo "$classpath" | tr ' ' '\n'
echo

echo "执行热更新命令..."
curl -Ss -XPOST http://127.0.0.1:8563/api -d "{\"action\":\"exec\",\"command\":\"retransform $classpath\"}"