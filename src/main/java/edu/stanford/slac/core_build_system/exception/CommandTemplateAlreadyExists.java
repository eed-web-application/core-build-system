package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "component already exists")
public class CommandTemplateAlreadyExists extends ControllerLogicException {
    @Builder(builderMethodName = "byName")
    public CommandTemplateAlreadyExists(Integer errorCode, String name) {
        super(errorCode,
                String.format("The command template with name '%s' already exists", name),
                getAllMethodInCall()
        );
    }
}