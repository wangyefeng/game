#!/bin/bash

classpath=`find hotfix -type f -name "*.class" | tr '\n' ' '`

if [ -z "$classpath" ]; then
    echo "没有找到热更新文件"
    exit 1
fi

echo "正在热更新 $classpath"

curl -Ss -XPOST http://localhost:8563/api -d "{\"action\":\"exec\",\"command\":\"retransform $classpath\"}"