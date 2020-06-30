package jb.dam2.discover.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReplyInfo {
    private String mUsername;
    private String mText;

    public ReplyInfo() {
        this.mUsername = "jbError";
        this.mText = "jbError";
    }
}
