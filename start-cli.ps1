# Personal Finance Manager - CLI Launcher
Write-Host "===============================================" -ForegroundColor Green
Write-Host "   PERSONAL FINANCE MANAGER - CLI LAUNCHER" -ForegroundColor Green  
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Compilazione in corso..." -ForegroundColor Yellow
try {
    & mvn compile -q
    if ($LASTEXITCODE -ne 0) {
        throw "Compilazione fallita"
    }
    Write-Host "Compilazione completata!" -ForegroundColor Green
} catch {
    Write-Host "ERRORE: Compilazione fallita!" -ForegroundColor Red
    Read-Host "Premi Enter per uscire"
    exit 1
}

Write-Host "Copia dipendenze..." -ForegroundColor Yellow
try {
    & mvn dependency:copy-dependencies -q
    if ($LASTEXITCODE -ne 0) {
        throw "Copia dipendenze fallita"
    }
    Write-Host "Dipendenze copiate!" -ForegroundColor Green
} catch {
    Write-Host "ERRORE: Copia dipendenze fallita!" -ForegroundColor Red
    Read-Host "Premi Enter per uscire"
    exit 1
}

Write-Host ""
Write-Host "Avvio interfaccia CLI..." -ForegroundColor Cyan
Write-Host "Premi Ctrl+C per uscire" -ForegroundColor Yellow
Write-Host ""

try {
    & java -cp "target/classes;target/dependency/*" org.finance.cli.FinanceCLI
} catch {
    Write-Host "Errore durante l'avvio della CLI" -ForegroundColor Red
}

Write-Host ""
Write-Host "CLI terminata." -ForegroundColor Green
Read-Host "Premi Enter per uscire"
