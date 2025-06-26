FROM rust:1 AS build-env
WORKDIR /app
COPY ./spa-service/src /app/src
COPY ./spa-service/Cargo.toml /app
RUN cargo build --release
FROM gcr.io/distroless/cc-debian12
EXPOSE 80
COPY --from=build-env /app/target/release/spa-service /
COPY ./spa-service/static /static
CMD ["./spa-service"]