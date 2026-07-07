package com.contatodo.infrastructure.mapper;

import com.contatodo.domain.entities.Module;
import com.contatodo.domain.entities.ModulePermission;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.infrastructure.persistence.document.ModuleDocument;
import com.contatodo.infrastructure.persistence.document.ModulePermissionDocument;
import com.contatodo.infrastructure.persistence.document.ProductDocument;
import com.contatodo.infrastructure.persistence.document.SaleDocument;
import com.contatodo.infrastructure.persistence.document.UserDocument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        document.setUnitRealCost(product.getUnitRealCost());
        document.setUnitPublicCost(product.getUnitPublicCost());
        document.setUrlPhoto(product.getUrlPhoto());
        document.setIsActive(product.getIsActive());
        document.setUserOid(product.getUserOid());
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
        product.setUnitRealCost(document.getUnitRealCost());
        product.setUnitPublicCost(document.getUnitPublicCost());
        product.setUrlPhoto(document.getUrlPhoto());
        product.setIsActive(document.getIsActive());
        product.setUserOid(document.getUserOid());
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

    /**
     * Maps a sale entity to a sale document.
     *
     * @param sale Sale entity.
     * @return Sale document.
     */
    public SaleDocument toSaleDocument(Sale sale) {
        SaleDocument document = new SaleDocument();
        document.setId(sale.getId());
        document.setSaleNumber(sale.getSaleNumber());
        document.setProductOid(sale.getProductOid());
        document.setProductName(sale.getProductName());
        document.setUserOid(sale.getUserOid());
        document.setQuantity(sale.getQuantity());
        document.setTotalCost(sale.getTotalCost());
        document.setOriginalTotalPrice(sale.getOriginalTotalPrice());
        document.setTotalSalePrice(sale.getTotalSalePrice());
        document.setSaleDate(sale.getSaleDate());
        document.setNotes(sale.getNotes());
        document.setCreatedDate(sale.getCreatedDate());
        document.setUpdatedDate(sale.getUpdatedDate());
        document.setIsDeleted(sale.getIsDeleted());
        return document;
    }

    /**
     * Maps a sale document to a sale entity.
     *
     * @param document Sale document.
     * @return Sale entity.
     */
    public Sale toSaleEntity(SaleDocument document) {
        Sale sale = new Sale();
        sale.setId(document.getId());
        sale.setSaleNumber(document.getSaleNumber());
        sale.setProductOid(document.getProductOid());
        sale.setProductName(document.getProductName());
        sale.setUserOid(document.getUserOid());
        sale.setQuantity(document.getQuantity());
        sale.setTotalCost(document.getTotalCost());
        sale.setOriginalTotalPrice(document.getOriginalTotalPrice());
        sale.setTotalSalePrice(document.getTotalSalePrice());
        sale.setSaleDate(document.getSaleDate());
        sale.setNotes(document.getNotes());
        sale.setCreatedDate(document.getCreatedDate());
        sale.setUpdatedDate(document.getUpdatedDate());
        sale.setIsDeleted(document.getIsDeleted());
        return sale;
    }

    /**
     * Maps a list of sale documents to sale entities.
     *
     * @param documents Sale documents.
     * @return Sale entities.
     */
    public List<Sale> toSaleEntityList(List<SaleDocument> documents) {
        return documents.stream().map(this::toSaleEntity).toList();
    }

    /**
     * Maps a module entity to a module document.
     *
     * @param module Module entity.
     * @return Module document.
     */
    public ModuleDocument toModuleDocument(Module module) {
        ModuleDocument document = new ModuleDocument();
        document.setId(module.getId());
        document.setName(module.getName());
        document.setLink(module.getLink());
        document.setIsActive(module.getIsActive());
        document.setIsDelete(module.getIsDeleted());
        
        List<ModulePermissionDocument> permissionDocuments = module.getPermissions().stream()
                .map(this::toModulePermissionDocument)
                .collect(Collectors.toList());
        document.setPermissions(permissionDocuments);
        
        document.setCreatedDate(module.getCreatedDate());
        document.setUpdatedDate(module.getUpdatedDate());
        return document;
    }

    /**
     * Maps a module document to a module entity.
     *
     * @param document Module document.
     * @return Module entity.
     */
    public Module toModuleEntity(ModuleDocument document) {
        Module module = new Module();
        module.setId(document.getId());
        module.setName(document.getName());
        module.setLink(document.getLink());
        module.setIsActive(document.getIsActive());
        module.setIsDeleted(document.getIsDelete());
        
        List<ModulePermission> permissions = document.getPermissions().stream()
                .map(this::toModulePermission)
                .collect(Collectors.toList());
        module.setPermissions(permissions);
        
        module.setCreatedDate(document.getCreatedDate());
        module.setUpdatedDate(document.getUpdatedDate());
        return module;
    }

    /**
     * Maps a list of module documents to module entities.
     *
     * @param documents Module documents.
     * @return Module entities.
     */
    public List<Module> toModuleEntityList(List<ModuleDocument> documents) {
        return documents.stream().map(this::toModuleEntity).toList();
    }

    /**
     * Maps a module permission entity to a module permission document.
     *
     * @param permission Module permission entity.
     * @return Module permission document.
     */
    private ModulePermissionDocument toModulePermissionDocument(ModulePermission permission) {
        ModulePermissionDocument document = new ModulePermissionDocument();
        document.setUserOid(permission.getUserOid());
        document.setCreate(permission.getCreate());
        document.setUpdate(permission.getUpdate());
        document.setEdit(permission.getEdit());
        return document;
    }

    /**
     * Maps a module permission document to a module permission entity.
     *
     * @param document Module permission document.
     * @return Module permission entity.
     */
    private ModulePermission toModulePermission(ModulePermissionDocument document) {
        ModulePermission permission = new ModulePermission();
        permission.setUserOid(document.getUserOid());
        permission.setCreate(document.getCreate());
        permission.setUpdate(document.getUpdate());
        permission.setEdit(document.getEdit());
        return permission;
    }
}
