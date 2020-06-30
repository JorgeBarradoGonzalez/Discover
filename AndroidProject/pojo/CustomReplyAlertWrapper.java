package jb.dam2.discover.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CustomReplyAlertWrapper{
	@Singular
	private List<String> replyAlertIds;

	public CustomReplyAlertWrapper() {
		this.replyAlertIds = new ArrayList<String>();
	}
}
