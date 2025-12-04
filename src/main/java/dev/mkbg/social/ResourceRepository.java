package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends MongoRepository<Resource, ObjectId> {
    Optional<Resource> findByName(String name);
    List<Resource> findByResourceType(String resourceType);
}