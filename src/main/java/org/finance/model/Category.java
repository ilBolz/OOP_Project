package org.finance.model;
import java.util.*;
/**
 * Implementation of the Composite pattern for hierarchical category management.
 * A category can contain subcategories and be contained in a parent category.
 */
public class Category {
    private final String id;
    private final String name;
    private final String description;
    private Category parent;
    private final Set<Category> subcategories;
    public Category(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description != null ? description : "";
        this.subcategories = new HashSet<>();
    }
    public Category(String name) {
        this(name, "");
    }
    /**
     * Adds a subcategory to this category.
     * Implements the Composite pattern.
     */
    public void addSubcategory(Category subcategory) {
        Objects.requireNonNull(subcategory, "Subcategory cannot be null");
        if (subcategory == this) {
            throw new IllegalArgumentException("Cannot add category as subcategory of itself");
        }
        if (isDescendantOf(subcategory)) {
            throw new IllegalArgumentException("Cannot create circular reference");
        }
        subcategory.parent = this;
        this.subcategories.add(subcategory);
    }
    /**
     * Removes a subcategory from this category.
     */
    public void removeSubcategory(Category subcategory) {
        if (subcategories.remove(subcategory)) {
            subcategory.parent = null;
        }
    }
    /**
     * Verifica se questa categoria � discendente della categoria specificata.
     */
    private boolean isDescendantOf(Category potentialAncestor) {
        Category current = this.parent;
        while (current != null) {
            if (current.equals(potentialAncestor)) {
                return true;
            }
            current = current.parent;
        }
        return false;
    }
    /**
     * Ottiene tutte le sottocategorie (incluse quelle annidate).
     */
    public Set<Category> getAllSubcategories() {
        Set<Category> allSubs = new HashSet<>();
        for (Category sub : subcategories) {
            allSubs.add(sub);
            allSubs.addAll(sub.getAllSubcategories());
        }
        return allSubs;
    }
    /**
     * Gets the full path of the category (es. "Casa > Bollette > Elettricit�").
     */
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
    /**
     * Verifica se questa categoria � una categoria foglia (senza sottocategorie).
     */
    public boolean isLeaf() {
        return subcategories.isEmpty();
    }
    /**
     * Verifica se questa categoria � la radice (senza categoria padre).
     */
    public boolean isRoot() {
        return parent == null;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getParent() { return parent; }
    public Set<Category> getSubcategories() { return new HashSet<>(subcategories); }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return String.format("Category{name='%s', fullPath='%s'}", name, getFullPath());
    }
}



