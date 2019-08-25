package pl.lukaszgrymulski.noteservice.service;

import javassist.NotFoundException;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;
import pl.lukaszgrymulski.noteservice.repository.NoteRepository;
import pl.lukaszgrymulski.noteservice.utils.NoteMapper;

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
}