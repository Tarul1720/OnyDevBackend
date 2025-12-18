package com.devcom.OnlyDev.Service;

import com.devcom.OnlyDev.Modal.Posts;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class FeedRankingService {
    public final double w_like = 0.8;
    public final double w_comment = 1.2;
    public final double w_view = 0.4;
    public final double w_recency = 2.0;
    public final double w_author = 3.0;
    public final double w_personal = 4.0;
    public final double w_isReel = 0.5;

    private double scorePostForUser(String userId, Posts p) {
        double like = Math.log(1 + p.getLikes().size());
        double comment = Math.log(1 + p.getComments().size());
        double view = Math.log(1 + p.getView().size());
        double ageHours = Duration.between(p.getCreatetime().toInstant(), Instant.now()).toHours();
        double recency = Math.exp(-0.05 * ageHours); // lambda=0.05

        //double authorAffinity = interactionService.authorAffinity(userId, p.getAuthorId()); // 0..1
        //double personal = interactionService.personalSimilarity(userId, p); // 0..1

        double score = 0.8*like + 1.2*comment + 0.4*view + 2.0*recency + 3.0; //*authorAffinity + 4.0*personal + (p.isReel()?0.5:0.0);
        return score;
    }

}
