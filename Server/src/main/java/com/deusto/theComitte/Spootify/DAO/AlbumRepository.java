package com.deusto.theComitte.Spootify.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>{
    Album findById(long id);

    @Query("SELECT a FROM Album a JOIN a.artists ar WHERE ar.id = :artistId")
    List<Album> findByArtistId(@Param("artistId") long artistId);
    @Query("SELECT a FROM Album a WHERE LOWER(a.name) LIKE LOWER(CONCAT(:name, '%'))")
    List<Album> findByName(String name);
}
