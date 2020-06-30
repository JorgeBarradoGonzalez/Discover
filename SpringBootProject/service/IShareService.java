package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;

public interface IShareService {
	public void save(Share share);
	public Optional<Share> findById(Long id);
	public List<Share> findAll();
	public boolean exists(Long id);
	public Optional<Share> manageUpload(String videoId,Long sessionId,String comment,String artist,String videoTitle);
	public List<Share> findFollowedShares(String sessionId);
	public List<Share> findUserShares(User user);
}
