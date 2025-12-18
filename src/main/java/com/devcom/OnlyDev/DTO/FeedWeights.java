package com.devcom.OnlyDev.DTO;

import com.devcom.OnlyDev.Controller.PostsController;
import com.devcom.OnlyDev.Modal.Posts;

import java.time.Duration;
import java.time.Instant;

public class FeedWeights {
    private static final double W_LIKES = 0.20;
    private static final double W_COMMENTS = 0.25;
    private static final double W_SHARES = 0.25;
    private static final double W_CONTENT_QUALITY = 0.15;
    private static final double W_USER_RELEVANCE = 0.30;

    /** Time decay controls how fast old posts drop off */
    private static final double DECAY_HALF_LIFE_HOURS = 12.0;

//    private double scorePostForUser(String userId, Posts p) {
//        double like = Math.log(1 + p.getLikes().size());
//        double comment = Math.log(1 + p.getComments().size());
//        double view = Math.log(1 + p.getViewCount());
//        double ageHours = Duration.between(p.getCreatetime().toInstant(), Instant.now()).toHours();
//        double recency = Math.exp(-0.05 * ageHours); // lambda=0.05
//
//        double authorAffinity = interactionService.authorAffinity(userId, p.getAuthorId()); // 0..1
//        double personal = interactionService.personalSimilarity(userId, p); // 0..1
//
//        double score = 0.8*like + 1.2*comment + 0.4*view + 2.0*recency + 3.0*authorAffinity + 4.0*personal + (p.isReel()?0.5:0.0);
//        return score;
//    }
//

}
