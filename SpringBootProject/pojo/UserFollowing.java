package jb.dam2.discover.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "USER_FOLLOWING")
public class UserFollowing {
	
	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length=9)
	private Long id;
	//QUITO EL CASCADE PARA GUARDAR MANUALMENTE
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userFollowing")
	private User userFollowing;
	//QUITO EL CASCADE PARA GUARDAR MANUALMENTE
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="userFollowed")
	private User userFollowed;
	
	public UserFollowing() {
		this.id = Long.valueOf(999);
		this.userFollowing = new User();
		this.userFollowed = new User();
	}

}
