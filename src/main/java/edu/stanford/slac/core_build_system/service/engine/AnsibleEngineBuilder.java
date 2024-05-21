package edu.stanford.slac.core_build_system.service.engine;

import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.CommandTemplate;
import edu.stanford.slac.core_build_system.model.CommandTemplateInstance;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ExecutionPipeline;
import edu.stanford.slac.core_build_system.repository.CommandTemplateRepository;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;

import java.util.*;

@org.springframework.stereotype.Component("ansible")
@Scope("prototype")
@RequiredArgsConstructor
public class AnsibleEngineBuilder implements EngineBuilder {
    public static final String SPEC_OS_TYPE = "osType";
    public static final String SPEC_HOST = "host";
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

    @Override
    public String build() {
        Set<String> processedComponents = new HashSet<>();
        StringBuilder playbook = new StringBuilder();

        playbook.append("---\n");
        playbook.append("- hosts: %s\n".formatted(getBuildSpec(SPEC_HOST)));
        playbook.append("  tasks:\n");

        for (Component component : components) {
            processComponent(component, playbook, processedComponents);
        }

        return playbook.toString();
    }

    /**
     * Get the value of the build spec key
     *
     * @param key the key to get the value for
     * @return the value of the key
     */
    private String getBuildSpec(String key) {
        if (!buildSpec.containsKey(key)) {
            throw new IllegalArgumentException("Build spec key not found: " + key);
        }
        return buildSpec.get(key);
    }

    /**
     * Process the component and append the ansible tasks to the playbook
     *
     * @param component           the component to process
     * @param playbook            the playbook to append the tasks to
     * @param processedComponents the set of processed components
     */
    private void processComponent(Component component, StringBuilder playbook, Set<String> processedComponents) {
        // Avoid processing the same component more than once to prevent infinite loops
        if (!processedComponents.add(component.getId())) {
            // Component already processed
            return;
        }

//        if (component.getDependOnComponentIds() != null) {
//            component.getDependOnComponentIds().forEach(
//                    componentDependency -> {
//                        Component  dependentComponent = componentRepository
//                                .findById(componentDependency.getComponentId())
//                                .orElseThrow(
//                                        ()->ComponentNotFound
//                                                .byId()
//                                                .errorCode(-1)
//                                                .id(componentDependency.getComponentId())
//                                                .build()
//                                );
//                        processComponent(dependentComponent, playbook, processedComponents);
//                    }
//            );
//        }

        // Process command templates instances if they exist
//        if (component.getCommandTemplatesInstances() != null) {
//            for (CommandTemplateInstance instance : component.getCommandTemplatesInstances()) {
//                CommandTemplate commandTemplate = commandTemplateRepository
//                        .findById(instance.getId())
//                        .orElseThrow(() -> new RuntimeException("Command template not found: " + instance.getId()));
//                // check if command depend on other component
//                if (commandTemplate.getDependOnComponents() != null) {
//                    commandTemplate.getDependOnComponents().forEach(
//                            componentId -> {
//                                Component  dependentComponent = componentRepository
//                                        .findById(componentId)
//                                        .orElseThrow(
//                                                ()->ComponentNotFound
//                                                        .byId()
//                                                        .errorCode(-1)
//                                                        .id(componentId)
//                                                        .build()
//                                        );
//                                processComponent(dependentComponent, playbook, processedComponents);
//                            }
//                    );
//                }
//                appendAnsibleTasksFromTemplate(commandTemplate, instance.getParameters(), playbook);
//            }
//        }
//
//        // Process owned command templates directly
//        if (component.getCommandTemplates() != null) {
//            for (CommandTemplate commandTemplate : component.getCommandTemplates()) {
//                appendAnsibleTasksFromTemplate(commandTemplate, null, playbook); // Assuming no parameters for simplicity
//            }
//        }

    }

    /**
     * Append the ansible tasks from the template to the playbook
     *
     * @param template        the template to get the tasks from
     * @param parameters      the parameters to substitute in the tasks
     * @param ansiblePlaybook the playbook to append the tasks to
     */
    private void appendAnsibleTasksFromTemplate(CommandTemplate template, Map<String, String> parameters, StringBuilder ansiblePlaybook) {
        for (ExecutionPipeline pipeline : template.getCommandExecutionsLayers()) {
            if (pipeline.getOperatingSystem().contains(getBuildSpec(SPEC_OS_TYPE))) { // Filtering based on target OS
                pipeline.getExecutionCommands().forEach(
                        command -> {
                            String processedCommand = (parameters != null) ? substituteParameters(command, parameters) : command;
                            ansiblePlaybook.append("    - name: \"").append(template.getName()).append("\"\n");
                            ansiblePlaybook.append("      shell: ").append(processedCommand).append("\n");
                        }
                );
            }
        }
    }

    /**
     * Substitute the parameters in the command
     *
     * @param command    the command to substitute the parameters in
     * @param parameters the parameters to substitute
     * @return the command with the parameters substituted
     */
    private String substituteParameters(String command, Map<String, String> parameters) {
        String processedCommand = command;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            processedCommand = processedCommand.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return processedCommand;
    }
}
