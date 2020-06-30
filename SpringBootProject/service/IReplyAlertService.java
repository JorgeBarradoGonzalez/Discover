package jb.dam2.discover.service;

import java.util.List;

import jb.dam2.discover.pojo.ReplyAlert;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.wrapper.CustomReplyAlertWrapper;
import jb.dam2.discover.wrapper.ReplyAlertWrapper;

public interface IReplyAlertService {
	public void save(ReplyAlert replyAlert);
	public List<ReplyAlert> findByUserShared(User user);
	public void manageSeenReplyAlerts(CustomReplyAlertWrapper replyAlerts);
}
