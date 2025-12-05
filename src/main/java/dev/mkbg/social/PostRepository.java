package dev.mkbg.social;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    //non pageable search, deprecated until further use cases
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);

    //pageable overload
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);

    @Query("{ '_id': { '$lt': ?0 } }")
    List<Post> findPostsBeforeCursor(ObjectId cursor, Pageable pageable);

    @Query("{ '$and': [{ '_id': { '$lt': ?2 } }, { '$or': [{ 'title': { '$regex': ?0, '$options': 'i' } }, { 'content': { '$regex': ?1, '$options': 'i' } }] }] }")
    List<Post> findSearchResultsBeforeCursor(String title, String content, ObjectId cursor, Pageable pageable);
//    @Query("{ '_id': { '$gt': ?0 } }")
//    List<Post> findPostsAfterCursor(ObjectId cursor, Pageable pageable);
//
//    @Query("{ '$and': [{ '_id': { '$lt': ?2 } }, { '$or': [{ 'title': { '$regex': ?0, '$options': 'i' } }, { 'content': { '$regex': ?1, '$options': 'i' } }] }] }")
//    List<Post> findSearchResultsBeforeCursor(String title, String content, ObjectId cursor, Pageable pageable);
}
