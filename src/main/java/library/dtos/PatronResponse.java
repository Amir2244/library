package library.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatronResponse {
    private Long id;
    private String name;
    private String contactInfo;
    private String email;
}