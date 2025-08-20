FROM openjdk:24-jdk-slim

# 安装curl
# RUN apt-get update && apt-get install -y curl

# 将arthas-bin目录复制到容器的/app/arthas-bin目录下
COPY arthas-bin/ /app/arthas-bin/

# 将本地的 JAR 文件复制到容器中的工作目录
COPY logic/target/logic-1.0.0-SNAPSHOT.jar /app/logic.jar

EXPOSE 11000 11001 11002

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-javaagent:arthas-bin/arthas-agent.jar", "-jar", "logic.jar"]
