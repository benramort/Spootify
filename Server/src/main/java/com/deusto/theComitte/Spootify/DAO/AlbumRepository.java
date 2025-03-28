package com.deusto.theComitte.Spootify.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deusto.theComitte.Spootify.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long>{
    Album findById(long id);

    // @Query("SELECT a FROM albums a WHERE a.artistId = :artistId")
    // List<Album> findByArtistId(@Param("artistId") long artistId);
}
