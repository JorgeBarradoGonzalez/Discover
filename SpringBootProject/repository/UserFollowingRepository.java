package jb.dam2.discover.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.User;
import jb.dam2.discover.pojo.UserFollowing;

@Repository
public interface UserFollowingRepository extends CrudRepository<UserFollowing, Long> {
	List<UserFollowing> findByUserFollowing(User user);
	List<UserFollowing> findByUserFollowedAndUserFollowing(User userFollowed,User userFollowing);
}
