FROM java:8-alpine

ADD target/jwe-auth.jar /jwe-auth/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/jwe-auth/app.jar"]
