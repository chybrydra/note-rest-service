package pl.lukaszgrymulski.noteservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "note")
public class NoteEntity implements Serializable {

    private static final long serialVersionUID = 1956917174759151411L;

    @NotNull
    private int id;

    @NotNull
    private int version;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private LocalDateTime modified;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private boolean is_deleted;
}
