package pl.lukaszgrymulski.noteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, LocalDateTime> {

    @Query(value = "SELECT n FROM NoteEntity n WHERE n.is_deleted=0 ORDER BY n.id DESC")
    List<NoteEntity> findAllRecentNoteVersions();

    @Query(value = "SELECT n FROM NoteEntity n WHERE n.is_deleted=0 and n.id=:id")
    Optional<NoteEntity> findRecentNoteVersionById(Long id);

    @Query(value = "SELECT MAX(n.id) FROM NoteEntity n")
    Long findMaxId();

    List<NoteEntity> findAllByIdOrderByVersionDesc(Long id);
}
