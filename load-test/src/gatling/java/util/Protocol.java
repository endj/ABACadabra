package util;

import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.http.HttpDsl.http;

public final class Protocol {
    public static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("http://localhost:8080")
            .contentTypeHeader("application/json");

}
