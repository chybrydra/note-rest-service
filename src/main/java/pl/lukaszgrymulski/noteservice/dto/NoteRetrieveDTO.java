package pl.lukaszgrymulski.noteservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NoteRetrieveDTO {

    private Long id;
    private Long version;
    private LocalDateTime modified;
    private LocalDateTime created;
    private String title;
    private String content;
}
