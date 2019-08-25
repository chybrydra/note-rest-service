package pl.lukaszgrymulski.noteservice.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;
import pl.lukaszgrymulski.noteservice.service.NoteService;

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

    @GetMapping("/{id}")
    public ResponseEntity<NoteRetrieveDTO> getNote(@PathVariable("id") int id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noteService.findById(id));
    }

}
