import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to include JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle authentication errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid, redirect to login
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const rosterAPI = {
  // Get all rosters
  getAllRosters: () => api.get('/rosters'),
  
  // Get roster by ID
  getRosterById: (id) => api.get(`/rosters/${id}`),
  
  // Get roster by flight number
  getRosterByFlightNumber: (flightNumber) => api.get(`/rosters/flight/${flightNumber}`),
  
  // Create new roster
  createRoster: (flightNumber, databaseType) => 
    api.get(`/rosters/new?flightNumber=${flightNumber}&databaseType=${databaseType}`),
  
  // Delete roster
  deleteRoster: (id) => api.delete(`/rosters/${id}`),
  
  // Export roster as JSON
  exportRoster: (id) => api.get(`/rosters/${id}/export`, { responseType: 'blob' }),
};

export const healthAPI = {
  // Check main system health
  checkHealth: () => api.get('/health'),
};

export const authAPI = {
  // Login
  login: (credentials) => api.post('/auth/login', credentials),
  
  // Register
  register: (userData) => api.post('/auth/register', userData),
  
  // Logout
  logout: () => api.post('/auth/logout'),
};

export const crewAPI = {
  // Get available pilots
  getAvailablePilots: () => api.get('/rosters/available-pilots'),
  
  // Get available cabin crew
  getAvailableCabinCrew: () => api.get('/rosters/available-cabin-crew'),
  
  // Assign crew manually
  assignCrewManually: (rosterId, crewData) => 
    api.post(`/rosters/${rosterId}/assign-crew`, crewData),
};

// Direct Pilot Service API (port 8082)
export const pilotServiceAPI = {
  // Get all pilots
  getAllPilots: () => axios.get('http://localhost:8082/api/pilots'),
  
  // Create pilot
  createPilot: (pilotData) => axios.post('http://localhost:8082/api/pilots', pilotData),
  
  // Update pilot
  updatePilot: (id, pilotData) => axios.put(`http://localhost:8082/api/pilots/${id}`, pilotData),
  
  // Delete pilot
  deletePilot: (id) => axios.delete(`http://localhost:8082/api/pilots/${id}`),
};

// Direct Cabin Service API (port 8083)
export const cabinServiceAPI = {
  // Get all attendants
  getAllAttendants: () => axios.get('http://localhost:8083/api/attendants'),
  
  // Create attendant
  createAttendant: (attendantData) => axios.post('http://localhost:8083/api/attendants', attendantData),
  
  // Update attendant
  updateAttendant: (id, attendantData) => axios.put(`http://localhost:8083/api/attendants/${id}`, attendantData),
  
  // Delete attendant
  deleteAttendant: (id) => axios.delete(`http://localhost:8083/api/attendants/${id}`),
};

// Direct Flight Info Service API (port 8081)
const flightInfoApi = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add JWT token to Flight Info Service requests if available
flightInfoApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwtToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const flightAPI = {
  // Get all flights from flight info service (via main-system proxy)
  getAllFlights: () => api.get('/rosters/flights'),
  
  // Get flight by number (via main-system proxy)
  getFlightByNumber: (flightNumber) => api.get(`/rosters/flights/${flightNumber}`),
  
  // Get all airports from flight info service
  getAllAirports: () => flightInfoApi.get('/airports'),
  
  // Get all vehicle types from flight info service
  getAllVehicleTypes: () => flightInfoApi.get('/vehicle-types'),
  
  // Create new flight (direct to flight info service)
  createFlight: (flightData) => flightInfoApi.post('/flights', flightData),
  
  // Update flight (direct to flight info service)
  updateFlight: (flightNumber, flightData) => flightInfoApi.put(`/flights/${flightNumber}`, flightData),
  
  // Delete flight (direct to flight info service)
  deleteFlight: (flightNumber) => flightInfoApi.delete(`/flights/${flightNumber}`),
};

export default api;
