package jb.dam2.discover.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "USERS")
public class User {
	
	@Include
	@Id
	@Column(length=20)
	private String username;
	@Column(length=60)
	private String password;
	@Column(length=50)
	private String email;
	
	public User() {
		this.username = "jbError";
		this.password = "jbError";
		this.email = "jbError";
	}
}
