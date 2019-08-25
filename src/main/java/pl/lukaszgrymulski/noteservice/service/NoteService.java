package pl.lukaszgrymulski.noteservice.service;

import javassist.NotFoundException;
import lombok.Data;
import org.springframework.stereotype.Service;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;
import pl.lukaszgrymulski.noteservice.repository.NoteRepository;
import pl.lukaszgrymulski.noteservice.utils.NoteMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class NoteService {

    private final NoteRepository repository;
    private final NoteMapper noteMapper;

    public List<NoteRetrieveDTO> getAllRecentVersionNotes() {
        return repository.findAllRecentNoteVersions().stream()
                .map(entity -> noteMapper.mapNoteEntityToNoteRetrieveDTO(entity))
                .collect(Collectors.toList());
    }

    public NoteRetrieveDTO findById(int id) throws NotFoundException {
        Optional<NoteEntity> recentNoteVersionById = repository.findRecentNoteVersionById(id);
        if (recentNoteVersionById.isPresent()) {
            return noteMapper.mapNoteEntityToNoteRetrieveDTO(recentNoteVersionById.get());
        }
        throw new NotFoundException("Note with id=" + id + " was not found");
    }

    public List<NoteRetrieveDTO> findByIdFullHistory(int id) {
        return repository.findAllById(id).stream()
                .map(entity -> noteMapper.mapNoteEntityToNoteRetrieveDTO(entity))
                .collect(Collectors.toList());
    }

    public NoteRetrieveDTO save(NotePersistDTO notePersistDTO) {
        NoteEntity noteEntity = noteMapper.mapNotePersistDTOToNoteEntity(notePersistDTO);
        LocalDateTime now = LocalDateTime.now();
        noteEntity.set_deleted(false);
        noteEntity.setCreated(now);
        noteEntity.setModified(now);
        noteEntity.setId(repository.findMaxId()+1);
        noteEntity.setVersion(1);
        NoteEntity savedEntity = repository.save(noteEntity);
        return noteMapper.mapNoteEntityToNoteRetrieveDTO(savedEntity);
    }

    public NoteRetrieveDTO deleteNote(int id) throws NotFoundException {
        Optional<NoteEntity> recentNoteVersionById = repository.findRecentNoteVersionById(id);
        if (recentNoteVersionById.isPresent()) {
            NoteEntity recentNoteEntity = recentNoteVersionById.get();
            recentNoteEntity.set_deleted(true);

            NoteEntity deletedNoteEntity = new NoteEntity();
            deletedNoteEntity.setId(id);
            deletedNoteEntity.setVersion(recentNoteEntity.getVersion()+1);
            deletedNoteEntity.setModified(LocalDateTime.now());
            deletedNoteEntity.setCreated(recentNoteEntity.getCreated());
            deletedNoteEntity.setTitle("");
            deletedNoteEntity.setContent("");
            deletedNoteEntity.set_deleted(true);

            repository.save(recentNoteEntity);
            return noteMapper.mapNoteEntityToNoteRetrieveDTO(repository.save(deletedNoteEntity));

        }
        throw new NotFoundException("Note to delete was not found");
    }
}