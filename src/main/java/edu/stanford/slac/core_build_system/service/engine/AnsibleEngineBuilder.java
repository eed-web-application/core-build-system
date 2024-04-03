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
public class AnsibleEngineBuilder implements EngineBuilder {
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
        StringBuilder ansiblePlaybook = new StringBuilder();
        ansiblePlaybook.append("---\n"); // YAML document start
        ansiblePlaybook.append("- hosts: all\n");
        ansiblePlaybook.append("  tasks:\n");

        for (Component component : components) {
            for (CommandTemplateInstance instance : component.getCommandTemplatesInstances()) {
                // Assuming you have a way to fetch the CommandTemplate by the instance's templateId
                CommandTemplate template =commandTemplateRepository
                        .findById(instance.getId())
                        .orElseThrow(
                                ()-> CommandTemplateNotFound.byId()
                                        .errorCode(-1)
                                        .id(instance.getId())
                                        .build()
                        );
                ansiblePlaybook.append("    - name: Execute ").append(template.getName()).append("\n");
                ansiblePlaybook.append("      shell: |\n");
                for (ExecutionPipeline pipeline : template.getCommandExecutionsLayers()) {
                    for (String command : pipeline.getExecutionCommands()) {
                        ansiblePlaybook.append("        ").append(command).append("\n");
                    }
                }
            }
        }

        return ansiblePlaybook.toString();
    }
}
