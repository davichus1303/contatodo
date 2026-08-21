package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.Role;
import com.contatodo.domain.repositories.RoleRepository;
import com.contatodo.adapters.outbound.persistence.mapper.RolePersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.RoleDocument;
import com.contatodo.adapters.outbound.persistence.repository.RoleMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the role repository port.
 */
@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleMongoRepository roleMongoRepository;
    private final RolePersistenceMapper persistenceMapper;

    /**
     * Creates a role repository adapter.
     *
     * @param roleMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public RoleRepositoryAdapter(
            RoleMongoRepository roleMongoRepository,
            RolePersistenceMapper persistenceMapper
    ) {
        this.roleMongoRepository = roleMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role save(Role role) {
        RoleDocument document = persistenceMapper.toDocument(role);
        RoleDocument savedDocument = roleMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Role> findById(String id) {
        return roleMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Role> findAllActive() {
        return persistenceMapper.toEntityList(roleMongoRepository.findActiveRoles());
    }
}
