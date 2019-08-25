package pl.lukaszgrymulski.noteservice.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.service.NoteService;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteRetrieveDTO>> getRecentNotesVersions() {
        List<NoteRetrieveDTO> allRecentVersionNotes = noteService.getAllRecentVersionNotes();
        HttpStatus status = allRecentVersionNotes.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return ResponseEntity
                .status(status)
                .body(allRecentVersionNotes);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> getNote(@PathVariable("id") int id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.findById(id));
    }

    @GetMapping(value = "/{id}/history",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteRetrieveDTO>> getNoteHistory(@PathVariable("id") int id) {
        List<NoteRetrieveDTO> byIdFullHistory = noteService.findByIdFullHistory(id);
        HttpStatus status = byIdFullHistory.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK;
        return ResponseEntity
                .status(status)
                .body(byIdFullHistory);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> createNote(@Valid @RequestBody NotePersistDTO notePersistDTO,
                                                          BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        NoteRetrieveDTO noteRetrieveDTO = noteService.save(notePersistDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteRetrieveDTO);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> deleteNote(@PathVariable int id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.deleteNote(id));
    }
}
