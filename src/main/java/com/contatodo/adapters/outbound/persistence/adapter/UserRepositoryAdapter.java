package com.contatodo.adapters.outbound.persistence.adapter;

import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.adapters.outbound.persistence.mapper.UserPersistenceMapper;
import com.contatodo.adapters.outbound.persistence.document.UserDocument;
import com.contatodo.adapters.outbound.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the user repository port.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository userMongoRepository;
    private final UserPersistenceMapper persistenceMapper;

    /**
     * Creates a user repository adapter.
     *
     * @param userMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public UserRepositoryAdapter(UserMongoRepository userMongoRepository, UserPersistenceMapper persistenceMapper) {
        this.userMongoRepository = userMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        UserDocument document = persistenceMapper.toDocument(user);
        UserDocument savedDocument = userMongoRepository.save(document);
        return persistenceMapper.toEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(String id) {
        return userMongoRepository.findById(id).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email).map(persistenceMapper::toEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAllActive() {
        return persistenceMapper.toEntityList(userMongoRepository.findByIsDelete(false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByEmail(String email) {
        return userMongoRepository.existsByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findActiveUserByEmail(String email, boolean isDelete) {
        return userMongoRepository.findByEmailAndIsDelete(email, isDelete)
                .map(persistenceMapper::toEntity);
    }
}
