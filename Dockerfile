FROM openjdk:8
ADD target/NoteApp.jar .
ADD docker/wait .
RUN echo "Europe/Warsaw" > /etc/timezone
RUN chmod +x /wait
CMD /wait && java -jar NoteApp.jar --spring.profiles.active=container
EXPOSE 8084
LABEL profile="container"