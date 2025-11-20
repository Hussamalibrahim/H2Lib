//package com.library.library.Model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Date;
//


/*  FIXME the problem is to dealing with thymeleaf
     the relations is correct
*/

//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "comment_votes")
//public class CommentVote {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "vote_id")
//    private Long id;
//
//    @Column(name = "is_upvote", nullable = false)
//    private Boolean isUpvote;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "comment_id", nullable = false)
//    private Comment comment;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private Date createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = new Date();
//    }
//}