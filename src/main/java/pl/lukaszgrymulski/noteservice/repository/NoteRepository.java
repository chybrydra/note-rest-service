package pl.lukaszgrymulski.noteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, LocalDateTime> {

    @Query(value = "SELECT n FROM NoteEntity n WHERE n.is_deleted=0")
    List<NoteEntity> findAllRecent();
}
