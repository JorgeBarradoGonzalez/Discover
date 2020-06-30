package jb.dam2.discover.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Share;
import jb.dam2.discover.pojo.User;

@Repository
public interface ShareRepository extends CrudRepository<Share, Long> {
	List<Share> findByUser(User user);
}
