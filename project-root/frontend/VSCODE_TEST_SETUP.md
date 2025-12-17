# VSCode'da Frontend Testlerini Çalıştırma Rehberi

## Adım 1: Jest Extension'ını Yükleyin

1. VSCode'u açın
2. Sol taraftaki **Extensions** ikonuna tıklayın (veya `Cmd+Shift+X` tuşlarına basın)
3. Arama kutusuna **"Jest"** yazın
4. **"Jest"** by **Orta** (orta.vscode-jest) extension'ını bulun
5. **Install** butonuna tıklayın

## Adım 2: VSCode'u Yeniden Başlatın

1. `Cmd+Shift+P` tuşlarına basın (Command Palette açılır)
2. **"Developer: Reload Window"** yazın ve Enter'a basın
3. Veya VSCode'u kapatıp tekrar açın

## Adım 3: Test Explorer'ı Bulun

### Yöntem 1: Sidebar'dan

1. Sol sidebar'da en üstteki **Activity Bar**'a bakın
2. Şu ikonları arayın:
   - 🧪 **Test** ikonu (flask/beaker ikonu)
   - Veya **Testing** yazısı
3. Eğer görmüyorsanız, Activity Bar'ın en altındaki **"..."** (üç nokta) menüsüne tıklayın
4. **"Testing"** seçeneğini işaretleyin

### Yöntem 2: Command Palette'den

1. `Cmd+Shift+P` tuşlarına basın
2. **"Test: Focus on Test View"** yazın ve Enter'a basın
3. Test Explorer paneli açılacak

### Yöntem 3: View Menüsünden

1. Üst menüden **View** → **Testing** seçin
2. Test Explorer paneli açılacak

## Adım 4: Jest'i Başlatın

1. `Cmd+Shift+P` tuşlarına basın
2. **"Jest: Start All Runners"** yazın ve Enter'a basın
3. Alt kısımdaki **Output** panelinde Jest çıktısını göreceksiniz
4. Output dropdown'ından **"Jest"** seçeneğini seçin

## Adım 5: Test Dosyalarını Kontrol Edin

1. `src/__tests__/` klasöründeki bir test dosyasını açın (örn: `Register.test.js`)
2. `describe` veya `it` satırlarının üzerinde **"Run Test"** ve **"Debug Test"** linklerini görmelisiniz
3. Bu linklere tıklayarak testleri çalıştırabilirsiniz

## Sorun Giderme

### Test Explorer görünmüyorsa:

1. Jest extension'ının yüklü olduğundan emin olun
2. VSCode'u yeniden başlatın
3. `Cmd+Shift+P` → **"Jest: Start All Runners"** çalıştırın

### "No tests found" hatası alıyorsanız:

1. Terminal'de şu komutu çalıştırın:
   ```bash
   cd project-root/frontend
   npm test -- --watchAll=false
   ```
2. Eğer terminal'de testler çalışıyorsa, Jest extension ayarlarını kontrol edin

### Jest Output'u kontrol edin:

1. Alt kısımdaki **Output** panelini açın
2. Dropdown'dan **"Jest"** seçin
3. Hata mesajlarını kontrol edin

## Alternatif: Terminal'den Test Çalıştırma

Eğer VSCode'da test butonları görünmüyorsa, terminal'den çalıştırabilirsiniz:

```bash
cd project-root/frontend
npm test
```

Watch mode için:

```bash
npm test
```

Tek seferlik çalıştırma için:

```bash
npm test -- --watchAll=false
```
