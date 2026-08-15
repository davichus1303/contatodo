package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.RoleDocument;
import com.contatodo.infrastructure.persistence.repository.RoleMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the role repository port.
 */
@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleMongoRepository roleMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates a role repository adapter.
     *
     * @param roleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public RoleRepositoryAdapter(
            RoleMongoRepository roleMongoRepository,
            PersistenceMapper persistenceMapper
    ) {
        this.roleMongoRepository = roleMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role save(Role role) {
        RoleDocument document = persistenceMapper.toRoleDocument(role);
        RoleDocument savedDocument = roleMongoRepository.save(document);
        return persistenceMapper.toRoleEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Role> findById(String id) {
        return roleMongoRepository.findById(id).map(persistenceMapper::toRoleEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Role> findAllActive() {
        return persistenceMapper.toRoleEntityList(roleMongoRepository.findActiveRoles());
    }
}
