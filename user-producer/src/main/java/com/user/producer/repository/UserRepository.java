package com.user.producer.repository;

import com.ead.authuser.models.UserModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID>, JpaSpecificationExecutor<UserModel> {

    boolean existsByUserName(String username);

    boolean existsByEmail(String email);

    //A anotação @EntityGraph é usada para definir como as entidades relacionadas devem ser carregadas na consulta
    // Indica que a propriedade roles da entidade UserModel deve ser carregada junto com a consulta.
    //Força o uso de JOIN FETCH, o que significa que a consulta buscará os dados da entidade UserModel
    //e seus roles em uma única query.
    @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.FETCH)
                Optional<UserModel> findByUserName(String userName);

    @EntityGraph(attributePaths = "roles", type = EntityGraph.EntityGraphType.FETCH)
    Optional<UserModel> findById(UUID userId);
}