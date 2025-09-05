# Script to translate Italian JavaDoc to English
$translations = @{
    "Aggiunge una sottocategoria a questa categoria\." = "Adds a subcategory to this category."
    "Implementa il pattern Composite\." = "Implements the Composite pattern."
    "Rimuove una sottocategoria da questa categoria\." = "Removes a subcategory from this category."
    "Verifica se questa categoria è discendente della categoria specificata\." = "Checks if this category is a descendant of the specified category."
    "Ottiene tutte le sottocategorie inclusi i discendenti\." = "Gets all subcategories including descendants."
    "Ottiene il percorso completo della categoria" = "Gets the full path of the category"
    "Verifica se questa categoria è una categoria radice \(senza padre\)\." = "Checks if this category is a root category (without parent)."
    "Verifica se questa categoria è una foglia \(senza sottocategorie\)\." = "Checks if this category is a leaf (without subcategories)."
    "Subject per il pattern Observer che gestisce le notifiche dei budget\." = "Subject for the Observer pattern that manages budget notifications."
    "Mantiene una lista di observer e li notifica quando ci sono cambiamenti nei budget\." = "Maintains a list of observers and notifies them when there are budget changes."
    "Notifica tutti gli observer di un superamento di budget\." = "Notifies all observers of a budget exceeded."
    "Notifica tutti gli observer che un budget si avvicina al limite\." = "Notifies all observers that a budget is near the limit."
    "Notifica tutti gli observer dell'aggiunta di una spesa a un budget\." = "Notifies all observers of an expense added to a budget."
    "Processa una spesa e verifica se devono essere inviate notifiche\." = "Processes an expense and checks if notifications should be sent."
    "Ottiene il numero di observer registrati\." = "Gets the number of registered observers."
    "Rimuove tutti gli observer\." = "Removes all observers."
}

Get-ChildItem -Recurse -Path "src" -Filter "*.java" | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content $filePath -Raw -Encoding UTF8
    
    foreach ($key in $translations.Keys) {
        $content = $content -replace $key, $translations[$key]
    }
    
    Set-Content $filePath $content -Encoding UTF8
    Write-Host "Processed: $filePath"
}

Write-Host "JavaDoc translation completed!"
