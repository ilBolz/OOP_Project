package org.finance.config;
import org.finance.model.Category;
import java.util.ArrayList;
import java.util.List;
/**
 * Configuration of default categories for the financial management system.
 */
public class DefaultCategories {
    /**
     * Creates and returns the list of default categories with their hierarchy.
     */
    public static List<Category> createDefaultCategories() {
        List<Category> rootCategories = new ArrayList<>();
        Category home = new Category("Casa", "Spese relative alla casa e all'abitazione");
        home.addSubcategory(new Category("Affitto", "Pagamento mensile dell'affitto"));
        home.addSubcategory(new Category("Mutuo", "Rate del mutuo"));
        home.addSubcategory(new Category("Condominio", "Spese condominiali"));
        home.addSubcategory(new Category("Manutenzione", "Riparazioni e manutenzione casa"));
        home.addSubcategory(new Category("Arredamento", "Mobili e decorazioni"));
        Category bills = new Category("Bollette", "Utenze e servizi");
        bills.addSubcategory(new Category("Elettricità", "Bolletta elettrica"));
        bills.addSubcategory(new Category("Gas", "Bolletta gas"));
        bills.addSubcategory(new Category("Acqua", "Bolletta acqua"));
        bills.addSubcategory(new Category("Internet", "Connessione internet e telefono"));
        home.addSubcategory(bills);
        rootCategories.add(home);
        Category transports = new Category("Trasporti", "Spese per mezzi di trasporto");
        transports.addSubcategory(new Category("Carburante", "Benzina, diesel, elettricit� per auto"));
        transports.addSubcategory(new Category("Assicurazione Auto", "Assicurazione veicoli"));
        transports.addSubcategory(new Category("Manutenzione Auto", "Riparazioni e tagliandi"));
        transports.addSubcategory(new Category("Trasporto Pubblico", "Autobus, metro, treni"));
        transports.addSubcategory(new Category("Taxi/Uber", "Servizi di trasporto privato"));
        transports.addSubcategory(new Category("Parcheggi", "Costi di parcheggio"));
        rootCategories.add(transports);
        Category food = new Category("Alimentari", "Spese per cibo e bevande");
        food.addSubcategory(new Category("Spesa Quotidiana", "Supermercato e alimentari"));
        food.addSubcategory(new Category("Ristoranti", "Pranzi e cene fuori"));
        food.addSubcategory(new Category("Delivery", "Cibo a domicilio"));
        food.addSubcategory(new Category("Bar/Caffè", "Colazioni e pause caffè"));
        food.addSubcategory(new Category("Alcol", "Bevande alcoliche"));
        rootCategories.add(food);
        Category health = new Category("Salute", "Spese mediche e benessere");
        health.addSubcategory(new Category("Visite Mediche", "Medico di base e specialisti"));
        health.addSubcategory(new Category("Farmaci", "Medicine e integratori"));
        health.addSubcategory(new Category("Dentista", "Cure dentali"));
        health.addSubcategory(new Category("Palestra", "Abbonamenti fitness"));
        health.addSubcategory(new Category("Assicurazione Sanitaria", "Polizze sanitarie"));
        rootCategories.add(health);
        Category freeTime = new Category("Intrattenimento", "Svago e tempo libero");
        freeTime.addSubcategory(new Category("Cinema/Teatro", "Spettacoli e intrattenimento"));
        freeTime.addSubcategory(new Category("Streaming", "Netflix, Spotify, etc."));
        freeTime.addSubcategory(new Category("Libri/Giornali", "Lettura e informazione"));
        freeTime.addSubcategory(new Category("Videogames", "Console e giochi"));
        freeTime.addSubcategory(new Category("Hobby", "Attività ricreative"));
        freeTime.addSubcategory(new Category("Viaggi", "Vacanze e weekend"));
        rootCategories.add(freeTime);
        Category clothes = new Category("Abbigliamento", "Vestiti e accessori");
        clothes.addSubcategory(new Category("Vestiti", "Abbigliamento generale"));
        clothes.addSubcategory(new Category("Scarpe", "Calzature"));
        clothes.addSubcategory(new Category("Accessori", "Borse, cinture, gioielli"));
        clothes.addSubcategory(new Category("Parrucchiere", "Cura dei capelli"));
        rootCategories.add(clothes);
        Category investments = new Category("Investimenti", "Strumenti di investimento");
        investments.addSubcategory(new Category("Azioni", "Acquisto di azioni"));
        investments.addSubcategory(new Category("ETF", "Exchange Traded Funds"));
        investments.addSubcategory(new Category("Fondi", "Fondi comuni di investimento"));
        investments.addSubcategory(new Category("Crypto", "Criptovalute"));
        investments.addSubcategory(new Category("Immobili", "Investimenti immobiliari"));
        rootCategories.add(investments);
        Category income = new Category("Entrate", "Fonti di reddito");
        income.addSubcategory(new Category("Stipendio", "Salario fisso"));
        income.addSubcategory(new Category("Freelance", "Lavoro autonomo"));
        income.addSubcategory(new Category("Investimenti Rendita", "Dividendi e interessi"));
        income.addSubcategory(new Category("Bonus", "Premi e bonus lavorativi"));
        income.addSubcategory(new Category("Rimborsi", "Rimborsi spese"));
        income.addSubcategory(new Category("Altri Redditi", "Altre fonti di reddito"));
        rootCategories.add(income);
        return rootCategories;
    }
}



