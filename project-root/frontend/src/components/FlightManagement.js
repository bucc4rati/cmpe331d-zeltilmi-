import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  IconButton,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  ArrowBack,
  Add,
  Edit,
  Delete,
  FlightTakeoff,
  Search,
  Refresh
} from '@mui/icons-material';
import { flightAPI } from '../services/api';
import axios from 'axios';

const FlightManagement = () => {
  const navigate = useNavigate();
  const [flights, setFlights] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingFlight, setEditingFlight] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);
  const [loadingFlights, setLoadingFlights] = useState(true);
  const [airports, setAirports] = useState([]);
  const [vehicleTypes, setVehicleTypes] = useState([]);
  const [loadingFormData, setLoadingFormData] = useState(true);
  
  const [formData, setFormData] = useState({
    number: '',
    route: '',
    destination: '',
    origin: 'Istanbul',
    aircraft: 'Boeing 737-800',
    capacity: 189,
    flightDate: '',
    durationMinutes: 90,
    distanceKm: 350
  });

  useEffect(() => {
    loadFormData();
    loadFlights(); // loadFlights will call checkRosterStatus internally
  }, []);

  const loadFormData = async () => {
    try {
      setLoadingFormData(true);
      const [airportsRes, vehicleTypesRes] = await Promise.all([
        flightAPI.getAllAirports(),
        flightAPI.getAllVehicleTypes()
      ]);
      setAirports(airportsRes.data);
      setVehicleTypes(vehicleTypesRes.data);
    } catch (err) {
      console.error('Error loading form data:', err);
    } finally {
      setLoadingFormData(false);
    }
  };

  const loadFlights = async () => {
    try {
      setLoadingFlights(true);
      setError(null); // Clear previous errors
      
      console.log('Loading flights from backend...');
      // Load flights from backend
      const response = await flightAPI.getAllFlights();
      console.log('Response received:', response);
      console.log('Response data:', response.data);
      
      // Handle both array and object responses
      const backendFlights = Array.isArray(response.data) 
        ? response.data 
        : (response.data?.data || response.data || []);
      
      console.log('Backend flights:', backendFlights);
      
      if (!Array.isArray(backendFlights) || backendFlights.length === 0) {
        console.warn('No flights found or invalid response format');
        setFlights([]);
        return;
      }
      
      // Convert backend format to frontend format
      const formattedFlights = backendFlights.map((flight, index) => ({
        id: index + 1,
        number: flight.flightNumber,
        route: `${flight.sourceAirport?.city || flight.sourceAirport?.airportName || 'Unknown'} → ${flight.destinationAirport?.city || flight.destinationAirport?.airportName || 'Unknown'}`,
        destination: flight.destinationAirport?.city || flight.destinationAirport?.airportName || 'Unknown',
        origin: flight.sourceAirport?.city || flight.sourceAirport?.airportName || 'Unknown',
        aircraft: flight.vehicleType?.typeName || 'Unknown',
        capacity: flight.vehicleType?.totalSeats || 0,
        flightDate: flight.flightDate,
        durationMinutes: flight.durationMinutes,
        distanceKm: flight.distanceKm,
        hasRosters: false // Default false, will be updated by checkRosterStatus
      }));
      
      console.log('Formatted flights:', formattedFlights);
      setFlights(formattedFlights);
      
      // Update localStorage for backward compatibility
      localStorage.setItem('flights', JSON.stringify(formattedFlights));
      
      // Check roster status AFTER flights are loaded
      await checkRosterStatus();
    } catch (err) {
      console.error('Error loading flights from backend:', err);
      console.error('Error details:', {
        message: err.message,
        response: err.response,
        status: err.response?.status,
        data: err.response?.data
      });
      
      const errorMessage = err.response?.data?.message 
        || err.response?.data 
        || err.message 
        || 'Failed to load flights from backend. Please check if the Flight Info Service is running.';
      
      setError(errorMessage);
      
      // Fallback to localStorage if backend fails
      const savedFlights = localStorage.getItem('flights');
      if (savedFlights) {
        try {
          const parsed = JSON.parse(savedFlights);
          setFlights(parsed);
          console.log('Loaded flights from localStorage as fallback');
        } catch (parseErr) {
          console.error('Error parsing localStorage flights:', parseErr);
        }
      }
    } finally {
      setLoadingFlights(false);
    }
  };

  const checkRosterStatus = async () => {
    try {
      // Get all rosters from backend
      const response = await fetch('http://localhost:8080/api/rosters');
      if (response.ok) {
        const rosters = await response.json();
        console.log('All rosters:', rosters);
        
        // Filter only active rosters (isActive = true or undefined/null)
        const activeRosters = rosters.filter(roster => roster.isActive !== false);
        console.log('Active rosters:', activeRosters);
        
        const flightsWithRosters = activeRosters.map(roster => roster.flightNumber);
        console.log('Flight numbers with rosters:', flightsWithRosters);
        
        // Update flights state with hasRosters flag
        setFlights(prevFlights => {
          const updated = prevFlights.map(flight => {
            const hasRosters = flightsWithRosters.includes(flight.number);
            console.log(`Flight ${flight.number}: hasRosters = ${hasRosters}`);
            return {
              ...flight,
              hasRosters: hasRosters
            };
          });
          console.log('Updated flights:', updated);
          return updated;
        });
      } else {
        console.error('Failed to fetch rosters, status:', response.status);
      }
    } catch (err) {
      console.error('Error checking roster status:', err);
    }
  };

  const saveFlights = (updatedFlights) => {
    localStorage.setItem('flights', JSON.stringify(updatedFlights));
    setFlights(updatedFlights);
  };

  const handleOpenDialog = (flight = null) => {
    if (flight) {
      // Double check: if flight has rosters, don't allow editing
      if (flight.hasRosters) {
        setError('Cannot edit flight with existing rosters! Please delete the roster first.');
        setTimeout(() => setError(null), 3000);
        return;
      }
      
      setEditingFlight(flight);
      // Convert frontend format to form data format
      const flightDateValue = flight.flightDate 
        ? new Date(flight.flightDate).toISOString().slice(0, 16) // Convert to datetime-local format
        : '';
      
      setFormData({
        number: flight.number || '',
        route: flight.route || '',
        destination: flight.destination || '',
        origin: flight.origin || 'Istanbul',
        aircraft: flight.aircraft || 'Boeing 737-800',
        capacity: flight.capacity || 189,
        flightDate: flightDateValue,
        durationMinutes: flight.durationMinutes || 90,
        distanceKm: flight.distanceKm || 350
      });
    } else {
      setEditingFlight(null);
      setFormData({
        number: '',
        route: '',
        destination: '',
        origin: 'Istanbul',
        aircraft: 'Boeing 737-800',
        capacity: 189,
        flightDate: '',
        durationMinutes: 90,
        distanceKm: 350
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingFlight(null);
    setError(null);
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.number || !formData.destination || !formData.origin) {
      setError('Flight number, origin, and destination are required');
      return;
    }

    try {
      setError(null);
      
      // Find source airport by city name (case-insensitive, trim whitespace)
      const originCity = formData.origin.trim();
      const sourceAirport = airports.find(a => 
        a.city.toLowerCase().trim() === originCity.toLowerCase()
      );
      if (!sourceAirport) {
        const availableCities = Array.from(new Set(airports.map(a => a.city))).slice(0, 10).join(', ');
        setError(`Airport not found for origin city: "${originCity}". Available cities: ${availableCities}...`);
        return;
      }

      // Find destination airport by city name (case-insensitive, trim whitespace)
      const destCity = formData.destination.trim();
      const destAirport = airports.find(a => 
        a.city.toLowerCase().trim() === destCity.toLowerCase()
      );
      if (!destAirport) {
        const availableCities = Array.from(new Set(airports.map(a => a.city))).slice(0, 10).join(', ');
        setError(`Airport not found for destination city: "${destCity}". Available cities: ${availableCities}...`);
        return;
      }

      // Find vehicle type by name
      const vehicleType = vehicleTypes.find(v => 
        v.typeName === formData.aircraft
      );
      if (!vehicleType) {
        setError(`Vehicle type not found: ${formData.aircraft}`);
        return;
      }

      // Prepare flight data for backend
      const flightDate = formData.flightDate 
        ? new Date(formData.flightDate).toISOString()
        : new Date().toISOString();

      // Spring Boot nested object'leri parse edebilir, ama bazen ID'ler daha güvenli
      // Önce full object ile deneyelim, çalışmazsa sadece ID göndeririz
      const backendFlightData = {
        flightNumber: formData.number,
        flightDate: flightDate,
        durationMinutes: formData.durationMinutes || 90,
        distanceKm: formData.distanceKm || 350,
        // Full object gönderiyoruz - Spring Boot bunu parse edebilmeli
        sourceAirport: {
          id: sourceAirport.id,
          airportCode: sourceAirport.airportCode,
          airportName: sourceAirport.airportName,
          city: sourceAirport.city,
          country: sourceAirport.country
        },
        destinationAirport: {
          id: destAirport.id,
          airportCode: destAirport.airportCode,
          airportName: destAirport.airportName,
          city: destAirport.city,
          country: destAirport.country
        },
        vehicleType: {
          id: vehicleType.id,
          typeName: vehicleType.typeName,
          totalSeats: vehicleType.totalSeats,
          businessSeats: vehicleType.businessSeats,
          economySeats: vehicleType.economySeats,
          maxPilots: vehicleType.maxPilots,
          maxCabinCrew: vehicleType.maxCabinCrew
        },
        isSharedFlight: false
      };

      console.log('Submitting flight data:', backendFlightData);

      if (editingFlight) {
        // Update existing flight
        console.log('Updating flight:', editingFlight.number);
        const response = await flightAPI.updateFlight(editingFlight.number, backendFlightData);
        console.log('Update response:', response);
        setSuccess('Flight updated successfully!');
      } else {
        // Create new flight
        console.log('Creating new flight');
        const response = await flightAPI.createFlight(backendFlightData);
        console.log('Create response:', response);
        setSuccess('Flight added successfully!');
      }

      // Reload flights from backend
      await loadFlights();
      await checkRosterStatus();
      
      handleCloseDialog();
      setTimeout(() => setSuccess(null), 3000);
    } catch (err) {
      console.error('Error saving flight:', err);
      console.error('Error response:', err.response);
      console.error('Error data:', err.response?.data);
      const errorMessage = err.response?.data?.message 
        || err.response?.data 
        || err.message 
        || 'Failed to save flight. Please check console for details.';
      setError(errorMessage);
    }
  };

  const handleDelete = async (flightNumber) => {
    // Double check: if flight has rosters, don't allow deletion
    const flight = flights.find(f => f.number === flightNumber);
    if (flight && flight.hasRosters) {
      setError('Cannot delete flight with existing rosters! Please delete the roster first.');
      setTimeout(() => setError(null), 3000);
      return;
    }
    
    if (window.confirm('Are you sure you want to delete this flight?')) {
      try {
        console.log('Deleting flight:', flightNumber);
        const response = await flightAPI.deleteFlight(flightNumber);
        console.log('Delete response:', response);
        setSuccess('Flight deleted successfully!');
        // Reload flights from backend
        await loadFlights();
        await checkRosterStatus();
        setTimeout(() => setSuccess(null), 3000);
      } catch (err) {
        console.error('Error deleting flight:', err);
        console.error('Error response:', err.response);
        const errorMessage = err.response?.data?.message 
          || err.response?.data 
          || err.message 
          || 'Failed to delete flight. Please check console for details.';
        setError(errorMessage);
      }
    }
  };

  const filteredFlights = flights.filter(flight =>
    searchQuery === '' ||
    flight.number.toLowerCase().includes(searchQuery.toLowerCase()) ||
    flight.route.toLowerCase().includes(searchQuery.toLowerCase()) ||
    flight.destination.toLowerCase().includes(searchQuery.toLowerCase()) ||
    flight.aircraft.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <Box>
      {/* Header */}
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/')}
            sx={{ mr: 2 }}
          >
            Back
          </Button>
          <Typography variant="h4">
            Flight Management
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={async () => {
              await loadFlights(); // loadFlights will call checkRosterStatus internally
              setSuccess('Flights refreshed!');
              setTimeout(() => setSuccess(null), 2000);
            }}
            disabled={loadingFlights}
          >
            Refresh
          </Button>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => handleOpenDialog()}
          >
            Add New Flight
          </Button>
        </Box>
      </Box>

      {/* Success/Error Messages */}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {/* Search Bar */}
      <TextField
        fullWidth
        placeholder="Search flights by number, route, destination, or aircraft..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        InputProps={{
          startAdornment: <Search sx={{ mr: 1, color: 'text.secondary' }} />
        }}
        sx={{ mb: 3 }}
      />

      {/* Loading State */}
      {loadingFlights && (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      )}

      {/* Flight Cards */}
      {!loadingFlights && (
      <Grid container spacing={2}>
        {filteredFlights.map((flight) => (
          <Grid item xs={12} sm={6} md={4} key={flight.id}>
            <Card>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
                  <Box sx={{ flexGrow: 1 }}>
                    <Typography variant="h6" gutterBottom>
                      {flight.number}
                    </Typography>
                    <Typography color="textSecondary" gutterBottom>
                      {flight.route}
                    </Typography>
                    <Typography variant="body2" sx={{ mt: 1 }}>
                      Aircraft: {flight.aircraft}
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      Capacity: {flight.capacity} passengers
                    </Typography>
                    {flight.hasRosters && (
                      <Chip 
                        label="Has Rosters" 
                        color="warning" 
                        size="small" 
                        sx={{ mt: 1 }}
                      />
                    )}
                  </Box>
                  <Box>
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        if (flight.hasRosters) {
                          e.stopPropagation();
                          setError('Cannot edit flight with existing rosters! Please delete the roster first.');
                          setTimeout(() => setError(null), 3000);
                          return;
                        }
                        handleOpenDialog(flight);
                      }}
                      color="primary"
                      disabled={flight.hasRosters}
                      title={flight.hasRosters ? "Cannot edit flight with existing rosters" : "Edit flight"}
                    >
                      <Edit />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={(e) => {
                        if (flight.hasRosters) {
                          e.stopPropagation();
                          setError('Cannot delete flight with existing rosters! Please delete the roster first.');
                          setTimeout(() => setError(null), 3000);
                          return;
                        }
                        handleDelete(flight.number);
                      }}
                      color="error"
                      disabled={flight.hasRosters}
                      title={flight.hasRosters ? "Cannot delete flight with existing rosters" : "Delete flight"}
                    >
                      <Delete />
                    </IconButton>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      )}

      {!loadingFlights && filteredFlights.length === 0 && (
        <Alert severity="info" sx={{ mt: 2 }}>
          No flights found matching your search criteria.
        </Alert>
      )}

      {/* Add/Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          {editingFlight ? 'Edit Flight' : 'Add New Flight'}
        </DialogTitle>
        <DialogContent>
          {loadingFormData ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : (
          <Box sx={{ pt: 2 }}>
            <TextField
              fullWidth
              label="Flight Number"
              value={formData.number}
              onChange={(e) => setFormData({ ...formData, number: e.target.value.toUpperCase() })}
              placeholder="e.g., TK013"
              sx={{ mb: 2 }}
              required
              disabled={!!editingFlight}
              helperText={editingFlight ? "Flight number cannot be changed" : "Format: AANNNN (e.g., TK001)"}
            />
            
            <TextField
              fullWidth
              label="Origin City"
              value={formData.origin}
              onChange={(e) => setFormData({ ...formData, origin: e.target.value })}
              placeholder="e.g., Istanbul"
              sx={{ mb: 2 }}
              required
              helperText="Enter the origin city name"
            />
            
            <TextField
              fullWidth
              label="Destination City"
              value={formData.destination}
              onChange={(e) => setFormData({ ...formData, destination: e.target.value })}
              placeholder="e.g., Ankara"
              sx={{ mb: 2 }}
              required
              helperText="Enter the destination city name"
            />

            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Aircraft Type</InputLabel>
              <Select
                value={formData.aircraft}
                label="Aircraft Type"
                onChange={(e) => {
                  const selected = vehicleTypes.find(v => v.typeName === e.target.value);
                  setFormData({
                    ...formData,
                    aircraft: e.target.value,
                    capacity: selected?.totalSeats || formData.capacity
                  });
                }}
                disabled={loadingFormData}
              >
                {vehicleTypes.map((type) => (
                  <MenuItem key={type.id} value={type.typeName}>
                    {type.typeName} ({type.totalSeats} seats)
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <TextField
              fullWidth
              label="Flight Date & Time"
              type="datetime-local"
              value={formData.flightDate}
              onChange={(e) => setFormData({ ...formData, flightDate: e.target.value })}
              sx={{ mb: 2 }}
              InputLabelProps={{ shrink: true }}
            />

            <TextField
              fullWidth
              label="Duration (minutes)"
              type="number"
              value={formData.durationMinutes}
              onChange={(e) => setFormData({ ...formData, durationMinutes: parseInt(e.target.value) || 90 })}
              sx={{ mb: 2 }}
            />

            <TextField
              fullWidth
              label="Distance (km)"
              type="number"
              value={formData.distanceKm}
              onChange={(e) => setFormData({ ...formData, distanceKm: parseFloat(e.target.value) || 350 })}
              sx={{ mb: 2 }}
            />
          </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained" disabled={loadingFormData}>
            {editingFlight ? 'Update' : 'Add'} Flight
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default FlightManagement;


