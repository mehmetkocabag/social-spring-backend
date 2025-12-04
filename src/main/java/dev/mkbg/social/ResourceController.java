package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "description", required = false) String description) {

        try {
            Resource savedResource = resourceService.storeResource(file, name, resourceType, description);

            Map<String, String> response = new HashMap<>();
            response.put("id", savedResource.getId().toString());
            response.put("name", savedResource.getName());
            response.put("resourceType", savedResource.getResourceType());
            response.put("contentType", savedResource.getContentType());
            response.put("uploadedAt", savedResource.getUploadedAt().toString());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload resource: " + e.getMessage()));
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getResource(@PathVariable String name) {
        return resourceService.getResourceByName(name)
                .map(resource -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(resource.getContentType()));
                    return new ResponseEntity<>(resource.getData().getData(), headers, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/list")
    public ResponseEntity<?> listResources(@RequestParam(required = false) String resourceType) {
        List<Resource> resources;

        if (resourceType != null && !resourceType.isEmpty()) {
            resources = resourceService.getResourcesByType(resourceType);
        } else {
            resources = resourceService.getAllResources();
        }

        List<Map<String, String>> responseList = resources.stream()
                .map(resource -> Map.of(
                        "id", resource.getId().toString(),
                        "name", resource.getName(),
                        "resourceType", resource.getResourceType(),
                        "contentType", resource.getContentType(),
                        "description", resource.getDescription() != null ? resource.getDescription() : ""
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResource(@PathVariable String id) {
        try {
            resourceService.deleteResource(new ObjectId(id));
            return ResponseEntity.ok(Map.of("message", "Resource deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete resource: " + e.getMessage()));
        }
    }
}