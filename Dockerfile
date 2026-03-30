FROM eclipse-temurin:17-jdk-jammy AS builder

RUN apt-get update \
    && apt-get install -y curl gnupg2 ca-certificates unzip \
    && echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" > /etc/apt/sources.list.d/sbt.list \
    && echo "deb https://repo.scala-sbt.org/scalasbt/debian /" > /etc/apt/sources.list.d/sbt_old.list \
    && curl -fsSL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | gpg --dearmor > /etc/apt/trusted.gpg.d/sbt.gpg \
    && apt-get update \
    && apt-get install -y sbt \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

RUN sbt clean dist

FROM eclipse-temurin:17-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install -y unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt
COPY --from=builder /app/target/universal/*.zip /tmp/app.zip
RUN unzip /tmp/app.zip -d /opt \
    && rm /tmp/app.zip \
    && mv /opt/play-backend-* /opt/play-backend

EXPOSE 9000
CMD ["/opt/play-backend/bin/play-backend", "-Dplay.http.address=0.0.0.0", "-Dhttp.port=9000"]
