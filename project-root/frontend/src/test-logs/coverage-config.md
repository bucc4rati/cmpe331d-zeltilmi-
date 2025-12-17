# Frontend Test Coverage Configuration

## Coverage Tool: Jest (Built-in)

Frontend projesinde **Jest** kullanılmaktadır ve Jest'in kendi code coverage sistemi mevcuttur. Backend'deki Jacoco gibi ayrı bir tool gerekmez.

## Coverage Komutları

### Coverage ile test çalıştırma:
```bash
npm run test:coverage
```

veya

```bash
npm test -- --coverage --watchAll=false
```

## Coverage Raporları

Coverage raporları şu klasörde oluşturulur:
- `src/test-logs/coverage/` (veya proje root'unda `coverage/`)

## Coverage Metrikleri

Jest coverage şu metrikleri sağlar:
- **Statements**: Kod satırlarının yüzdesi
- **Branches**: If/else dallarının yüzdesi  
- **Functions**: Fonksiyonların yüzdesi
- **Lines**: Satırların yüzdesi

## Backend vs Frontend Coverage

- **Backend (Java)**: Jacoco Maven Plugin kullanılır
- **Frontend (JavaScript/React)**: Jest'in built-in coverage kullanılır

Her ikisi de aynı amaca hizmet eder (code coverage ölçümü) ancak farklı teknolojiler için tasarlanmıştır.

