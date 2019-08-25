package pl.lukaszgrymulski.noteservice.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import pl.lukaszgrymulski.noteservice.dto.NotePersistDTO;
import pl.lukaszgrymulski.noteservice.dto.NoteRetrieveDTO;
import pl.lukaszgrymulski.noteservice.entity.NoteEntity;

@Component
@Getter
@RequiredArgsConstructor
public class NoteMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public NoteEntity mapNoteRetrieveDTOToNoteEntity(NoteRetrieveDTO noteRetrieveDTO) {
        return modelMapper.map(noteRetrieveDTO, NoteEntity.class);
    }

    public NoteRetrieveDTO mapNoteEntityToNoteRetrieveDTO(NoteEntity noteEntity) {
        return modelMapper.map(noteEntity, NoteRetrieveDTO.class);
    }

    public NoteEntity mapNotePersistDTOToNoteEntity(NotePersistDTO noteRetrieveDTO) {
        return modelMapper.map(noteRetrieveDTO, NoteEntity.class);
    }

    public NotePersistDTO mapNoteEntityToNotePersistDTO(NoteEntity noteEntity) {
        return modelMapper.map(noteEntity, NotePersistDTO.class);
    }

}
