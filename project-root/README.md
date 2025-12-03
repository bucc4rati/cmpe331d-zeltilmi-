# Flight Roster Management System

A comprehensive microservices-based flight roster management system built with Spring Boot and React.



## 🚀 Quick Start

### Prerequisites

#### **Docker Compose (Recommended):**
- **Docker Desktop** (MUST be installed and running)
- **Docker Compose** (included with Docker Desktop)

**⚠️ Important:** Docker Desktop must be **running** for the application to work!

#### **Manual Setup (Alternative):**
- Java 17+
- Node.js 16+
- Maven 3.6+
- PostgreSQL 12+ (Tested with PostgreSQL 18)

### Running the Application

#### **🐳 Docker Compose (Recommended):**

1. **Make sure Docker Desktop is running:**
   ```bash
   # Check Docker is running
   docker --version
   docker ps
   ```

2. **Start all services:**
   ```bash
   cd project-root
   docker compose up -d
   ```

3. **Stop all services:**
   ```bash
   docker compose down
   ```

4. **View logs:**
   ```bash
   docker compose logs
   ```

5. **Access the application:**
   - **Frontend:** http://localhost:3000
   - **API Endpoints:**

### **Main System (Port 8080):**
- **Authentication:**
  - `http://localhost:8080/api/auth/register` - User registration (POST)
  - `http://localhost:8080/api/auth/login` - User login (POST)
  - `http://localhost:8080/api/auth/logout` - User logout (POST)
- **Rosters:**
  - `http://localhost:8080/api/rosters` - Get all rosters
  - `http://localhost:8080/api/rosters/new?flightNumber=TK001&databaseType=main` - Create new roster
  - `http://localhost:8080/api/rosters/flight/{flightNumber}` - Get roster by flight number
  - `http://localhost:8080/api/rosters/{id}` - Get roster by ID
  - `http://localhost:8080/api/rosters/{id}` - Delete roster
  - `http://localhost:8080/api/rosters/{id}/export` - Export roster as JSON
- **Crew Management:**
  - `http://localhost:8080/api/rosters/available-pilots` - Get available pilots
  - `http://localhost:8080/api/rosters/available-cabin-crew` - Get available cabin crew
  - `http://localhost:8080/api/rosters/{id}/assign-crew` - Assign crew to roster
- **Flight Info:**
  - `http://localhost:8080/api/rosters/flights` - Get all flights
  - `http://localhost:8080/api/rosters/flights/{flightNumber}` - Get flight by number

### **Flight Info Service (Port 8081):**
- **Flights:**
  - `http://localhost:8081/api/flights` - Get all flights
  - `http://localhost:8081/api/flights/{flightNumber}` - Get flight by number
  - `http://localhost:8081/api/flights/search?sourceAirport=IST&destinationAirport=JFK` - Search flights
  - `http://localhost:8081/api/flights` - Create flight
- **Airports:**
  - `http://localhost:8081/api/airports` - Get all airports
  - `http://localhost:8081/api/airports/{id}` - Get airport by ID
  - `http://localhost:8081/api/airports/code/{airportCode}` - Get airport by code
  - `http://localhost:8081/api/airports` - Create airport
- **Vehicle Types:**
  - `http://localhost:8081/api/vehicle-types` - Get all vehicle types
  - `http://localhost:8081/api/vehicle-types/{id}` - Get vehicle type by ID
  - `http://localhost:8081/api/vehicle-types` - Create vehicle type

### **Pilot Service (Port 8082):**
- **Pilots:**
  - `http://localhost:8082/api/pilots` - Get all pilots
  - `http://localhost:8082/api/pilots/available` - Get available pilots
  - `http://localhost:8082/api/pilots/{id}` - Get pilot by ID
  - `http://localhost:8082/api/pilots/pilot-id/{pilotId}` - Get pilot by pilot ID
  - `http://localhost:8082/api/pilots/vehicle-type/{vehicleType}` - Get pilots by vehicle type
  - `http://localhost:8082/api/pilots/seniority/{level}` - Get pilots by seniority level
  - `http://localhost:8082/api/pilots/{id}/availability?isAvailable=false` - Update pilot availability
  - `http://localhost:8082/api/pilots/bulk-availability?isAvailable=false` - Update multiple pilots availability

### **Cabin Service (Port 8083):**
- **Attendants:**
  - `http://localhost:8083/api/attendants` - Get all attendants
  - `http://localhost:8083/api/attendants/available` - Get available attendants
  - `http://localhost:8083/api/attendants/{id}` - Get attendant by ID
  - `http://localhost:8083/api/attendants/attendant-id/{attendantId}` - Get attendant by attendant ID
  - `http://localhost:8083/api/attendants/type/{type}` - Get attendants by type
  - `http://localhost:8083/api/attendants/chefs` - Get available chefs
  - `http://localhost:8083/api/attendants/chiefs` - Get available chiefs
  - `http://localhost:8083/api/attendants/regulars` - Get available regulars
  - `http://localhost:8083/api/attendants` - Create attendant
  - `http://localhost:8083/api/attendants/{id}` - Update attendant
  - `http://localhost:8083/api/attendants/{id}` - Delete attendant
  - `http://localhost:8083/api/attendants/by-ids` - Get attendants by IDs

### **Passenger Service (Port 8084):**
- **Passengers:**
  - `http://localhost:8084/api/passengers` - Get all passengers
  - `http://localhost:8084/api/passengers/flight/{flightId}` - Get passengers by flight ID
  - `http://localhost:8084/api/passengers/{id}` - Get passenger by ID
  - `http://localhost:8084/api/passengers/passenger-id/{passengerId}` - Get passenger by passenger ID
  - `http://localhost:8084/api/passengers/flight/{flightId}/seat-type/{seatType}` - Get passengers by flight and seat type

**Note:** Direct port access (e.g., http://localhost:8080) shows 404 error - this is normal! Use the API endpoints above.

### **🔐 Authentication:**
- **Main System:** JWT token required (auto-added by frontend)
- **Microservices:** No authentication required
- **API Key:** Not used in this project

**⚠️ Remember:** Keep Docker Desktop running while using the application!

#### **Manual Setup (Alternative):**

##### **Database Setup:**
1. **Install PostgreSQL** and create databases:
   ```sql
   CREATE DATABASE main_db;
   CREATE DATABASE flight_info_db;
   CREATE DATABASE pilot_db;
   CREATE DATABASE cabin_db;
   CREATE DATABASE passenger_db;
   ```

2. **Default PostgreSQL credentials:**
   - Username: `postgres`
   - Password: `1234`
   - Port: `5432`

##### **Start Services:**
```bash
# Main System (Port 8080)
cd main-system && mvn spring-boot:run

# Flight Info Service (Port 8081)
cd services/flight-info-service && mvn spring-boot:run

# Pilot Service (Port 8082)
cd services/pilot-service && mvn spring-boot:run

# Cabin Service (Port 8083)
cd services/cabin-service && mvn spring-boot:run

# Passenger Service (Port 8084)
cd services/passenger-service && mvn spring-boot:run

# Frontend (Port 3000)
cd frontend
npm install
npm start
```

## 🔧 **Troubleshooting**

### **Docker Compose Issues:**

#### **1. "docker-compose command not found":**
```bash
# Install Docker Desktop (includes Docker Compose)
# Or install via Chocolatey (Windows):
choco install docker-compose

# Or install via Scoop (Windows):
scoop install docker-compose
```

#### **2. "Docker daemon not running":**
```bash
# Start Docker Desktop application
# Make sure Docker Desktop is running in the background
# Check Docker Desktop status in system tray (Windows) or menu bar (macOS)

# Verify Docker is running:
docker --version
docker ps
```

#### **3. "Port already in use":**
```bash
# Check what's using the ports
docker ps
netstat -ano | findstr :3000
netstat -ano | findstr :8080

# Stop conflicting services or change ports in docker-compose.yml
```

#### **4. "Build failed" or "Image not found":**
```bash
# Clean up and rebuild
docker compose down
docker system prune -f
docker compose up --build -d
```

### **Manual Setup Issues:**

#### **1. "index.html not found" Error:**
```bash
# Make sure public folder exists
ls frontend/public/
# Should show: index.html, favicon.ico, manifest.json
```

#### **2. "Cannot find module" Error:**
```bash
# Delete node_modules and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
```

#### **3. Database Connection Error:**
```bash
# Check PostgreSQL is running
# Windows: Check Services
# macOS: brew services list | grep postgresql
# Linux: sudo systemctl status postgresql
```

## 🏗️ Architecture

### **Docker Compose Services:**
- **Frontend** (3000) - React web interface
- **Main System** (8080) - Central orchestration & roster management
- **Flight Info Service** (8081) - Flight data management
- **Pilot Service** (8082) - Pilot information & assignments
- **Cabin Service** (8083) - Cabin crew management
- **Passenger Service** (8084) - Passenger data

### **Databases:**
- **Flight Info DB** (5432) - Flight and airport data
- **Pilot DB** (5433) - Pilot information
- **Cabin DB** (5434) - Cabin crew data
- **Passenger DB** (5435) - Passenger information
- **Main DB** (5436) - Roster and user data

### **Technology Stack:**
- **Backend:** Spring Boot, Java 17, Maven
- **Frontend:** React 18, Material-UI, Axios
- **Database:** PostgreSQL 15
- **Containerization:** Docker, Docker Compose
- **Authentication:** JWT
- **Architecture:** Microservices

## 👤 User Management

**No default users exist.** You need to register a new user:

1. **Go to:** http://localhost:3000/register
2. **Create your account** with username, email, and password
3. **Choose role:** USER or ADMIN
4. **Login** with your credentials

**Note:** First registered user can be set as ADMIN for full access.

## 📋 Features

- Flight roster creation and management
- Crew assignment and scheduling
- Admin panel for flight and crew management
- Real-time seat mapping
- JWT-based authentication
- Responsive web interface
- Docker containerization
- Cross-platform support

## 🚀 Quick Commands

### **Docker Compose:**
```bash
# Start all services
docker compose up -d

# Stop all services
docker compose down

# View logs
docker compose logs

# Restart specific service
docker compose restart frontend
```

### **API Testing:**
```bash
# Test Main System API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","password":"123456"}'

# Test Flight Info API
curl http://localhost:8081/api/flights
curl http://localhost:8081/api/airports

# Test Pilot API
curl http://localhost:8082/api/pilots
curl http://localhost:8082/api/pilots/available

# Test Cabin Service API
curl http://localhost:8083/api/attendants
curl http://localhost:8083/api/attendants/available

# Test Passenger API
curl http://localhost:8084/api/passengers

# Test Roster Creation
curl "http://localhost:8080/api/rosters/new?flightNumber=TK001&databaseType=main"
```

### **Development:**
```bash
# Check container status
docker ps

# Access container shell
docker exec -it frontend sh

# View service logs
docker compose logs main-system
```

## 🚨 Troubleshooting

### **Roster Creation Issues:**

#### **"No pilots assigned to roster" or "Failed to create roster"**
- **Problem:** Pilots are not available (`isAvailable: false`)
- **Solution:** Reset pilot availability:
```bash
# Check pilot availability
curl http://localhost:8082/api/pilots/available

# If empty or few pilots, reset all pilots to available
curl -X PUT "http://localhost:8082/api/pilots/bulk-availability?isAvailable=true" \
  -H "Content-Type: application/json" \
  -d "[1,3,5,7,9,11,12]"

# Verify pilots are now available
curl http://localhost:8082/api/pilots/available
```

#### **"Flight not found" when creating roster**
- **Problem:** Flight doesn't exist in Flight Info Service
- **Solution:** Check if flight exists:
```bash
# List all flights
curl http://localhost:8081/api/flights

# If flight missing, it might be frontend-only (localStorage)
# Use existing flights like TK001, TK002, etc.
```

### **API Access Issues:**

#### **"404 Whitelabel Error Page" on direct port access**
- **This is NORMAL!** ✅
- **Solution:** Use proper API endpoints:
  - ❌ `http://localhost:8080/` (404 - normal)
  - ✅ `http://localhost:8080/api/auth/register` (POST - correct)
  - ✅ `http://localhost:8081/api/flights` (GET - correct)

#### **"Erişim reddedildi" / "Access denied" in browser**
- **Problem:** API endpoints require POST method, browser sends GET
- **This is NORMAL!** ✅
- **Solution:** Use proper tools:
  - ✅ **Frontend:** http://localhost:3000 (use the web interface)
  - ✅ **API Testing:** Use curl/Postman
  - ❌ **Browser:** Don't access API endpoints directly

#### **"Username is already taken" error**
- **Problem:** User already exists in database
- **Solution:** Use different username or check existing users:
```bash
# Check if user exists
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","email":"new@test.com","password":"123456"}'
```

#### **"Connection refused" on API endpoints**
- **Solution:** Check if services are running: `docker ps`
- **Check:** Verify Docker Desktop is running
- **Restart:** `docker compose restart [service-name]`

### **Docker Compose Issues:**

#### **"docker-compose: command not found"**
- **Solution:** Use `docker compose` (V2) instead of `docker-compose` (V1)

#### **"Docker daemon is not running"**
- **Solution:** Start Docker Desktop

#### **"no configuration file provided: not found"**
- **Solution:** Make sure you're in the `project-root` directory

#### **Port conflicts**
- **Solution:** Stop conflicting services or change ports in `docker-compose.yml`

#### **Build failures**
- **Solution:** Clean and rebuild: `docker compose down && docker compose up --build`
