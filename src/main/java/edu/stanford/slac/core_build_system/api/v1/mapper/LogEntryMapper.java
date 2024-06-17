package edu.stanford.slac.core_build_system.api.v1.mapper;

import edu.stanford.slac.core_build_system.api.v1.dto.*;
import edu.stanford.slac.core_build_system.exception.ComponentNotFound;
import edu.stanford.slac.core_build_system.model.Branch;
import edu.stanford.slac.core_build_system.model.Component;
import edu.stanford.slac.core_build_system.model.ComponentDependency;
import edu.stanford.slac.core_build_system.model.LogEntry;
import edu.stanford.slac.core_build_system.repository.ComponentRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.slac.ad.eed.baselib.exception.Utility.wrapCatch;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public abstract class LogEntryMapper {
    abstract public LogEntryDTO toDTO(LogEntry logEntry);
}