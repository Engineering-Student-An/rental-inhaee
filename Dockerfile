FROM gradle:8.5-jdk21-alpine as builder
WORKDIR /build

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN gradle build -x test --parallel

# APP
FROM openjdk:21-slim
WORKDIR /app

# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/rental-inhaee-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

# 환경 변수를 위한 ARG 선언
ARG SPRING_MAIL_USERNAME
ARG SPRING_MAIL_PASSWORD

# 환경 변수를 환경 변수로 설정
ENV SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}

# root 대신 nobody 권한으로 실행
USER nobody
ENTRYPOINT [ \
   "java", \
   "-jar", \
   "-Djava.security.egd=file:/dev/./urandom", \
   "-Dsun.net.inetaddr.ttl=0", \
   "-Dspring.mail.username=${SPRING_MAIL_USERNAME}", \
   "-Dspring.mail.password=${SPRING_MAIL_PASSWORD}", \
   "rental-inhaee-0.0.1-SNAPSHOT.jar" \
]
