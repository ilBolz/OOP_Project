# Complete translation script for remaining JavaDoc
$moreTranslations = @{
    "Aggiunge un observer alla lista\." = "Adds an observer to the list."
    "Rimuove un observer dalla lista\." = "Removes an observer from the list."
    "Calcola il valore di bilancio di questa transazione\." = "Calculates the balance value of this transaction."
    "Ottiene il tipo di questa transazione\." = "Gets the type of this transaction."
    "Imposta la categoria per questa transazione\." = "Sets the category for this transaction."
    "Ottiene il mese e anno della transazione\." = "Gets the month and year of the transaction."
    "Factory per la creazione dinamica di transazioni\." = "Factory for dynamic transaction creation."
    "Crea una transazione del tipo specificato\." = "Creates a transaction of the specified type."
    "Crea una transazione di entrata rapida\." = "Creates a quick income transaction."
    "Crea una transazione di spesa rapida\." = "Creates a quick expense transaction."
    "Crea una transazione di investimento rapida\." = "Creates a quick investment transaction."
    "Implementazione di una transazione di entrata\." = "Implementation of an income transaction."
    "Implementazione di una transazione di spesa\." = "Implementation of an expense transaction."
    "Implementazione di una transazione di investimento\." = "Implementation of an investment transaction."
    "Iteratore per la navigazione cronologica delle transazioni\." = "Iterator for chronological transaction navigation."
    "Implementa il pattern Iterator per permettere la navigazione attraverso le transazioni in ordine cronologico\." = "Implements the Iterator pattern to allow navigation through transactions in chronological order."
    "Verifica se ci sono altre transazioni da navigare\." = "Checks if there are more transactions to navigate."
    "Ritorna la prossima transazione nella sequenza cronologica\." = "Returns the next transaction in chronological sequence."
    "Interfaccia Observer per ricevere notifiche sui budget\." = "Observer interface for receiving budget notifications."
    "Chiamato quando un budget viene superato\." = "Called when a budget is exceeded."
    "Chiamato quando un budget si avvicina al limite\." = "Called when a budget approaches the limit."
    "Chiamato quando viene aggiunta una spesa a un budget\." = "Called when an expense is added to a budget."
    "Observer per le notifiche di budget che stampa messaggi sulla console\." = "Observer for budget notifications that prints messages to console."
    "Repository generico per operazioni CRUD\." = "Generic repository for CRUD operations."
    "Salva un'entità nel repository\." = "Saves an entity in the repository."
    "Trova un'entità per ID\." = "Finds an entity by ID."
    "Trova tutte le entità nel repository\." = "Finds all entities in the repository."
    "Aggiorna un'entità esistente\." = "Updates an existing entity."
    "Elimina un'entità per ID\." = "Deletes an entity by ID."
    "Conta il numero totale di entità\." = "Counts the total number of entities."
    "Verifica se un'entità esiste per ID\." = "Checks if an entity exists by ID."
}

Get-ChildItem -Recurse -Path "src" -Filter "*.java" | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content $filePath -Raw -Encoding UTF8
    
    foreach ($key in $moreTranslations.Keys) {
        $content = $content -replace $key, $moreTranslations[$key]
    }
    
    Set-Content $filePath $content -Encoding UTF8
    Write-Host "Processed: $filePath"
}

Write-Host "Additional JavaDoc translation completed!"
