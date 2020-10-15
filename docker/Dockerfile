FROM registry.gs.youyuwo.com/base/docker-springboot-pinpoint:latest


COPY yyconfig-main/target/yyconfig-main.jar /app.jar

ENV PATH="${JAVA_HOME}/bin:${PATH}"

EXPOSE 8080

CMD ["java","-jar","app.jar"]



