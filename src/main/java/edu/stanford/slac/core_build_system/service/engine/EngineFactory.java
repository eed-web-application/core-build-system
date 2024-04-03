package edu.stanford.slac.core_build_system.service.engine;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class EngineFactory {
    private final ApplicationContext context;

    public EngineBuilder getEngineBuilder(String engineType) {
        return context.getBean(engineType.toLowerCase(), EngineBuilder.class);
    }

    public Set<String> getEngineNames() {
        // Retrieves a map of beans that implement the Engine interface
        // The map's keys are bean names, and the values are the bean instances
        Map<String, EngineBuilder> engines = context.getBeansOfType(EngineBuilder.class);

        // Print the names of all engines
        return engines.keySet();
    }
}