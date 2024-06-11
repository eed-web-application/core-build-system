package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Component without build os")
public class BuildOSMissing extends ControllerLogicException {
    @Builder(builderMethodName = "byName")
    public BuildOSMissing(Integer errorCode, String name) {
        super(errorCode,
                String.format("The component '%s' has not build os defined", name),
                getAllMethodInCall()
        );
    }
}