# ---- Build stage ----
FROM gradle:8.8-jdk21-alpine AS build
#AS build 는 “이 단계에 이름을 붙이자”는 뜻 (뒤에서 복사할 때 씀).
WORKDIR /app
#컨테이너 안에서 작업할 폴더를 /app으로 지정.
#현재 로컬(너의 프로젝트) 전체 폴더를 컨테이너 /app 안으로 복사.
COPY . .
# 테스트 아직 없으니까 -x test
RUN gradle clean bootJar -x test
#프로젝트를 빌드해서 .jar 생성

# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
#실행용 JRE(런타임) 이미지를 사용.
WORKDIR /app
EXPOSE 8080
#위에서 빌드한 jar 파일(build/libs/*.jar)을 이 실행용 이미지로 복사.
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]

#Dockerfile은 한마디로 “내 애플리케이션을 어떻게 실행 가능한 이미지로 포장할지”
 #정의한 설계도입니다.