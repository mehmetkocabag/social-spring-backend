package dev.mkbg.social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "resources")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
    @Id
    private ObjectId id;
    private String name;
    private String resourceType;
    private String contentType;
    private Binary data;
    private String description;
    private Date uploadedAt;
}
