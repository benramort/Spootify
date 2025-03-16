package com.deusto.theComitte.Spootify.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    Song findById(long id); 
}
