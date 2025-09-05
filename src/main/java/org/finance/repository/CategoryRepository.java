package org.finance.repository;

import org.finance.model.Category;
import java.util.List;

/**
 * Repository specifico per le categorie.
 */
public interface CategoryRepository extends Repository<Category, String> {
    
    /**
     * Trova categorie padre (senza parent).
     */
    List<Category> findRootCategories();
    
    /**
     * Trova sottocategorie di una categoria padre.
     */
    List<Category> findByParentName(String parentName);
}
