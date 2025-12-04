package dev.mkbg.social;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    public Resource storeResource(MultipartFile file, String name, String resourceType, String description) throws IOException {
        Resource resource = new Resource();
        resource.setName(name);
        resource.setResourceType(resourceType);
        resource.setContentType(file.getContentType());
        resource.setData(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        resource.setDescription(description);
        resource.setUploadedAt(new Date());

        return resourceRepository.save(resource);
    }

    public Optional<Resource> getResourceByName(String name) {
        return resourceRepository.findByName(name);
    }

    public List<Resource> getResourcesByType(String resourceType) {
        return resourceRepository.findByResourceType(resourceType);
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public void deleteResource(ObjectId id) {
        resourceRepository.deleteById(id);
    }
}