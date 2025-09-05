# Script per rimuovere emoticons dai file Java
$files = @(
    "src\main\java\org\finance\cli\FinanceCLI.java",
    "src\main\java\org\finance\Main.java",
    "src\main\java\org\finance\CLIMain.java", 
    "src\main\java\org\finance\CLIDemo.java",
    "src\main\java\org\finance\observer\ConsoleBudgetObserver.java"
)

# Mappatura delle emoticons da sostituire
$replacements = @{
    "📊" = ""
    "📈" = ""
    "📁" = ""
    "💰" = ""
    "⚙️" = ""
    "➕" = ""
    "➖" = ""
    "🔍" = ""
    "📋" = ""
    "⬅️" = ""
    "❌" = ""
    "💡" = ""
    "🚀" = ""
    "✅" = ""
    "🎉" = ""
    "🎯" = ""
    "💻" = ""
    "📉" = ""
    "🚨" = ""
    "🏗️" = ""
    "💱" = ""
    "🔧" = ""
    "🎨" = ""
    "📭" = ""
    "📅" = ""
    "👋" = ""
    "⏳" = ""
    "🔄" = ""
    "⚠️" = ""
    "📂" = ""
    "🍽️" = ""
    "👁️" = ""
}

foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "Processando: $file"
        $content = Get-Content $file -Raw -Encoding UTF8
        
        foreach ($emoji in $replacements.Keys) {
            $content = $content -replace [regex]::Escape($emoji), $replacements[$emoji]
        }
        
        # Rimuovi spazi extra dopo la rimozione delle emoticons
        $content = $content -replace "\s+", " "
        $content = $content -replace " \n", "`n"
        $content = $content -replace " :", ":"
        
        Set-Content $file -Value $content -Encoding UTF8
        Write-Host "Completato: $file"
    }
}

Write-Host "Rimozione emoticons completata!"
