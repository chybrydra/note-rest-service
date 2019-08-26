package pl.lukaszgrymulski.noteservice.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j(topic = "application.logger")
public class NoteService {

    private final NoteRepository repository;
    private final NoteMapper noteMapper;

    public List<NoteRetrieveDTO> getAllRecentVersionNotes() throws NotFoundException {
        log.debug("Retrieving all recent notes...");
        List<NoteEntity> allRecentNoteVersions = repository.findAllRecentNoteVersions();
        if (allRecentNoteVersions.isEmpty()) throw new NotFoundException("No notes were found");
        return allRecentNoteVersions.stream()
                .map(noteMapper::mapNoteEntityToNoteRetrieveDTO)
                .collect(Collectors.toList());
    }

    public NoteRetrieveDTO findById(Long id) throws NotFoundException {
        log.debug("Retrieving note with id={}", id);
        Optional<NoteEntity> recentNoteVersionById = repository.findRecentNoteVersionById(id);
        if (recentNoteVersionById.isPresent()) {
            return noteMapper.mapNoteEntityToNoteRetrieveDTO(recentNoteVersionById.get());
        }
        throw new NotFoundException("Note with id=" + id + " was not found");
    }

    public List<NoteRetrieveDTO> findByIdFullHistory(Long id) throws NotFoundException {
        log.debug("Retrieving history for note with id={}", id);
        List<NoteEntity> allById = repository.findAllById(id);
        if (allById.isEmpty()) throw new NotFoundException("No notes were found for id=" + id);
        return allById.stream()
                .map(noteMapper::mapNoteEntityToNoteRetrieveDTO)
                .collect(Collectors.toList());
    }

    public NoteRetrieveDTO save(NotePersistDTO notePersistDTO) {
        log.debug("Saving new note: {}", notePersistDTO);
        NoteEntity noteEntity = prepareNewNoteEntity(notePersistDTO);
        NoteEntity savedEntity = repository.save(noteEntity);
        return noteMapper.mapNoteEntityToNoteRetrieveDTO(savedEntity);
    }

    public NoteEntity prepareNewNoteEntity(NotePersistDTO notePersistDTO) {
        NoteEntity noteEntity = noteMapper.mapNotePersistDTOToNoteEntity(notePersistDTO);
        LocalDateTime now = LocalDateTime.now();
        noteEntity.set_deleted(false);
        noteEntity.setCreated(now);
        noteEntity.setModified(now);
        Long maxId = repository.findMaxId();
        long prevId = maxId == null ? 0l : maxId;
        noteEntity.setId(prevId+1l);
        noteEntity.setVersion(1l);
        return noteEntity;
    }

    public NoteRetrieveDTO deleteNote(Long id) throws NotFoundException {
        log.debug("Deleting note with id={}", id);
        Optional<NoteEntity> recentNoteVersionById = repository.findRecentNoteVersionById(id);
        if (recentNoteVersionById.isPresent()) {
            NoteEntity recentNoteEntity = recentNoteVersionById.get();
            NoteEntity deletedNoteEntity = prepareDeletedNoteInstance(recentNoteEntity);
            markRecentNoteVersionAsDeleted(recentNoteEntity);
            return noteMapper.mapNoteEntityToNoteRetrieveDTO(repository.save(deletedNoteEntity));
        }
        throw new NotFoundException("Note to delete was not found");
    }

    private void markRecentNoteVersionAsDeleted(NoteEntity recentNoteEntity) {
        recentNoteEntity.set_deleted(true);
        repository.save(recentNoteEntity);
    }

    private NoteEntity prepareDeletedNoteInstance(NoteEntity recentNoteEntity){
        NoteEntity deletedNoteEntity = new NoteEntity();
        deletedNoteEntity.setId(recentNoteEntity.getId());
        deletedNoteEntity.setVersion(recentNoteEntity.getVersion()+1);
        deletedNoteEntity.setModified(LocalDateTime.now());
        deletedNoteEntity.setCreated(recentNoteEntity.getCreated());
        deletedNoteEntity.setTitle("");
        deletedNoteEntity.setContent("");
        deletedNoteEntity.set_deleted(true);
        return deletedNoteEntity;
    }

    public NoteRetrieveDTO updateNote(NotePersistDTO notePersistDTO, Long id) throws NotFoundException {
        log.debug("Editing note with id={}", id);
        Optional<NoteEntity> recentNoteVersionById = repository.findRecentNoteVersionById(id);
        if (recentNoteVersionById.isPresent()) {
            NoteEntity recentNoteEntity = recentNoteVersionById.get();
            markRecentNoteVersionAsDeleted(recentNoteEntity);
            NoteEntity newNoteVersion = prepareEditedNoteEntity(notePersistDTO, recentNoteEntity);
            return noteMapper.mapNoteEntityToNoteRetrieveDTO(repository.save(newNoteVersion));
        }
        throw new NotFoundException("Note to edit was not found");
    }

    private NoteEntity prepareEditedNoteEntity(NotePersistDTO notePersistDTO, NoteEntity recentNoteEntity) {
        NoteEntity newNoteVersion = new NoteEntity();
        newNoteVersion.setId(recentNoteEntity.getId());
        newNoteVersion.setVersion(recentNoteEntity.getVersion()+1);
        newNoteVersion.setCreated(recentNoteEntity.getCreated());
        newNoteVersion.setModified(LocalDateTime.now());
        newNoteVersion.setTitle(notePersistDTO.getTitle());
        newNoteVersion.setContent(notePersistDTO.getContent());
        newNoteVersion.set_deleted(false);
        return newNoteVersion;
    }
}