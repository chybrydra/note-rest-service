package pl.lukaszgrymulski.noteservice.service;

import javassist.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;
import pl.lukaszgrymulski.noteservice.repository.NoteRepository;
import pl.lukaszgrymulski.noteservice.utils.NoteMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NoteServiceTest {

    @Mock
    NoteRepository noteRepository;

    @Mock
    NoteMapper noteMapper;

    @InjectMocks
    NoteService noteService;

    private static final long ID = 1;

    @Test
    public void getAllRecentVersionNotesShouldBeInvoked() throws NotFoundException {
        ArrayList noteList = mock(ArrayList.class);
        when(noteRepository.findAllRecentNoteVersions()).thenReturn(noteList);
        noteService.getAllRecentVersionNotes();
        verify(noteRepository).findAllRecentNoteVersions();
    }

    @Test(expected = NotFoundException.class)
    public void getAllRecentVersionNotesShouldThrowNotFoundException() throws NotFoundException {
        when(noteRepository.findAllRecentNoteVersions()).thenReturn(new ArrayList<>());
        noteService.getAllRecentVersionNotes();
    }

    @Test
    public void findRecentNoteVersionByIdShouldBeInvoked() throws NotFoundException {
        NoteEntity noteEntity = mock(NoteEntity.class);
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.of(noteEntity));
        noteService.findById(ID);
        verify(noteRepository).findRecentNoteVersionById(ID);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdShouldThrowNotFoundException() throws NotFoundException {
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.empty());
        noteService.findById(ID);
    }

    @Test
    public void findAllByIdShouldBeInvoked() throws NotFoundException {
        ArrayList noteList = mock(ArrayList.class);
        when(noteRepository.findAllById(ID)).thenReturn(noteList);
        noteService.findByIdFullHistory(ID);
        verify(noteRepository).findAllById(ID);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdFullHistoryShouldThrowNotFoundException() throws NotFoundException {
        when(noteRepository.findAllById(ID)).thenReturn(new ArrayList<>());
        noteService.findByIdFullHistory(ID);
    }

    @Test
    public void saveWithNoteEntityAsArgumentShouldBeInvoked() {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        NoteEntity noteEntity = mock(NoteEntity.class);
        when(noteMapper.mapNotePersistDTOToNoteEntity(notePersistDTO)).thenReturn(noteEntity);
        noteService.save(notePersistDTO);
        verify(noteRepository).save(noteEntity);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNoteShouldThrowNotFoundExceptionIfNoNoteForIdExists() throws NotFoundException {
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.empty());
        noteService.deleteNote(ID);
    }

    @Test
    public void deleteNoteShouldInvokeSaveEntityMethod() throws NotFoundException {
        NoteEntity noteEntity = mock(NoteEntity.class);
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.of(noteEntity));
        noteService.deleteNote(ID);
        verify(noteRepository).save(noteEntity);
    }

    @Test(expected = NotFoundException.class)
    public void updateNoteShouldThrowNotFoundExceptionIfNoNoteForIdExists() throws NotFoundException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.empty());
        noteService.updateNote(notePersistDTO, ID);
    }

    @Test
    public void updateNoteShouldInvokeSaveEntityMethod() throws NotFoundException {
        NotePersistDTO notePersistDTO = mock(NotePersistDTO.class);
        NoteEntity noteEntity = mock(NoteEntity.class);
        when(noteRepository.findRecentNoteVersionById(ID)).thenReturn(Optional.of(noteEntity));
        noteService.updateNote(notePersistDTO, ID);
        verify(noteRepository).save(noteEntity);
    }
}