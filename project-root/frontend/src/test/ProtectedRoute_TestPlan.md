## 2. TEST PLAN – ProtectedRoute

### 2.1 Testing Strategy
- **Unit (white-box)**: ProtectedRoute’un auth kontrolü ve render mantığı. Hızlı ve izolasyonlu, Jest + React Testing Library (RTL).
- **Integration**: Router içinde gerçek rota akışı; token var/yok senaryosunda yönlendirme davranışı. MemoryRouter ile Navigation davranışı test ediliyor.
- **Motivasyon**: Bileşenin tek sorumluluğu “auth yoksa login’e yönlendir, varsa child render et.” Bu iki kritik dalın otomatik testle korunması gerekir.

### 2.2 Test Subjects (Integration Focus)
- Veri / durum: `localStorage.jwtToken`
- Akış: `/` rotasında ProtectedRoute çocuklarını render eder; token yoksa `/login`’e Navigate.
- Yan-etkiler: `Navigate` ile redirect; child component render edilmemesi.

### 2.3 Black Box Testing (Equivalence Partitioning)
| ID | Use Case | Açıklama | Girdi | Beklenen |
|----|----------|----------|-------|----------|
| TC-PR-01 | UC-Auth-Gate | Token yok | `jwtToken`: yok | `/login`’e yönlendirme, child görünmez |
| TC-PR-02 | UC-Auth-Gate | Geçerli token | `jwtToken`: “valid” | Child render edilir, redirect olmaz |
| TC-PR-03 | UC-Auth-Gate | Boş string token | `jwtToken`: "" | `/login`’e yönlendirme (falsy token) |

### 2.4 White Box Testing
- **Kaynak kod**: `src/components/ProtectedRoute.js`
- **Test dosyası**: `src/test/ProtectedRoute.test.js`
- **Testler**:
  - TC-PR-01, TC-PR-02 (ve gerekirse TC-PR-03) MemoryRouter + Routes ile doğrulanıyor.
- **Mock / Test Doubles**:
  - `localStorage` (jsdom native, testte clear/set yapılıyor)
  - `MemoryRouter` + `Routes/Route` (redirect ve render kontrolü)
- **Araçlar**: Jest, React Testing Library.

## 3. ADDITIONAL TESTS (ProtectedRoute’a yönelik)
- **Security**: Yetkisiz erişimde redirect doğrulaması (TC-PR-01). Admin/role kontrolleri bu bileşende olmadığı için kapsam dışı.
- **Performance / Load / Stress**: Bileşen minimal olduğu için uygulanabilir değil; ölçülebilir gecikme beklenmiyor.
- **Acceptance**: “Login olmadan korumalı sayfaya giden kullanıcı login sayfasına yönlendirilir.” senaryosu TC-PR-01 ile karşılanır.

## 4. TEST RESULTS
- **Uygulanan testler**: `ProtectedRoute.test.js` içinde 2 birim testi (token var / yok). İstenirse TC-PR-03 eklenebilir.
- **Önerilen coverage çalıştırma**: `npm test -- --coverage --watch=false --testPathPattern=src/test/ProtectedRoute.test.js`
- **Beklenen coverage**: ProtectedRoute için %100 line/branch; overall proje coverage raporuna otomatik eklenir.
- **Loglama**: Jest çıktısı (pass/fail) ve coverage HTML/JSON raporu `coverage/` altında üretilir. CI’da artefakt olarak saklanması önerilir.

