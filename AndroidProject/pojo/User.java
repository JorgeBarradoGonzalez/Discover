package jb.dam2.discover.pojo;

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
public class User {
	
	@Include
	private String email;
	private String password;
	
	public User() {
		this.email = "jbError";
		this.password = "jbError";
	}
}
