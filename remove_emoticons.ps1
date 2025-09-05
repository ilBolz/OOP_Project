# Script to remove emoticons from markdown files
Get-ChildItem -Recurse -Filter "*.md" | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content $filePath -Raw -Encoding UTF8
    
    # Remove common emoticons one by one
    $content = $content -replace '🗄️', ''
    $content = $content -replace '📊', ''
    $content = $content -replace '🏗️', ''
    $content = $content -replace '🔧', ''
    $content = $content -replace '💾', ''
    $content = $content -replace '🔄', ''
    $content = $content -replace '🖥️', ''
    $content = $content -replace '💰', ''
    $content = $content -replace '🔍', ''
    $content = $content -replace '🚨', ''
    $content = $content -replace '💱', ''
    $content = $content -replace '🎨', ''
    $content = $content -replace '🎯', ''
    $content = $content -replace '🚀', ''
    $content = $content -replace '💻', ''
    $content = $content -replace '🚧', ''
    $content = $content -replace '🏭', ''
    $content = $content -replace '👁️', ''
    $content = $content -replace '🔒', ''
    
    Set-Content $filePath $content -Encoding UTF8
    Write-Host "Processed: $filePath"
}

Write-Host "Emoticon removal completed!"
