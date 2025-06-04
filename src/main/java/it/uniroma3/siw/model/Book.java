package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.*;
import java.time.*; // solo se ti serve LocalDate ecc.

/**
 * Rappresenta un libro con titolo, anno, sinossi, autori, recensioni e
 * immagini.
 */
@Entity
public class Book {

	private String imagePath;

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String title;

	@PastOrPresent
	private Integer publicationYear;

	@Lob
	@Column(length = 10000)
	private String synopsis;

	@ManyToMany
	@JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
	private Set<Author> authors = new HashSet<>();

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	// --- Getters & Setters ---

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	// --- Helper methods per le relazioni bidirezionali ---

	public void addAuthor(Author author) {
		this.authors.add(author);
		author.getBooks().add(this);
	}

	public void removeAuthor(Author author) {
		this.authors.remove(author);
		author.getBooks().remove(this);
	}

	public void addReview(Review review) {
		this.reviews.add(review);
		review.setBook(this);
	}

	public void removeReview(Review review) {
		this.reviews.remove(review);
		review.setBook(null);
	}

	public void addImage(Image image) {
		this.images.add(image);
		image.setBook(this);
	}

	public void removeImage(Image image) {
		this.images.remove(image);
		image.setBook(null);
	}

	// (Opzionale) equals, hashCode, toString...
}
