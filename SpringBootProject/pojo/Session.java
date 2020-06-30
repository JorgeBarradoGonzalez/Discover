package jb.dam2.discover.pojo;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
@Table(name = "SESSIONS")
public class Session {
	
	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length=9)
	private Long id;
	//ELIMINADO EL CASCADE DE USER POR PROBLEMAS DE REGISTROS YA EXISTENTES
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user") 
	private User user;
	
	public Session() {
		this.id = Long.valueOf(999);
		this.user = new User();
	}

}
