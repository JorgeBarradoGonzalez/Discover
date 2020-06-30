package jb.dam2.discover.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jb.dam2.discover.pojo.Artist;

@Repository
public interface ArtistRepository extends CrudRepository<Artist, String> {

}
