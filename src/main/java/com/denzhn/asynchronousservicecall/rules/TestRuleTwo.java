package com.denzhn.asynchronousservicecall.rules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class TestRuleTwo implements Rule {

    private final WebClient webClient;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    public TestRuleTwo() {
        webClient = WebClient.builder()
                .baseUrl("https://imsea.herokuapp.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public String initialize(String param) {
        try {
            requestHeadersSpec = webClient.get().uri("/api/1?q=" + param);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "SUCCESS";
    }

    @Override
    public String validate(String param) {
        if (!StringUtils.hasLength(param)) {
            return "Image name is empty or null!";
        }
        return "SUCCESS";
    }

    @Override
    public String execute() {
        long startTime = System.currentTimeMillis();
        AtomicReference<String> result = new AtomicReference<>();
        result.set(requestHeadersSpec.retrieve().bodyToMono(String.class)
                .doOnError(throwable -> result.set(throwable.getMessage()))
                .blockOptional().orElse(null));
        log.info("RuleTwo execution finished : " + (System.currentTimeMillis() - startTime));
        return result.get();
    }
}
