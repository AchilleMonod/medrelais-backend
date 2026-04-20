package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.RoleEnum;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurBO {
    private Long id;
    private String email;
    private String password;
    private RoleEnum role;
}
