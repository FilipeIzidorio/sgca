package stub;

import com.faculdade.sgca.application.dto.UsuarioDTO;
import com.faculdade.sgca.domain.model.PerfilUsuario;
import com.faculdade.sgca.domain.model.Usuario;

public class UsuarioStub {

    public static UsuarioDTO dto() {
        UsuarioDTO dto = new UsuarioDTO();

        dto.setNome("Admin");
        dto.setEmail("admin@email.com");
        dto.setPerfil(PerfilUsuario.ADMIN);
        return dto;
    }

    public static Usuario entity() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNome("Admin");
        u.setEmail("admin@email.com");
        u.setPerfil(PerfilUsuario.ADMIN);
        return u;
    }

    public static Usuario novo() {
        Usuario u = new Usuario();
        u.setNome("Novo Usuário");
        u.setEmail("novo@email.com");
        u.setPerfil(PerfilUsuario.ADMIN);
        return u;
    }
}
