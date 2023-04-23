package com.ecom.winners.readers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Builder
public class RestApiItemReaderBuilder<T> implements ItemReader<List<T>> {
    private String url;
    private Map<String, String> headers;
    private WebClient webClient;
    private Class<T> entity;
    private String name;
    private int page = 1;
    private int size = 10;
    private ObjectMapper mapper;
    private Predicate<Integer> endRead;

    @Override
    public List<T> read() {
        if (endRead.test(page)) {
            return null;
        }
        Object[] response = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object[].class).cache().block();
        page += 1;
        List<T> users = new ArrayList<>();
        assert response != null;
        for (Object user : response)
            users.add(mapper.convertValue(user, entity));
        return users;
    }
}
