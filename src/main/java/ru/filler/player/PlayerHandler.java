package ru.filler.player;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlayerHandler {

    private final PlayerService playerService;

    Mono<ServerResponse> process(ServerRequest request) {
        Mono<PlayerResponse> result = request.bodyToMono(GameData.class)
                .map(playerService::process);
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, PlayerResponse.class);
    }
}
