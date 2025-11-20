package com.library.library.model.enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum Category {
    // Fiction Genres
    LITERARY_FICTION("Literary Fiction", "literary-fiction"),
    HISTORICAL_FICTION("Historical Fiction", "historical-fiction"),
    CONTEMPORARY_FICTION("Contemporary Fiction", "contemporary-fiction"),
    MAGICAL_REALISM("Magical Realism", "magical-realism"),

    // Sci-Fi & Fantasy
    SCIENCE_FICTION("Science Fiction", "science-fiction"),
    FANTASY("Fantasy", "fantasy"),
    HIGH_FANTASY("High Fantasy", "high-fantasy"),
    URBAN_FANTASY("Urban Fantasy", "urban-fantasy"),
    DYSTOPIAN("Dystopian", "dystopian"),
    STEAMPUNK("Steampunk", "steampunk"),
    CYBERPUNK("Cyberpunk", "cyberpunk"),
    SPACE_OPERA("Space Opera", "space-opera"),

    // Mystery & Thriller
    MYSTERY("Mystery", "mystery"),
    THRILLER("Thriller", "thriller"),
    CRIME("Crime", "crime"),
    DETECTIVE("Detective", "detective"),
    LEGAL_THRILLER("Legal Thriller", "legal-thriller"),
    PSYCHOLOGICAL_THRILLER("Psychological Thriller", "psychological-thriller"),
    SPY_THRILLER("Spy Thriller", "spy-thriller"),

    // Romance
    ROMANCE("Romance", "romance"),
    HISTORICAL_ROMANCE("Historical Romance", "historical-romance"),
    PARANORMAL_ROMANCE("Paranormal Romance", "paranormal-romance"),
    EROTIC("Erotic", "erotic"),

    // Horror & Paranormal
    HORROR("Horror", "horror"),
    GOTHIC("Gothic", "gothic"),
    SUPERNATURAL("Supernatural", "supernatural"),
    PARANORMAL("Paranormal", "paranormal"),
    ZOMBIE("Zombie", "zombie"),
    VAMPIRE("Vampire", "vampire"),

    // Non-Fiction
    BIOGRAPHY("Biography", "biography"),
    AUTOBIOGRAPHY("Autobiography", "autobiography"),
    MEMOIR("Memoir", "memoir"),
    ESSAYS("Essays", "essays"),
    JOURNALISM("Journalism", "journalism"),

    // Academic & Educational
    TEXTBOOK("Textbook", "textbook"),
    REFERENCE("Reference", "reference"),
    SCIENCE("Science", "science"),
    MATHEMATICS("Mathematics", "mathematics"),
    PHILOSOPHY("Philosophy", "philosophy"),
    PSYCHOLOGY("Psychology", "psychology"),

    // Business & Finance
    BUSINESS("Business", "business"),
    ECONOMICS("Economics", "economics"),
    FINANCE("Finance", "finance"),
    INVESTING("Investing", "investing"),
    ENTREPRENEURSHIP("Entrepreneurship", "entrepreneurship"),

    // Self-Help & Wellness
    SELF_HELP("Self-Help", "self-help"),
    MOTIVATIONAL("Motivational", "motivational"),
    SPIRITUAL("Spiritual", "spiritual"),
    HEALTH("Health", "health"),
    FITNESS("Fitness", "fitness"),
    DIET("Diet", "diet"),

    // Art & Creativity
    ART("Art", "art"),
    PHOTOGRAPHY("Photography", "photography"),
    MUSIC("Music", "music"),
    WRITING("Writing", "writing"),
    FILM("Film", "film"),

    // Travel & Culture
    TRAVEL("Travel", "travel"),
    FOOD("Food", "food"),
    CULTURE("Culture", "culture"),
    ANTHROPOLOGY("Anthropology", "anthropology"),

    // Children & YA
    CHILDREN("Children", "children"),
    PICTURE_BOOK("Picture Book", "picture-book"),
    MIDDLE_GRADE("Middle Grade", "middle-grade"),
    YOUNG_ADULT("Young Adult", "young-adult"),

    // Other Formats
    GRAPHIC_NOVEL("Graphic Novel", "graphic-novel"),
    COMIC_BOOK("Comic Book", "comic-book"),
    MANGA("Manga", "manga"),
    AUDIOBOOK("Audiobook", "audiobook"),
    SHORT_STORIES("Short Stories", "short-stories"),
    POETRY("Poetry", "poetry"),
    DRAMA("Drama", "drama"),

    // Miscellaneous
    HUMOR("Humor", "humor"),
    SATIRE("Satire", "satire"),
    TRUE_CRIME("True Crime", "true-crime"),
    DIY("DIY", "diy"),
    GARDENING("Gardening", "gardening"),
    SPORTS("Sports", "sports"),
    MILITARY("Military", "military"),
    WESTERN("Western", "western");


    private final String displayName;
    private final String slug;
}