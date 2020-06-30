package jb.dam2.discover.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode.Include;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "SHARES")
public class Share {
	
	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length=9)
	private Long id;
	@Column(length=12)
	private String videoId;
	@ManyToOne(cascade= CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="user") 
	private User user;
	@Embedded
	private Comment comment;
	@ManyToOne(cascade= CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name="artist") 
	private Artist artist;
	@Column(length=100)
	private String videoTitle;
	private LocalDateTime date;
	
	public Share() {
		this.id = Long.valueOf(999);
		this.user = new User();
		this.videoId = "jbError";
		this.comment = new Comment();
		this.videoTitle = "jbError";
		this.date=LocalDateTime.of(1999, 9, 9, 9, 9, 9);
	}
	
}
