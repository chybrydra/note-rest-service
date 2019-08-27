package pl.lukaszgrymulski.noteservice.controller;

import javassist.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.service.NoteService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NoteControllerTest {

    @Mock
    NoteService noteService;

    @InjectMocks
    NoteController noteController;

    private static final long ID = 1;

    @Test
    public void getNoteShouldReturnOkStatus() throws NotFoundException {
        assertEquals(HttpStatus.OK,
                noteController.getNote(ID).getStatusCode());
    }

    @Test
    public void getNoteHistoryShouldReturnOkStatus() throws NotFoundException {
        assertEquals(HttpStatus.OK,
                noteController.getNoteHistory(ID).getStatusCode());
    }

    @Test
    public void getRecentNotesVersionsShouldReturnOkStatus() throws NotFoundException {
        assertEquals(HttpStatus.OK,
                noteController.getRecentNotesVersions().getStatusCode());
    }

    @Test
    public void deleteNoteShouldReturnOkStatusIfCanBeDeleted() throws NotFoundException {
        when(noteService.deleteNote(ID)).thenReturn(mock(NoteRetrieveDTO.class));
        assertEquals(HttpStatus.OK, noteController.deleteNote(ID).getStatusCode());
    }

    @Test
    public void updateNoteShouldReturnCreatedStatusIfNoteToUpdateExists() throws NotFoundException, BindException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        NoteRetrieveDTO noteRetrieveDTO = mock(NoteRetrieveDTO.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(noteService.updateNote(notePersistDTO, ID)).thenReturn(noteRetrieveDTO);
        assertEquals(HttpStatus.CREATED, noteController.updateNote(notePersistDTO, ID, bindingResult).getStatusCode());
    }

    @Test(expected = BindException.class)
    public void updateNoteShouldThrowBindException() throws NotFoundException, BindException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        noteController.updateNote(notePersistDTO, ID, bindingResult);
    }

    @Test
    public void addNoteShouldReturnCreatedStatusIfNoteWasCreated() throws BindException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        assertEquals(HttpStatus.CREATED, noteController.createNote(notePersistDTO, bindingResult).getStatusCode());
    }

    @Test(expected = BindException.class)
    public void addNoteShouldThrowBindException() throws BindException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        noteController.createNote(notePersistDTO, bindingResult);
    }

}