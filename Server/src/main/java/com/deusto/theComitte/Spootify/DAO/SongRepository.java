package com.deusto.theComitte.Spootify.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    Song findById(long id);
    List<Song> findByAlbumId(long albumId);

    @Query("SELECT s FROM Song s JOIN s.album a JOIN a.artists ar WHERE ar.id = :artistId")
    List<Song> findByArtistId(@Param("artistId") long artistId);

    @Query("SELECT s FROM Song s JOIN s.album a JOIN a.artists ar WHERE ar.id = :artistId AND a.id = :albumId")
    List<Song> findByArtistIdAndAlbumId(@Param("artistId") long artistId, @Param("albumId") long albumId);

    @Query("SELECT s FROM Song s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%',:name, '%'))")
    List<Song> findByName(String name);
}
