# Set All Pilots Available Script
# Tüm pilotları available=true yapar

Write-Host "`n=== PILOTLARI AVAILABLE YAPMA ===" -ForegroundColor Green

try {
    $url = "http://localhost:8082/api/pilots/all/availability?isAvailable=true"
    
    Write-Host "`nPilot Service endpoint cagriliyor..." -ForegroundColor Yellow
    Write-Host "URL: $url" -ForegroundColor Cyan
    
    # PUT request - body olmadan
    $headers = @{
        "Content-Type" = "application/json"
    }
    $response = Invoke-WebRequest -Uri $url -Method PUT -Headers $headers -UseBasicParsing
    
    if ($response.StatusCode -eq 200) {
        $pilots = $response.Content | ConvertFrom-Json
        Write-Host "`nBasarili! Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "Toplam pilot sayisi: $($pilots.Count)" -ForegroundColor Cyan
        
        $availableCount = ($pilots | Where-Object { $_.isAvailable -eq $true }).Count
        Write-Host "Available pilot sayisi: $availableCount" -ForegroundColor Cyan
        
        Write-Host "`nIlk 5 pilot:" -ForegroundColor Yellow
        $pilots | Select-Object -First 5 | Format-Table id, pilotId, name, isAvailable
    }
    
    Write-Host "`nBasarili! Pilotlar guncellendi:" -ForegroundColor Green
    Write-Host "Toplam pilot sayisi: $($response.Count)" -ForegroundColor Cyan
    
    $availableCount = ($response | Where-Object { $_.isAvailable -eq $true }).Count
    Write-Host "Available pilot sayisi: $availableCount" -ForegroundColor Cyan
    
    Write-Host "`nIlk 5 pilot:" -ForegroundColor Yellow
    $response | Select-Object -First 5 | Format-Table id, pilotId, name, isAvailable
    
} catch {
    Write-Host "`nHATA:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        Write-Host $_.ErrorDetails.Message -ForegroundColor Red
    }
}

Write-Host "`n=== TEST ICIN YENI ROSTER OLUSTUR ===" -ForegroundColor Yellow
Write-Host "Browser'da veya PowerShell'de:" -ForegroundColor Cyan
Write-Host "http://localhost:8080/api/rosters/new?flightNumber=TK013&databaseType=SQL" -ForegroundColor White

