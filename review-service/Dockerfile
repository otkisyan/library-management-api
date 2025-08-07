FROM bellsoft/liberica-openjdk-alpine:17
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8083