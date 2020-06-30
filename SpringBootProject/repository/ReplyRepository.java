package jb.dam2.discover.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Reply;
import jb.dam2.discover.pojo.Share;

@Repository
public interface ReplyRepository extends CrudRepository<Reply, Long> {
	public List<Reply> findByShare(Share share);
}
