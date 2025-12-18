package com.devcom.OnlyDev.Service;

import com.devcom.OnlyDev.Modal.Posts;
import com.devcom.OnlyDev.Modal.UserInteractions;
import com.devcom.OnlyDev.Modal.UserProfile;
import com.devcom.OnlyDev.Repository.PostRepository;
import com.devcom.OnlyDev.Repository.UserInteractionRepository;
import com.devcom.OnlyDev.Repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserInteractionService {

    UserInteractionRepository userInteractionRepo;

    UserProfileRepository userProfileRepo;

    PostRepository postRepo;



    public double calculateTagsAffinity(String postId,String userId){
        UserProfile profile = userProfileRepo.findById(userId)
                .orElse(null);
        UserInteractions userInteractions=userInteractionRepo.findByUserId(userId).orElse(null);
        Posts post = postRepo.findById(userId)
                .orElse(null);
        if(post!=null){
            return 0.0;
        }
        double totalScore=0;
        for(String tag:post.getTags()){
            double score=0;

            if(userInteractions!=null&& userInteractions.getTagInteractions().containsKey(tag)){
                score=userInteractions.getTagInteractions().get(tag);

            }
            score+=1;
            totalScore+=score;
            assert userInteractions != null;
            userInteractions.getTagInteractions().put(tag,score);

        }
        assert userInteractions != null;
        userInteractionRepo.save(userInteractions);
        return totalScore;
    }

    public double calculateAuthorAffinity(String postId,String userId){
        UserProfile profile = userProfileRepo.findById(userId)
                .orElse(null);
        UserInteractions userInteractions=userInteractionRepo.findByUserId(userId).orElse(null);
        Posts post = postRepo.findById(userId)
                .orElse(null);
        if(post!=null){
            return 0.0;
        }
        if(userInteractions==null){
            return 0.0;
        }
        double authorAffinity=0;
        if(userInteractions.getAuthorInteractions().containsKey(post.getUserid())) {
            authorAffinity = userInteractions.getAuthorInteractions().get(post.getUserid());
        }
        userInteractions.getAuthorInteractions().put(post.getUserid(),authorAffinity+1);
        userInteractionRepo.save(userInteractions);

        return authorAffinity;
    }
    public double calculateContentAffinity(String postId,String userId){
        UserProfile profile = userProfileRepo.findById(userId)
                .orElse(null);
        UserInteractions userInteractions=userInteractionRepo.findByUserId(userId).orElse(null);
        Posts post = postRepo.findById(userId)
                .orElse(null);
        return  0;


    }


}
