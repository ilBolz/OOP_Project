package org.finance.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Category Tests - Pattern Composite")
class CategoryTest {
    private Category casa;
    private Category bollette;
    private Category elettricita;
    private Category gas;
    @BeforeEach
    void setUp() {
        casa = new Category("Casa", "Spese relative alla casa");
        bollette = new Category("Bollette", "Utenze varie");
        elettricita = new Category("Elettricità", "Bolletta elettrica");
        gas = new Category("Gas", "Bolletta gas");
    }
    @Test
    @DisplayName("Dovrebbe creare una categoria con nome e descrizione")
    void shouldCreateCategoryWithNameAndDescription() {
        assertNotNull(casa.getId());
        assertEquals("Casa", casa.getName());
        assertEquals("Spese relative alla casa", casa.getDescription());
        assertTrue(casa.isRoot());
        assertTrue(casa.isLeaf());
    }
    @Test
    @DisplayName("Dovrebbe gestire la gerarchia di categorie correttamente")
    void shouldManageCategoryHierarchy() {
        casa.addSubcategory(bollette);
        bollette.addSubcategory(elettricita);
        bollette.addSubcategory(gas);
        assertFalse(casa.isLeaf()); // Ha sottocategorie
        assertTrue(casa.isRoot()); // Non ha padre
        assertFalse(bollette.isLeaf()); // Ha sottocategorie
        assertFalse(bollette.isRoot()); // Ha un padre
        assertEquals(casa, bollette.getParent());
        assertTrue(elettricita.isLeaf()); // Non ha sottocategorie
        assertFalse(elettricita.isRoot()); // Ha un padre
        assertEquals(bollette, elettricita.getParent());
    }
    @Test
    @DisplayName("Dovrebbe generare il percorso completo correttamente")
    void shouldGenerateFullPathCorrectly() {
        casa.addSubcategory(bollette);
        bollette.addSubcategory(elettricita);
        assertEquals("Casa", casa.getFullPath());
        assertEquals("Casa > Bollette", bollette.getFullPath());
        assertEquals("Casa > Bollette > Elettricità", elettricita.getFullPath());
    }
    @Test
    @DisplayName("Dovrebbe ottenere tutte le sottocategorie incluse quelle annidate")
    void shouldGetAllSubcategoriesIncludingNested() {
        casa.addSubcategory(bollette);
        bollette.addSubcategory(elettricita);
        bollette.addSubcategory(gas);
        var allSubs = casa.getAllSubcategories();
        assertEquals(3, allSubs.size()); // bollette, elettricita, gas
        assertTrue(allSubs.contains(bollette));
        assertTrue(allSubs.contains(elettricita));
        assertTrue(allSubs.contains(gas));
    }
    @Test
    @DisplayName("Dovrebbe impedire riferimenti circolari")
    void shouldPreventCircularReferences() {
        casa.addSubcategory(bollette);
        assertThrows(IllegalArgumentException.class, () -> {
            bollette.addSubcategory(casa); // Tentativo di creare un ciclo
        });
    }
    @Test
    @DisplayName("Dovrebbe impedire l'aggiunta di una categoria come sottocategoria di se stessa")
    void shouldPreventSelfReference() {
        assertThrows(IllegalArgumentException.class, () -> {
            casa.addSubcategory(casa);
        });
    }
    @Test
    @DisplayName("Dovrebbe rimuovere sottocategorie correttamente")
    void shouldRemoveSubcategoriesCorrectly() {
        casa.addSubcategory(bollette);
        assertEquals(1, casa.getSubcategories().size());
        assertEquals(casa, bollette.getParent());
        casa.removeSubcategory(bollette);
        assertEquals(0, casa.getSubcategories().size());
        assertNull(bollette.getParent());
    }
}



