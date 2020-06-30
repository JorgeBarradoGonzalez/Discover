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
public class AlertInfo {
        private String mId;
        private String mShareId;
        private String mUsernameReplied;
        private String mShareTitle;
        private String mRepliedText;
        private boolean isSeen;

    public AlertInfo() {
        this.mId = "jbError";
        this.mShareId = "jbError";
        this.mUsernameReplied = "jbError";
        this.mShareTitle = "jbError";
        this.mRepliedText = "jbError";
        this.isSeen = false;
    }
}
