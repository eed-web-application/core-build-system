package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "component not found")
public class ComponentNotFound extends ControllerLogicException {
    @Builder(builderMethodName = "byId")
    public ComponentNotFound(Integer errorCode, String componentId) {
        super(errorCode,
                String.format("The component with id '%s' has not been found", componentId),
                getAllMethodInCall()
        );
    }
}