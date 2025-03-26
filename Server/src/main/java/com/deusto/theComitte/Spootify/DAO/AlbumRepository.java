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

    @Query("SELECT a FROM Albums WHERE artistId = :artistId")
    List<Album> findByArtistId(@Param("artistId") long artistId);
}
