package jb.dam2.discover.service;

import java.util.List;

import jb.dam2.discover.pojo.Reply;

public interface IReplyService {
	public void save(Reply share);
	public List<Reply> findAll();
	public List<Reply> findByShare(String shareId);
	public Reply manageUpload(String sessionId,String shareId,String text);
}
