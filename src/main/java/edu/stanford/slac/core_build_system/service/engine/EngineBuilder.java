package edu.stanford.slac.core_build_system.service.engine;

import edu.stanford.slac.core_build_system.model.Component;

public interface EngineBuilder {
    EngineBuilder addComponent(Component component);
    EngineBuilder addBuilderSpec(String key,  String value);
    String build();
}
