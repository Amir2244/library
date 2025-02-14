package library.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact information is mandatory")
    @Column(nullable = false)
    private String contactInfo;

    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;


    public Patron() {}
}