package com.contatodo.infrastructure.mapper;

import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.User;
import com.contatodo.infrastructure.persistence.document.ProductDocument;
import com.contatodo.infrastructure.persistence.document.UserDocument;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between domain entities and persistence documents.
 */
@Component
public class PersistenceMapper {

    /**
     * Maps a user entity to a user document.
     *
     * @param user User entity.
     * @return User document.
     */
    public UserDocument toUserDocument(User user) {
        UserDocument document = new UserDocument();
        document.setId(user.getId());
        document.setUserName(user.getUserName());
        document.setEmail(user.getEmail());
        document.setPassword(user.getPassword());
        document.setName(user.getName());
        document.setActive(user.isActive());
        document.setDelete(user.isDelete());
        document.setCreatedDate(user.getCreatedDate());
        document.setUpdatedDate(user.getUpdatedDate());
        document.setCreatedBy(user.getCreatedBy());
        document.setUpdatedBy(user.getUpdatedBy());
        return document;
    }

    /**
     * Maps a user document to a user entity.
     *
     * @param document User document.
     * @return User entity.
     */
    public User toUserEntity(UserDocument document) {
        User user = new User();
        user.setId(document.getId());
        user.setUserName(document.getUserName());
        user.setEmail(document.getEmail());
        user.setPassword(document.getPassword());
        user.setName(document.getName());
        user.setActive(document.isActive());
        user.setDelete(document.isDelete());
        user.setCreatedDate(document.getCreatedDate());
        user.setUpdatedDate(document.getUpdatedDate());
        user.setCreatedBy(document.getCreatedBy());
        user.setUpdatedBy(document.getUpdatedBy());
        return user;
    }

    /**
     * Maps a list of user documents to user entities.
     *
     * @param documents User documents.
     * @return User entities.
     */
    public List<User> toUserEntityList(List<UserDocument> documents) {
        return documents.stream().map(this::toUserEntity).toList();
    }

    /**
     * Maps a product entity to a product document.
     *
     * @param product Product entity.
     * @return Product document.
     */
    public ProductDocument toProductDocument(Product product) {
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setStock(product.getStock());
        document.setCode(product.getCode());
        document.setRealCost(product.getRealCost());
        document.setUrlPhoto(product.getUrlPhoto());
        document.setPublicCost(product.getPublicCost());
        document.setCreatedDate(product.getCreatedDate());
        document.setUpdatedDate(product.getUpdatedDate());
        return document;
    }

    /**
     * Maps a product document to a product entity.
     *
     * @param document Product document.
     * @return Product entity.
     */
    public Product toProductEntity(ProductDocument document) {
        Product product = new Product();
        product.setId(document.getId());
        product.setName(document.getName());
        product.setDescription(document.getDescription());
        product.setStock(document.getStock());
        product.setCode(document.getCode());
        product.setRealCost(document.getRealCost());
        product.setUrlPhoto(document.getUrlPhoto());
        product.setPublicCost(document.getPublicCost());
        product.setCreatedDate(document.getCreatedDate());
        product.setUpdatedDate(document.getUpdatedDate());
        return product;
    }

    /**
     * Maps a list of product documents to product entities.
     *
     * @param documents Product documents.
     * @return Product entities.
     */
    public List<Product> toProductEntityList(List<ProductDocument> documents) {
        return documents.stream().map(this::toProductEntity).toList();
    }
}
