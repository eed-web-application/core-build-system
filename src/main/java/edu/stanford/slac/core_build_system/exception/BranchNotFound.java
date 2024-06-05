package edu.stanford.slac.core_build_system.exception;

import edu.stanford.slac.ad.eed.baselib.exception.ControllerLogicException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.getAllMethodInCall;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Branch not found")
public class BranchNotFound extends ControllerLogicException {
    @Builder(builderMethodName = "byName")
    public BranchNotFound(Integer errorCode, String branchName) {
        super(errorCode,
                String.format("The branch '%s' has not been found", branchName),
                getAllMethodInCall()
        );
    }
}
