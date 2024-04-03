package edu.stanford.slac.core_build_system.service.engine;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EngineFactory {
    private final ApplicationContext context;

    public EngineBuilder getEngineBuilder(String engineType) {
        return context.getBean(engineType.toLowerCase(), EngineBuilder.class);
    }
}