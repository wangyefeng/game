FROM openjdk:25-jdk

RUN microdnf install -y nc
# 将本地的 hotfix.sh 文件复制到容器中的工作目录
COPY hotfix.sh /app/hotfix.sh
# 将本地的 JAR 文件复制到容器中的工作目录
COPY target/gate-1.0.0-SNAPSHOT.jar /app/gate.jar

EXPOSE 12000 12001

# 设置工作目录
WORKDIR /app

# 容器启动时执行的命令
CMD ["java", "-jar", "gate.jar"]
