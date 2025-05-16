package com.deusto.theComitte.Spootify.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deusto.theComitte.Spootify.DAO.ArtistRepository;
import com.deusto.theComitte.Spootify.DAO.PlayListRepository;
import com.deusto.theComitte.Spootify.DAO.SongRepository;
import com.deusto.theComitte.Spootify.DAO.UserRepository;
import com.deusto.theComitte.Spootify.entity.Song;
import com.deusto.theComitte.Spootify.entity.SongList;
import com.deusto.theComitte.Spootify.entity.User;

@Service
public class PlaylistService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    PlayListRepository songListRepository;

    @Autowired
    private UserService userService;

    // public void addSongsToUser(long userId, List<Long> songIds, long songListId) {
    //     User user = userService.getActiveUser(userId);
    //     if (user == null) {
    //         throw new RuntimeException("User does not exist");
    //     }
    //     for (long songId : songIds) {
    //         Song song = songRepository.findById(songId);
    //         if (song == null) {
    //             throw new RuntimeException("Song does not exist");
    //         }

    //         for (SongList songList : user.getSongLists()) {
    //             if (songList.getId().equals(songListId)) {
    //                 songList.getSongs().add(song);
    //                 songList.setUser(user);
    //                 return;
    //             }
    //         }
    //     }
    // }

    /**
     * Añade una canción a una playlist de un usuario autenticado.
     * 
     * Busca el usuario activo mediante el token, la canción por su ID y la playlist por su ID.
     * Si alguno no existe, lanza una excepción. Si todos existen, añade la canción a la playlist y guarda los cambios.
     * 
     * @param token Token del usuario autenticado
     * @param songId ID de la canción a añadir
     * @param songListId ID de la playlist a la que se añade la canción
     * @throws RuntimeException Si el usuario, la canción o la playlist no existen
     */
    public void addSongToPlayList(long token, long songId, long songListId) {
        User user = userService.getActiveUser(token); // Obtén el usuario activo
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
    
        Song song = songRepository.findById(songId); // Busca la canción en la base de datos
        if (song == null) {
            throw new RuntimeException("Song does not exist");
        }
    
        SongList songList = songListRepository.findById(songListId); // Busca la playlist en la base de datos
        if (songList == null) {
            throw new RuntimeException("SongList does not exist");
        }
    
        // Añade la canción a la playlist
        songList.getSongs().add(song);
    
        // Guarda la playlist en la base de datos
        songListRepository.save(songList);
        userRepository.save(user); // Guarda el usuario en la base de datos
    }

    /**
     * Crea una nueva playlist para el usuario autenticado.
     * 
     * Se crea una nueva playlist con el nombre y visibilidad indicados, se asocia al usuario y se guardan los cambios en la base de datos.
     * 
     * @param token Token del usuario autenticado
     * @param name Nombre de la nueva playlist
     * @param isPublic Indica si la playlist será pública o privada
     * @throws RuntimeException Si el usuario no está autenticado
     */
    public void createPlayList(long token, String name, boolean isPublic) {
        User user = userService.getActiveUser(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        SongList songList = new SongList(name, isPublic, user);
        user.addSongList(songList);
        userRepository.save(user);
        songListRepository.save(songList);
    }

    /**
     * Obtiene todas las playlists del usuario autenticado.
     * 
     * Busca el usuario activo mediante el token y devuelve la lista de sus playlists.
     * 
     * @param token Token del usuario autenticado
     * @return Lista de playlists del usuario
     * @throws RuntimeException Si el usuario no existe
     */
    public List<SongList> getPlayLists(long token) {
        User user = userService.getActiveUser(token);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        User updatedUser = userRepository.findById(user.getId()); //Beñat del futuro, busca una manera de hacer esto mejor
        return updatedUser.getSongLists();
    }

    /**
     * Obtiene una playlist por su ID, comprobando permisos de acceso.
     * 
     * El usuario debe estar autenticado. Si la playlist no es pública y no pertenece al usuario,
     * se lanza una excepción. Si existe y el usuario tiene acceso, se devuelve la playlist.
     * 
     * @param token Token del usuario autenticado
     * @param songListId ID de la playlist a obtener
     * @return La playlist solicitada
     * @throws RuntimeException Si el usuario o la playlist no existen, o si no tiene permisos de acceso
     */
    public SongList getPlaylistById(long token, long songListId) {
        User user = userService.getActiveUser(token);
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        SongList songList = songListRepository.findById(songListId);
        if (songList == null) {
            throw new RuntimeException("SongList does not exist");
        }
        System.out.println("SongListUserID: " + songList.getUser().getId());
        System.out.println("UserID: " + user.getId());
        if (songList.getUser().getId() != user.getId() && !songList.getIsPublic()) {
            throw new RuntimeException("User does not have access to this playlist");
        }

        return songList;
    }

    /**
     * Comparte una playlist haciéndola pública.
     * 
     * El usuario debe estar autenticado y ser el propietario de la playlist.
     * Si la playlist no es pública, se actualiza su estado a pública y se guarda.
     * 
     * @param id ID de la playlist a compartir
     * @param token Token del usuario autenticado
     * @throws RuntimeException Si el usuario no está autenticado o la playlist no existe
     */
    public void sharePlaylist(long id, long token) {
        User user = userService.getActiveUser(token);
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        SongList songList = songListRepository.findById(id);
        if (songList == null) {
            throw new RuntimeException("Playlist does not exist");
        }
        if(songList.getIsPublic() == false){
            songList.setPublic(true);
            songListRepository.save(songList);
        }
    }
}
