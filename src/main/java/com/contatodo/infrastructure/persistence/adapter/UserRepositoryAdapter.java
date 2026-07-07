package com.contatodo.infrastructure.persistence.adapter;

import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.infrastructure.mapper.PersistenceMapper;
import com.contatodo.infrastructure.persistence.document.UserDocument;
import com.contatodo.infrastructure.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the user repository port.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository userMongoRepository;
    private final PersistenceMapper persistenceMapper;

    /**
     * Creates a user repository adapter.
     *
     * @param userMongoRepository Mongo repository.
     * @param persistenceMapper Persistence mapper.
     */
    public UserRepositoryAdapter(UserMongoRepository userMongoRepository, PersistenceMapper persistenceMapper) {
        this.userMongoRepository = userMongoRepository;
        this.persistenceMapper = persistenceMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User save(User user) {
        UserDocument document = persistenceMapper.toUserDocument(user);
        UserDocument savedDocument = userMongoRepository.save(document);
        return persistenceMapper.toUserEntity(savedDocument);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findById(String id) {
        return userMongoRepository.findById(id).map(persistenceMapper::toUserEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email).map(persistenceMapper::toUserEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAllActive() {
        return persistenceMapper.toUserEntityList(userMongoRepository.findByIsDelete(false));
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
                .map(persistenceMapper::toUserEntity);
    }
}
