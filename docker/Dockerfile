FROM registry.gs.youyuwo.com/base/docker-java-base:jdk_8


COPY yyconfig-main/target/yyconfig-main-1.0.0-SNAPSHOT.jar /app.jar

ENV PATH="${JAVA_HOME}/bin:${PATH}"

EXPOSE 8080

CMD ["java","-jar","app.jar"]



