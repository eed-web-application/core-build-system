package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "command template not found")
public class CommandTemplateNotFound extends ControllerLogicException {
    @Builder(builderMethodName = "byId")
    public CommandTemplateNotFound(Integer errorCode, String id) {
        super(errorCode,
                String.format("The command template with id '%s' has not been found", id),
                getAllMethodInCall()
        );
    }
}