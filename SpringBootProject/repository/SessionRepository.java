package jb.dam2.discover.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Session;
import jb.dam2.discover.pojo.User;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
	Optional<Session> findByUser(User user);
}
