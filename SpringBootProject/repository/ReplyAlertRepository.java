package jb.dam2.discover.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Like;
import jb.dam2.discover.pojo.ReplyAlert;
import jb.dam2.discover.pojo.User;

@Repository
public interface ReplyAlertRepository extends CrudRepository<ReplyAlert, Long> {
	public List<ReplyAlert> findByUserShared(User user);
}
