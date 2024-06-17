package edu.stanford.slac.core_build_system.repository;


import edu.stanford.slac.core_build_system.model.BuildImage;
import edu.stanford.slac.core_build_system.model.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BuildImageRepository extends MongoRepository<BuildImage, String> {

}
