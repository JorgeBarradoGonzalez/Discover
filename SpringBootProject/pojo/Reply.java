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
@Table(name="REPLIES")
public class Reply {
	
	@Include
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(length=9)
	private Long id;
	@Column(length=400)
	private String text;
	//NO ESTOY MUY SEGURO DE ESTO. SI LO PONGO COMO ENTIDAD EL DIAGRAMA SALE CIRCULAR
	@Column(length=20)
	private String username;
	private LocalDateTime date;
	//ELIMINADO EL CASCADE DE USER POR PROBLEMAS DE REGISTROS YA EXISTENTES
	//LAZY DA ERROR
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="share") 
	private Share share;
	
	public Reply() {
		this.id = Long.valueOf(999);
		this.text = "jbError";
		this.username = "jbError";
		this.date=LocalDateTime.of(1999, 9, 9, 9, 9, 9);
		this.share = new Share();
		
	}
}
