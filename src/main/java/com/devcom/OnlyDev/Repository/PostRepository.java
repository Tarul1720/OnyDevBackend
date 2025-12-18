package com.devcom.OnlyDev.Repository;

import com.devcom.OnlyDev.Modal.Posts;
import com.devcom.OnlyDev.Modal.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface PostRepository extends MongoRepository<Posts, String> {
    // Find posts by user
    List<Posts> findByUserid(String userid);
    Page<Posts> findByUserid(String userid, Pageable pageable);

    Page<Posts> findByUseridIn(List<String> userids, Pageable pageable);
    Page<Posts> findByLikesIn(List<String> likes,Pageable pageable);
    // Find posts by tag
    List<Posts> findByTagsContaining(String tag);

    // Find posts by title (case insensitive)
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Posts> findByTitleContaining(String title);

    // Find posts created between dates
    List<Posts> findByCreatetimeBetween(Date start, Date end);

    // Find posts with minimum likes
    List<Posts> findByLikesGreaterThanEqual(int likes);

    // Find recent posts (ordered by create time)
    List<Posts> findAllByOrderByCreatetimeDesc();
    Page<Posts> findAllByOrderByCreatetimeDesc(Pageable pageable);

    // Find popular posts (ordered by likes)
    List<Posts> findAllByOrderByLikesDesc();
    Page<Posts> findAllByOrderByLikesDesc(Pageable pageable);

    // Count posts by user
    long countByUserid(String userid);

    @Query("{ '$or': [ { 'title': { '$regex': ?0, '$options': 'i' } }, " +
            "{ 'description': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Posts> searchProjects(String keyword);

    @Query("{ 'tags': { '$in': ?0 } }")
    List<Posts> findByTagsIn(List<String> tags);
}
