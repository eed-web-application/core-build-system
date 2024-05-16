package edu.stanford.slac.core_build_system.service.engine;

import edu.stanford.slac.core_build_system.exception.CommandTemplateNotFound;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.CommandTemplateInstance;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ExecutionPipeline;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@org.springframework.stereotype.Component("docker")
@Scope("prototype")
@RequiredArgsConstructor
public class DockerEngineBuilder implements EngineBuilder {
    public static final String SPEC_OS_TYPE = "osType";
    private final ComponentRepository componentRepository;
    private final CommandTemplateRepository commandTemplateRepository;
    private List<Component> components = new ArrayList<>();
    private Map<String, String> buildSpec = new HashMap<>();
    @Override
    public EngineBuilder addComponent(Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public EngineBuilder addBuilderSpec(String key, String value) {
        buildSpec.put(key, value);
        return this;
    }
    
    private String getBuildSpec(String key){
        if(!buildSpec.containsKey(key)){
            throw new IllegalArgumentException("Build spec key not found: "+key);
        }
        return buildSpec.get(key);
    }
    
    @Override
    public String build() {
        StringBuilder dockerfile = new StringBuilder("FROM %s:latest\n".formatted(getBuildSpec(SPEC_OS_TYPE))); // Base image

        Set<String> processedComponents = new HashSet<>(); // Track processed components

        for (Component component : components) {
            processComponent(component, dockerfile, processedComponents);
        }

        return dockerfile.toString();
    }

    private void processComponent(Component component, StringBuilder dockerfile, Set<String> processedComponents) {
        if (!processedComponents.add(component.getId())) {
            // Component already processed, prevent further processing to avoid infinite loops
            return;
        }

//        // Process dependencies first
//        if (component.getCommandTemplatesInstances() != null) {
//            for (CommandTemplateInstance instance : component.getCommandTemplatesInstances()) {
//                CommandTemplate template = commandTemplateRepository
//                        .findById(instance.getId())
//                        .orElseThrow(
//                                () -> CommandTemplateNotFound.byId()
//                                        .errorCode(-1)
//                                        .id(instance.getId())
//                                        .build()
//                        );
//                // Process dependent components
//                if (template.getDependOnComponents() != null) {
//                    for (String componentId : template.getDependOnComponents()) {
//                        Component dependOnComponent = componentRepository
//                                .findById(componentId)
//                                .orElseThrow(
//                                        () -> ComponentNotFound.byId()
//                                                .errorCode(-2)
//                                                .id(componentId)
//                                                .build()
//                                );
//                        processComponent(dependOnComponent, dockerfile, processedComponents);
//                    }
//                }
//                // Now, append the current component's commands
//                appendCommandsFromTemplate(template, instance, dockerfile);
//            }
//        }
//        // Assuming component.getCommandTemplates() directly gives CommandTemplate objects
//        if(component.getCommandTemplates()!=null) {
//            component.getCommandTemplates().forEach(commandTemplate -> appendCommandsFromTemplate(commandTemplate, null, dockerfile));
//        }
    }

    private void appendCommandsFromTemplate(CommandTemplate template, CommandTemplateInstance instance, StringBuilder dockerfile) {
        for (ExecutionPipeline pipeline : template.getCommandExecutionsLayers()) {
            if (pipeline.getOperatingSystem().contains(getBuildSpec(SPEC_OS_TYPE)) && pipeline.getArchitecture().contains("linux")) {
                pipeline.getExecutionCommands().forEach(command -> {
                    String processedCommand = instance != null ? substituteParameters(command, instance.getParameters()) : command;
                    dockerfile.append("RUN ").append(processedCommand).append("\n");
                });
            }
        }
    }

    private String substituteParameters(String command, Map<String, String> parameters) {
        String processedCommand = new String(command);
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            processedCommand = processedCommand.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return processedCommand;
    }
}
