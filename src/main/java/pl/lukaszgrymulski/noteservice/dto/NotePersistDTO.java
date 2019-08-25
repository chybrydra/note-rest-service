package pl.lukaszgrymulski.noteservice.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class NotePersistDTO {

    private final int id;

    @NotNull
    @Size(max=45)
    private final String title;

    @NotNull
    @Size(max=255)
    private final String content;
}
