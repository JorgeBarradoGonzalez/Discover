package jb.dam2.discover.pojo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
@Table(name = "REPLY_ALERTS")
public class ReplyAlert {
	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length=9)
	private Long id;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userShared")
	private User userShared;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userReplied")
	private User userReplied;
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="share") 
	private Share share;
	private LocalDateTime date;
	@Column(name="seen")
	private boolean isSeen;
	@Column(length=400)
	private String repliedText;
	
	public ReplyAlert() {
		this.id = Long.valueOf(999);
		this.userShared = new User();
		this.userReplied = new User();
		this.share = new Share();
		this.date=LocalDateTime.of(1999, 9, 9, 9, 9, 9);
		this.isSeen = false;
		this.repliedText = "jbError";
	}
	
	
}
