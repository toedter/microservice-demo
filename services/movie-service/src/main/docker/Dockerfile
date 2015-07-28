FROM java
RUN pwd
COPY ./services/user-service/build/libs/user-service-0.0.1.jar /app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]