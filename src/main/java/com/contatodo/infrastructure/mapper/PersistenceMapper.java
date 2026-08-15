package com.contatodo.infrastructure.mapper;

import com.contatodo.domain.entities.Acquisition;
import com.contatodo.domain.entities.AcquisitionType;
import com.contatodo.domain.entities.Expense;
import com.contatodo.domain.entities.Module;
import com.contatodo.domain.entities.ModulePermission;
import com.contatodo.domain.entities.Product;
import com.contatodo.domain.entities.ProductCostHistory;
import com.contatodo.domain.entities.Role;
import com.contatodo.domain.entities.RolePermission;
import com.contatodo.domain.entities.Sale;
import com.contatodo.domain.entities.User;
import com.contatodo.infrastructure.persistence.document.AcquisitionDocument;
import com.contatodo.infrastructure.persistence.document.AcquisitionTypeDocument;
import com.contatodo.infrastructure.persistence.document.ExpenseDocument;
import com.contatodo.infrastructure.persistence.document.ModuleDocument;
import com.contatodo.infrastructure.persistence.document.ModulePermissionDocument;
import com.contatodo.infrastructure.persistence.document.ProductCostHistoryDocument;
import com.contatodo.infrastructure.persistence.document.ProductDocument;
import com.contatodo.infrastructure.persistence.document.RoleDocument;
import com.contatodo.infrastructure.persistence.document.RolePermissionDocument;
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
     * Maps an acquisition type entity to an acquisition type document.
     *
     * @param acquisitionType Acquisition type entity.
     * @return Acquisition type document.
     */
    public AcquisitionTypeDocument toAcquisitionTypeDocument(AcquisitionType acquisitionType) {
        AcquisitionTypeDocument document = new AcquisitionTypeDocument();
        document.setId(acquisitionType.getId());
        document.setName(acquisitionType.getName());
        document.setDescription(acquisitionType.getDescription());
        document.setUserOid(acquisitionType.getUserOid());
        document.setIsActive(acquisitionType.getIsActive());
        document.setIsDeleted(acquisitionType.getIsDeleted());
        document.setAffectsInventory(acquisitionType.getAffectsInventory());
        document.setCreatedDate(acquisitionType.getCreatedDate());
        document.setUpdatedDate(acquisitionType.getUpdatedDate());
        return document;
    }

    /**
     * Maps an acquisition type document to an acquisition type entity.
     *
     * @param document Acquisition type document.
     * @return Acquisition type entity.
     */
    public AcquisitionType toAcquisitionTypeEntity(AcquisitionTypeDocument document) {
        AcquisitionType acquisitionType = new AcquisitionType();
        acquisitionType.setId(document.getId());
        acquisitionType.setName(document.getName());
        acquisitionType.setDescription(document.getDescription());
        acquisitionType.setUserOid(document.getUserOid());
        acquisitionType.setIsActive(document.getIsActive());
        acquisitionType.setIsDeleted(document.getIsDeleted());
        acquisitionType.setAffectsInventory(document.getAffectsInventory());
        acquisitionType.setCreatedDate(document.getCreatedDate());
        acquisitionType.setUpdatedDate(document.getUpdatedDate());
        return acquisitionType;
    }

    /**
     * Maps a list of acquisition type documents to acquisition type entities.
     *
     * @param documents Acquisition type documents.
     * @return Acquisition type entities.
     */
    public List<AcquisitionType> toAcquisitionTypeEntityList(List<AcquisitionTypeDocument> documents) {
        return documents.stream().map(this::toAcquisitionTypeEntity).toList();
    }

    /**
     * Maps an acquisition entity to an acquisition document.
     *
     * @param acquisition Acquisition entity.
     * @return Acquisition document.
     */
    public AcquisitionDocument toAcquisitionDocument(Acquisition acquisition) {
        AcquisitionDocument document = new AcquisitionDocument();
        document.setId(acquisition.getId());
        document.setAcquisitionTypeOid(acquisition.getAcquisitionTypeOid());
        document.setProductOid(acquisition.getProductOid());
        document.setProductName(acquisition.getProductName());
        document.setQuantity(acquisition.getQuantity());
        document.setRealCost(acquisition.getRealCost());
        document.setUnitRealCost(acquisition.getUnitRealCost());
        document.setUnitPublicCost(acquisition.getUnitPublicCost());
        document.setSupplierOid(acquisition.getSupplierOid());
        document.setSupplierName(acquisition.getSupplierName());
        document.setInvoiceNumber(acquisition.getInvoiceNumber());
        document.setAcquisitionDate(acquisition.getAcquisitionDate());
        document.setObservations(acquisition.getObservations());
        document.setUserOid(acquisition.getUserOid());
        document.setIsDeleted(acquisition.getIsDeleted());
        document.setCreatedDate(acquisition.getCreatedDate());
        document.setUpdatedDate(acquisition.getUpdatedDate());
        return document;
    }

    /**
     * Maps an acquisition document to an acquisition entity.
     *
     * @param document Acquisition document.
     * @return Acquisition entity.
     */
    public Acquisition toAcquisitionEntity(AcquisitionDocument document) {
        Acquisition acquisition = new Acquisition();
        acquisition.setId(document.getId());
        acquisition.setAcquisitionTypeOid(document.getAcquisitionTypeOid());
        acquisition.setProductOid(document.getProductOid());
        acquisition.setProductName(document.getProductName());
        acquisition.setQuantity(document.getQuantity());
        acquisition.setRealCost(document.getRealCost());
        acquisition.setUnitRealCost(document.getUnitRealCost());
        acquisition.setUnitPublicCost(document.getUnitPublicCost());
        acquisition.setSupplierOid(document.getSupplierOid());
        acquisition.setSupplierName(document.getSupplierName());
        acquisition.setInvoiceNumber(document.getInvoiceNumber());
        acquisition.setAcquisitionDate(document.getAcquisitionDate());
        acquisition.setObservations(document.getObservations());
        acquisition.setUserOid(document.getUserOid());
        acquisition.setIsDeleted(document.getIsDeleted());
        acquisition.setCreatedDate(document.getCreatedDate());
        acquisition.setUpdatedDate(document.getUpdatedDate());
        return acquisition;
    }

    /**
     * Maps a list of acquisition documents to acquisition entities.
     *
     * @param documents Acquisition documents.
     * @return Acquisition entities.
     */
    public List<Acquisition> toAcquisitionEntityList(List<AcquisitionDocument> documents) {
        return documents.stream().map(this::toAcquisitionEntity).toList();
    }

    /**
     * Maps a product cost history entity to a product cost history document.
     *
     * @param productCostHistory Product cost history entity.
     * @return Product cost history document.
     */
    public ProductCostHistoryDocument toProductCostHistoryDocument(ProductCostHistory productCostHistory) {
        ProductCostHistoryDocument document = new ProductCostHistoryDocument();
        document.setId(productCostHistory.getId());
        document.setProductOid(productCostHistory.getProductOid());
        document.setAcquisitionOid(productCostHistory.getAcquisitionOid());
        document.setQuantity(productCostHistory.getQuantity());
        document.setRemainingQuantity(productCostHistory.getRemainingQuantity());
        document.setRealCost(productCostHistory.getRealCost());
        document.setUnitRealCost(productCostHistory.getUnitRealCost());
        document.setUnitPublicCostAtPurchase(productCostHistory.getUnitPublicCostAtPurchase());
        document.setAcquisitionDate(productCostHistory.getAcquisitionDate());
        document.setUserOid(productCostHistory.getUserOid());
        document.setCreatedDate(productCostHistory.getCreatedDate());
        return document;
    }

    /**
     * Maps a product cost history document to a product cost history entity.
     *
     * @param document Product cost history document.
     * @return Product cost history entity.
     */
    public ProductCostHistory toProductCostHistoryEntity(ProductCostHistoryDocument document) {
        ProductCostHistory productCostHistory = new ProductCostHistory();
        productCostHistory.setId(document.getId());
        productCostHistory.setProductOid(document.getProductOid());
        productCostHistory.setAcquisitionOid(document.getAcquisitionOid());
        productCostHistory.setQuantity(document.getQuantity());
        productCostHistory.setRemainingQuantity(document.getRemainingQuantity());
        productCostHistory.setRealCost(document.getRealCost());
        productCostHistory.setUnitRealCost(document.getUnitRealCost());
        productCostHistory.setUnitPublicCostAtPurchase(document.getUnitPublicCostAtPurchase());
        productCostHistory.setAcquisitionDate(document.getAcquisitionDate());
        productCostHistory.setUserOid(document.getUserOid());
        productCostHistory.setCreatedDate(document.getCreatedDate());
        return productCostHistory;
    }

    /**
     * Maps a list of product cost history documents to product cost history entities.
     *
     * @param documents Product cost history documents.
     * @return Product cost history entities.
     */
    public List<ProductCostHistory> toProductCostHistoryEntityList(List<ProductCostHistoryDocument> documents) {
        return documents.stream().map(this::toProductCostHistoryEntity).toList();
    }

    /**
     * Maps an expense entity to an expense document.
     *
     * @param expense Expense entity.
     * @return Expense document.
     */
    public ExpenseDocument toExpenseDocument(Expense expense) {
        ExpenseDocument document = new ExpenseDocument();
        document.setId(expense.getId());
        document.setAcquisitionOid(expense.getAcquisitionOid());
        document.setAcquisitionTypeOid(expense.getAcquisitionTypeOid());
        document.setName(expense.getName());
        document.setQuantity(expense.getQuantity());
        document.setAmount(expense.getAmount());
        document.setCurrency(expense.getCurrency());
        document.setExpenseDate(expense.getExpenseDate());
        document.setUserOid(expense.getUserOid());
        document.setIsActive(expense.getIsActive());
        document.setIsDeleted(expense.getIsDeleted());
        document.setCreatedDate(expense.getCreatedDate());
        document.setUpdatedDate(expense.getUpdatedDate());
        return document;
    }

    /**
     * Maps an expense document to an expense entity.
     *
     * @param document Expense document.
     * @return Expense entity.
     */
    public Expense toExpenseEntity(ExpenseDocument document) {
        Expense expense = new Expense();
        expense.setId(document.getId());
        expense.setAcquisitionOid(document.getAcquisitionOid());
        expense.setAcquisitionTypeOid(document.getAcquisitionTypeOid());
        expense.setName(document.getName());
        expense.setQuantity(document.getQuantity());
        expense.setAmount(document.getAmount());
        expense.setCurrency(document.getCurrency());
        expense.setExpenseDate(document.getExpenseDate());
        expense.setUserOid(document.getUserOid());
        expense.setIsActive(document.getIsActive());
        expense.setIsDeleted(document.getIsDeleted());
        expense.setCreatedDate(document.getCreatedDate());
        expense.setUpdatedDate(document.getUpdatedDate());
        return expense;
    }

    /**
     * Maps a list of expense documents to expense entities.
     *
     * @param documents Expense documents.
     * @return Expense entities.
     */
    public List<Expense> toExpenseEntityList(List<ExpenseDocument> documents) {
        return documents.stream().map(this::toExpenseEntity).toList();
    }

    /**
     * Maps a role entity to a role document.
     *
     * @param role Role entity.
     * @return Role document.
     */
    public RoleDocument toRoleDocument(Role role) {
        RoleDocument document = new RoleDocument();
        document.setId(role.getId());
        document.setName(role.getName());
        document.setIsDeleted(role.getIsDeleted());
        document.setIsActive(role.getIsActive());
        document.setCreatedDate(role.getCreatedDate());
        document.setUpdatedDate(role.getUpdatedDate());
        document.setCreatedBy(role.getCreatedBy());
        
        List<RolePermissionDocument> permissionDocuments = role.getPermissions().stream()
                .map(this::toRolePermissionDocument)
                .collect(Collectors.toList());
        document.setPermissions(permissionDocuments);
        
        return document;
    }

    /**
     * Maps a role document to a role entity.
     *
     * @param document Role document.
     * @return Role entity.
     */
    public Role toRoleEntity(RoleDocument document) {
        Role role = new Role();
        role.setId(document.getId());
        role.setName(document.getName());
        role.setIsDeleted(document.getIsDeleted());
        role.setIsActive(document.getIsActive());
        role.setCreatedDate(document.getCreatedDate());
        role.setUpdatedDate(document.getUpdatedDate());
        role.setCreatedBy(document.getCreatedBy());
        
        List<RolePermission> permissions = document.getPermissions().stream()
                .map(this::toRolePermission)
                .collect(Collectors.toList());
        role.setPermissions(permissions);
        
        return role;
    }

    /**
     * Maps a list of role documents to role entities.
     *
     * @param documents Role documents.
     * @return Role entities.
     */
    public List<Role> toRoleEntityList(List<RoleDocument> documents) {
        return documents.stream().map(this::toRoleEntity).toList();
    }

    /**
     * Maps a role permission entity to a role permission document.
     *
     * @param permission Role permission entity.
     * @return Role permission document.
     */
    private RolePermissionDocument toRolePermissionDocument(RolePermission permission) {
        RolePermissionDocument document = new RolePermissionDocument();
        document.setModuleOid(permission.getModuleOid());
        
        RolePermissionDocument.PermissionDetailsDocument details = 
            new RolePermissionDocument.PermissionDetailsDocument();
        details.setCreate(permission.getPermissions().getCreate());
        details.setUpdate(permission.getPermissions().getUpdate());
        details.setDelete(permission.getPermissions().getDelete());
        details.setView(permission.getPermissions().getView());
        document.setPermissions(details);
        
        return document;
    }

    /**
     * Maps a role permission document to a role permission entity.
     *
     * @param document Role permission document.
     * @return Role permission entity.
     */
    private RolePermission toRolePermission(RolePermissionDocument document) {
        RolePermission permission = new RolePermission();
        permission.setModuleOid(document.getModuleOid());
        
        RolePermission.PermissionDetails details = new RolePermission.PermissionDetails();
        details.setCreate(document.getPermissions().getCreate());
        details.setUpdate(document.getPermissions().getUpdate());
        details.setDelete(document.getPermissions().getDelete());
        details.setView(document.getPermissions().getView());
        permission.setPermissions(details);
        
        return permission;
    }
}
