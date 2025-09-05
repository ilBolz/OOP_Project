package org.finance.config;

import org.finance.model.Category;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurazione delle categorie predefinite per il sistema di gestione finanziaria.
 */
public class DefaultCategories {
    
    /**
     * Crea e restituisce la lista delle categorie predefinite con la loro gerarchia.
     */
    public static List<Category> createDefaultCategories() {
        List<Category> rootCategories = new ArrayList<>();
        
        // Categoria Casa
        Category casa = new Category("Casa", "Spese relative alla casa e all'abitazione");
        casa.addSubcategory(new Category("Affitto", "Pagamento mensile dell'affitto"));
        casa.addSubcategory(new Category("Mutuo", "Rate del mutuo"));
        casa.addSubcategory(new Category("Condominio", "Spese condominiali"));
        
        Category bollette = new Category("Bollette", "Utenze e servizi");
        bollette.addSubcategory(new Category("Elettricità", "Bolletta elettrica"));
        bollette.addSubcategory(new Category("Gas", "Bolletta gas"));
        bollette.addSubcategory(new Category("Acqua", "Bolletta acqua"));
        bollette.addSubcategory(new Category("Internet", "Connessione internet e telefono"));
        casa.addSubcategory(bollette);
        
        casa.addSubcategory(new Category("Manutenzione", "Riparazioni e manutenzione casa"));
        casa.addSubcategory(new Category("Arredamento", "Mobili e decorazioni"));
        rootCategories.add(casa);
        
        // Categoria Trasporti
        Category trasporti = new Category("Trasporti", "Spese per mezzi di trasporto");
        trasporti.addSubcategory(new Category("Carburante", "Benzina, diesel, elettricità per auto"));
        trasporti.addSubcategory(new Category("Assicurazione Auto", "Assicurazione veicoli"));
        trasporti.addSubcategory(new Category("Manutenzione Auto", "Riparazioni e tagliandi"));
        trasporti.addSubcategory(new Category("Trasporto Pubblico", "Autobus, metro, treni"));
        trasporti.addSubcategory(new Category("Taxi/Uber", "Servizi di trasporto privato"));
        trasporti.addSubcategory(new Category("Parcheggi", "Costi di parcheggio"));
        rootCategories.add(trasporti);
        
        // Categoria Alimentari
        Category alimentari = new Category("Alimentari", "Spese per cibo e bevande");
        alimentari.addSubcategory(new Category("Spesa Quotidiana", "Supermercato e alimentari"));
        alimentari.addSubcategory(new Category("Ristoranti", "Pranzi e cene fuori"));
        alimentari.addSubcategory(new Category("Delivery", "Cibo a domicilio"));
        alimentari.addSubcategory(new Category("Bar/Caffè", "Colazioni e pause caffè"));
        alimentari.addSubcategory(new Category("Alcol", "Bevande alcoliche"));
        rootCategories.add(alimentari);
        
        // Categoria Salute
        Category salute = new Category("Salute", "Spese mediche e benessere");
        salute.addSubcategory(new Category("Visite Mediche", "Medico di base e specialisti"));
        salute.addSubcategory(new Category("Farmaci", "Medicine e integratori"));
        salute.addSubcategory(new Category("Dentista", "Cure dentali"));
        salute.addSubcategory(new Category("Palestra", "Abbonamenti fitness"));
        salute.addSubcategory(new Category("Assicurazione Sanitaria", "Polizze sanitarie"));
        rootCategories.add(salute);
        
        // Categoria Intrattenimento
        Category intrattenimento = new Category("Intrattenimento", "Svago e tempo libero");
        intrattenimento.addSubcategory(new Category("Cinema/Teatro", "Spettacoli e intrattenimento"));
        intrattenimento.addSubcategory(new Category("Streaming", "Netflix, Spotify, etc."));
        intrattenimento.addSubcategory(new Category("Libri/Giornali", "Lettura e informazione"));
        intrattenimento.addSubcategory(new Category("Videogames", "Console e giochi"));
        intrattenimento.addSubcategory(new Category("Hobby", "Attività ricreative"));
        intrattenimento.addSubcategory(new Category("Viaggi", "Vacanze e weekend"));
        rootCategories.add(intrattenimento);
        
        // Categoria Abbigliamento
        Category abbigliamento = new Category("Abbigliamento", "Vestiti e accessori");
        abbigliamento.addSubcategory(new Category("Vestiti", "Abbigliamento generale"));
        abbigliamento.addSubcategory(new Category("Scarpe", "Calzature"));
        abbigliamento.addSubcategory(new Category("Accessori", "Borse, cinture, gioielli"));
        abbigliamento.addSubcategory(new Category("Parrucchiere", "Cura dei capelli"));
        rootCategories.add(abbigliamento);
        
        // Categoria Educazione
        Category educazione = new Category("Educazione", "Formazione e crescita personale");
        educazione.addSubcategory(new Category("Corsi", "Formazione professionale"));
        educazione.addSubcategory(new Category("Università", "Tasse universitarie"));
        educazione.addSubcategory(new Category("Certificazioni", "Certificazioni professionali"));
        educazione.addSubcategory(new Category("Libri Tecnici", "Manuali e testi specialistici"));
        rootCategories.add(educazione);
        
        // Categoria Investimenti
        Category investimenti = new Category("Investimenti", "Strumenti di investimento");
        investimenti.addSubcategory(new Category("Azioni", "Acquisto di azioni"));
        investimenti.addSubcategory(new Category("ETF", "Exchange Traded Funds"));
        investimenti.addSubcategory(new Category("Fondi", "Fondi comuni di investimento"));
        investimenti.addSubcategory(new Category("Crypto", "Criptovalute"));
        investimenti.addSubcategory(new Category("Immobili", "Investimenti immobiliari"));
        rootCategories.add(investimenti);
        
        // Categoria Entrate
        Category entrate = new Category("Entrate", "Fonti di reddito");
        entrate.addSubcategory(new Category("Stipendio", "Salario fisso"));
        entrate.addSubcategory(new Category("Freelance", "Lavoro autonomo"));
        entrate.addSubcategory(new Category("Investimenti Rendita", "Dividendi e interessi"));
        entrate.addSubcategory(new Category("Bonus", "Premi e bonus lavorativi"));
        entrate.addSubcategory(new Category("Rimborsi", "Rimborsi spese"));
        entrate.addSubcategory(new Category("Altri Redditi", "Altre fonti di reddito"));
        rootCategories.add(entrate);
        
        return rootCategories;
    }
    
    /**
     * Trova una categoria per nome tra le categorie predefinite.
     */
    public static Category findCategoryByName(String name) {
        List<Category> categories = createDefaultCategories();
        return findCategoryInList(categories, name);
    }
    
    private static Category findCategoryInList(List<Category> categories, String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
            
            // Cerca nelle sottocategorie
            Category found = findCategoryInList(
                new ArrayList<>(category.getSubcategories()), 
                name
            );
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
