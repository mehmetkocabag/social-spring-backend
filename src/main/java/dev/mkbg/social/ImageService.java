package dev.mkbg.social;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;

    public String storeImage(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ObjectId fileId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
            return fileId.toString();
        }
    }

    public GridFSFile getImageFile(String id) {
        return gridFsTemplate.findOne(new org.springframework.data.mongodb.core.query.Query(Criteria.where("_id").is(id)));
    }

    public InputStream getImageStream(GridFSFile file) {
        return gridFSBucket.openDownloadStream(file.getObjectId());
    }
}
