package jb.dam2.discover.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Like;
import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;

@Repository
public interface LikeRepository extends CrudRepository<Like, Long> {
	public List<Like> findByUser(User user);
	public List<Like> findByUserAndShare(User user,Share share);
}
