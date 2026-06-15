# Sử dụng môi trường Java 17 tối ưu, siêu nhẹ
FROM eclipse-temurin:17-jdk-alpine

# Copy file code đã nén (.jar) từ máy vào bên trong Container ảo
COPY target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Lệnh khởi động Spring Boot khi bật Container
ENTRYPOINT ["java", "-jar", "/app.jar"]