package pl.lukaszgrymulski.noteservice.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j(topic = "application.logger")
public class NoteController {

    private final NoteService noteService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteRetrieveDTO>> getRecentNotesVersions() throws NotFoundException {
        log.debug("GET method at '/notes'");
        List<NoteRetrieveDTO> allRecentVersionNotes = noteService.getAllRecentVersionNotes();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allRecentVersionNotes);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> getNote(@PathVariable("id") int id) throws NotFoundException {
        log.debug("GET method at '/notes/{}'", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.findById(id));
    }

    @GetMapping(value = "/{id}/history",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteRetrieveDTO>> getNoteHistory(@PathVariable("id") int id) throws NotFoundException {
        log.debug("GET method at '/notes/{}/history'", id);
        List<NoteRetrieveDTO> byIdFullHistory = noteService.findByIdFullHistory(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(byIdFullHistory);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> createNote(@Valid @RequestBody NotePersistDTO notePersistDTO,
                                                          BindingResult bindingResult) throws BindException {
        log.debug("POST method at '/notes' with data: {}",notePersistDTO);
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        NoteRetrieveDTO noteRetrieveDTO = noteService.save(notePersistDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteRetrieveDTO);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> deleteNote(@PathVariable int id) throws NotFoundException {
        log.debug("DELETE method at '/notes/{}'", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.deleteNote(id));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteRetrieveDTO> updateNote(@Valid @RequestBody NotePersistDTO notePersistDTO,
                                                      @PathVariable int id,
                                                      BindingResult bindingResult) throws NotFoundException, BindException {
        log.debug("PUT method at '/notes/{id}' with data: {}", id, notePersistDTO);
        if (bindingResult.hasErrors()) throw new BindException(bindingResult);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteService.updateNote(notePersistDTO, id));
    }
}
