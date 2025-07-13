package com.library.library.Model;

import com.library.library.Model.Enumerations.AuthorStatus;
import com.library.library.Model.Enumerations.Country;
import com.library.library.Model.Enumerations.Gender;
import com.library.library.Utils.SlugGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                // Username must be unique per role (but role is in UserCredentials)
                // So we need a composite key with credentials.role
                // This requires a workaround since we can't directly reference credentials.role here.
                // Alternative: Use a database trigger or custom validation.
                @UniqueConstraint(
                        name = "uk_username_key_role",
                        columnNames = {"username_key", "id"} // Workaround: Use user_id to link to credentials
                ),
                @UniqueConstraint(
                        name = "uk_username_display_name_role",
                        columnNames = {"username_display_name", "id"}
                )
        }
)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Display name is required")
    @Size(min = 4, max = 20, message = "Username name must be 3-20 characters")
    @Column(name = "username_display_name", unique = true, nullable = false)
    private String displayName;//unique for url search

    @NotBlank(message = "Username key is required")
    @Pattern(regexp = "^[a-z0-9_-]{3,20}$",
            message = "Username key must be 3-20 chars (lowercase, numbers, _, -)")
    @Column(name = "username_key", unique = true, nullable = false)
    private String usernameKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_status")
    private AuthorStatus authorStatus;


    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Size(max = 500, message = "Bio must be ≤ 500 characters")
    @Column(name = "bio")
    private String bio;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_of_join", nullable = false, updatable = false)
    private Date dateOfJoin;

    @Column(name = "notification_number")
    private Integer notificationNumber = 0;

    @ElementCollection
    @CollectionTable(name = "date_of_birth_user", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "date_of_birth_key")
    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth_value")
    private Map<LocalDate, Boolean> dateOfBirth;// could be hidden

    //image of profile
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "image_content_type")
    private String imageContentType;

    @ElementCollection
    @CollectionTable(name = "country_user", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "country_key")
    @Enumerated(EnumType.STRING)
    @Column(name = "country_value")
    private Map<Country, Boolean> country;

/*
    @ElementCollection
    @CollectionTable(name = "carer_user", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "carer_key")
    @Column(name = "carer_value")
    private Map<String, Boolean> carer;
*/

    //book that user published
    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
    private List<Book> uploadedBooks = new ArrayList<>();

    @OneToMany(mappedBy = "authors")
    private List<Book> authoredBooks = new ArrayList<>();

    //user book have download by him
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Downloads> downloads = new ArrayList<>();

    //rating of user
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<BookList> bookLists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();
    //TODO  = new ArrayList<>("welcome in our web"); could i make it later

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserCredentials credentials;




//FIXME
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CommentVote> commentVotes = new ArrayList<>();

    //or in service put new date when sign in
    @PrePersist
    protected void onCreate() {
        dateOfJoin = new Date();
        addDefaultBookList();
        this.setUsernameKey(SlugGenerator.createKey(this.displayName));
    }

    private void addDefaultBookList() {
        BookList lovedList = new BookList();
        lovedList.setName("Loved List");
        lovedList.setUser(this);
        lovedList.setIsDefault(true);
        this.bookLists.add(lovedList);
    }
}
