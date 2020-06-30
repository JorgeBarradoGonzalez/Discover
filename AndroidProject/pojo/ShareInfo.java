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
public class ShareInfo {
    private String mShareTitle;
    private String mShareVideoId;
    private String mShareComment;
    private String mShareId;
    private String mUsername;

    public ShareInfo() {
        this.mShareTitle = "";
        this.mShareVideoId = "";
        this.mShareComment = "";
        this.mShareId = "";
        this.mUsername = "";
    }
}
