package jb.dam2.discover.pojo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString 
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class Comment {

	@Column(length=400)
	private String text;
	
	public Comment() {
		this.text = "";
	}

}
