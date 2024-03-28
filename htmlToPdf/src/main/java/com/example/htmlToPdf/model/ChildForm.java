package com.example.htmlToPdf.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChildForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String childName;
    private LocalDate dateOfBirth;
    private String homeAddress;
    private boolean includeInfo2Email;
    @Enumerated(EnumType.STRING)
    private Gender gender;
}

