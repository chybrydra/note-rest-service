package pl.lukaszgrymulski.noteservice.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.repository.NoteRepository;
import pl.lukaszgrymulski.noteservice.utils.NoteMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class NoteService {

    private final NoteRepository repository;
    private final NoteMapper noteMapper;

    public List<NoteRetrieveDTO> getAllRecentVersionNotes() {
        return repository.findAllRecent().stream()
                .map(entity -> noteMapper.mapNoteEntityToNoteRetrieveDTO(entity))
                .collect(Collectors.toList());
    }
}
