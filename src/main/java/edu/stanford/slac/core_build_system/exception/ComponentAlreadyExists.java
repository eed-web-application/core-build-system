package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "component already exists")
public class ComponentAlreadyExists extends ControllerLogicException {
    @Builder(builderMethodName = "byNameAndVersion")
    public ComponentAlreadyExists(Integer errorCode, String name, String version) {
        super(errorCode,
                String.format("The component with name '%s' and tagName '%s' already exists", name, version),
                getAllMethodInCall()
        );
    }
    @Builder(builderMethodName = "byName")
    public ComponentAlreadyExists(Integer errorCode, String name) {
        super(errorCode,
                String.format("The component with name '%s' already exists", name),
                getAllMethodInCall()
        );
    }
}