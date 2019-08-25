package pl.lukaszgrymulski.noteservice.dto;

import lombok.Data;

@Data
public class NotePersistDTO {

    private final int id;
    private final String title;
    private final String content;
}
