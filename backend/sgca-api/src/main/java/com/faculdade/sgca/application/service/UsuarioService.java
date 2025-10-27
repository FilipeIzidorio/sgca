package com.faculdade.sgca.application.service;

import com.faculdade.sgca.application.dto.UsuarioDTO;
import com.faculdade.sgca.application.mapper.UsuarioMapper;
import com.faculdade.sgca.domain.model.PerfilUsuario;
import com.faculdade.sgca.domain.model.Usuario;
import com.faculdade.sgca.infrastructure.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    // -------------------------------------------------
    // LISTAR TODOS
    // -------------------------------------------------
    public List<UsuarioDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    // -------------------------------------------------
    // BUSCAR POR ID
    // -------------------------------------------------
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        return mapper.toDTO(usuario);
    }

    // -------------------------------------------------
    // CRIAR NOVO USUÁRIO
    // -------------------------------------------------
    @Transactional
    public UsuarioDTO criar(String nome,
                            String email,
                            String senha,
                            PerfilUsuario perfil) {

        // e-mail único
        if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        // monta entidade
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil); // agora perfil é enum direto

        Usuario salvo = repository.save(usuario);

        return mapper.toDTO(salvo);
    }

    // -------------------------------------------------
    // ATUALIZAR (parcial)
    // -------------------------------------------------
    @Transactional
    public UsuarioDTO atualizar(Long id,
                                String nome,
                                String email,
                                String senha,
                                PerfilUsuario perfil) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado com ID: " + id));

        // nome
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }

        // email
        if (email != null && !email.isBlank() && !email.equalsIgnoreCase(usuario.getEmail())) {
            if (repository.existsByEmail(email)) {
                throw new IllegalArgumentException("E-mail já cadastrado: " + email);
            }
            usuario.setEmail(email);
        }

        // senha (regrava criptografada)
        if (senha != null && !senha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(senha));
        }

        // perfil (enum)
        if (perfil != null) {
            usuario.setPerfil(perfil);
        }

        Usuario atualizado = repository.save(usuario);

        return mapper.toDTO(atualizado);
    }

    // -------------------------------------------------
    // EXCLUIR
    // -------------------------------------------------
    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        repository.deleteById(id);
    }
}
