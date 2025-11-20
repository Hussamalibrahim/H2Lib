//package com.library.library.Model;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
/*  FIXME the problem is to dealing with thymeleaf
     the relations is correct
*/


//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "comment")
//public class Comment {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "comment_id")
//    private Long id;
//
//    @NotBlank(message = "Comment content is required")
//    @Size(max = 1000, message = "Comment must be less than 1000 characters")
//    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
//    private String content;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @Column(name = "is_edited", columnDefinition = "BOOLEAN DEFAULT FALSE")
//    private Boolean isEdited = false;
//
//    @Column(name = "notified_replier", columnDefinition = "BOOLEAN DEFAULT FALSE")
//    private Boolean notifiedReplier = false;
//
//    // Relationships
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private Users user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id")
//    private Book book;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_comment_id")
//    private Comment parentComment;
//
//    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> replies = new ArrayList<>();
//
//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentVote> votes = new ArrayList<>();
//
//@Column(name = "notified_replier", columnDefinition = "BOOLEAN DEFAULT FALSE")
//private Boolean notifiedReplier = false;
//
//    // Lifecycle methods
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        validateCommentAssociation();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//        isEdited = true;
//        validateCommentAssociation();
//    }
//
//    private void validateCommentAssociation() {
//        if (book == null) {
//            throw new IllegalStateException("Comment must be associated with a book");
//        }
//    }
//
//    // Helper methods
//    public void addReply(Comment reply) {
//        replies.add(reply);
//        reply.setParentComment(this);
//    }
//
//    public void removeReply(Comment reply) {
//        replies.remove(reply);
//        reply.setParentComment(null);
//    }
//}