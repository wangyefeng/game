#!/bin/bash
# hoxfix 路径
hoxfixPath=/app/hotfix
# 输入命令，执行命令
find "/app/hotfix" -type f -name "*.class" | while read file; do
    echo "热更新文件: $file"
    curl -Ss -XPOST http://localhost:8563/api -d "{\"action\":\"exec\",\"command\":\"retransform $file\"}"
done