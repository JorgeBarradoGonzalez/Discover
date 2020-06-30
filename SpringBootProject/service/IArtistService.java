package jb.dam2.discover.service;

import java.util.List;
import java.util.Optional;

import jb.dam2.discover.pojo.Artist;

public interface IArtistService {
	public void save(Artist artist);
	public Optional<Artist> findById(String id);
	public List<Artist> findAll();
	public boolean exists(String id);
}
