package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import jb.dam2.discover.pojo.Like;

public interface ILikeService {
	public void save(Like like);
	public void delete(Like like);
	public List<Like> findUserLikes(String username);
	public Optional<Like> manageLike(String sessionId,String shareId);
	public Optional<Like> isLiked(String sessionId,String shareId);
}
