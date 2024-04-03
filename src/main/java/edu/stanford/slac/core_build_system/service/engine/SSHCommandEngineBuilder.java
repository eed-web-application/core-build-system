package edu.stanford.slac.core_build_system.service.engine;

import edu.stanford.slac.core_build_system.exception.CommandTemplateNotFound;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.CommandTemplateInstance;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ExecutionPipeline;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SSHCommandEngineBuilder implements EngineBuilder {
    private final CommandTemplateRepository commandTemplateRepository;
    private List<Component> components = new ArrayList<>();

    @Override
    public EngineBuilder addComponent(Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public EngineBuilder addBuilderSpec(String key, String value) {
        return null;
    }

    @Override
    public String build() {
        StringBuilder sshCommands = new StringBuilder();
        for (Component component : components) {
            for (CommandTemplateInstance instance : component.getCommandTemplatesInstances()) {
                CommandTemplate template = commandTemplateRepository
                        .findById(instance.getId())
                        .orElseThrow(
                                ()-> CommandTemplateNotFound.byId()
                                        .errorCode(-1)
                                        .id(instance.getId())
                                        .build()
                        );
                for (ExecutionPipeline pipeline : template.getCommandExecutionsLayers()) {
                    if (pipeline.getEngine().equals("ssh")) {
                        pipeline.getExecutionCommands().forEach(command ->
                                sshCommands.append(command).append("\n"));
                    }
                }
            }
        }
        return sshCommands.toString();
    }
}
