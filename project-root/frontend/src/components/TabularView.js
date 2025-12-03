import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Alert,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Grid,
  Tabs,
  Tab
} from '@mui/material';
import {
  ArrowBack,
  FlightTakeoff,
  People,
  Assignment,
  Person
} from '@mui/icons-material';
import { rosterAPI } from '../services/api';

const TabularView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [roster, setRoster] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState(0);

  useEffect(() => {
    fetchRoster();
  }, [id]); // eslint-disable-line react-hooks/exhaustive-deps

  const fetchRoster = async () => {
    try {
      setLoading(true);
      const response = await rosterAPI.getRosterById(id);
      setRoster(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch roster details.');
      console.error('Error fetching roster:', err);
    } finally {
      setLoading(false);
    }
  };

  const getRosterData = () => {
    if (!roster || !roster.rosterData) return null;
    try {
      return JSON.parse(roster.rosterData);
    } catch (e) {
      return null;
    }
  };

  const renderPersonTable = (persons, title, columns) => {
    if (!persons || persons.length === 0) {
      return (
        <Alert severity="info">
          No {title.toLowerCase()} found.
        </Alert>
      );
    }

    return (
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell key={column.key} align={column.align || 'left'}>
                  {column.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {persons.map((person, index) => (
              <TableRow key={index} hover>
                {columns.map((column) => (
                  <TableCell key={column.key} align={column.align || 'left'}>
                    {column.render ? column.render(person) : person[column.key]}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
        <Button startIcon={<ArrowBack />} onClick={() => navigate('/')}>
          Back to Dashboard
        </Button>
      </Box>
    );
  }

  if (!roster) {
    return (
      <Box>
        <Alert severity="warning" sx={{ mb: 3 }}>
          Roster not found.
        </Alert>
        <Button startIcon={<ArrowBack />} onClick={() => navigate('/')}>
          Back to Dashboard
        </Button>
      </Box>
    );
  }

  const rosterData = getRosterData();
  const flight = rosterData?.flight;
  const pilots = rosterData?.pilots || [];
  const cabinCrew = rosterData?.cabinCrew || [];
  const passengers = rosterData?.passengers || [];

  const pilotColumns = [
    { key: 'name', label: 'Name' },
    { key: 'age', label: 'Age' },
    { key: 'gender', label: 'Gender' },
    { key: 'nationality', label: 'Nationality' },
    { key: 'seniorityLevel', label: 'Level' },
    { key: 'vehicleRestriction', label: 'Aircraft' },
    { key: 'maxDistanceKm', label: 'Max Distance (km)' }
  ];

  const cabinColumns = [
    { key: 'name', label: 'Name' },
    { key: 'age', label: 'Age' },
    { key: 'gender', label: 'Gender' },
    { key: 'nationality', label: 'Nationality' },
    { key: 'attendantType', label: 'Type' },
    { key: 'vehicleRestrictions', label: 'Aircraft' },
    { key: 'recipeTypes', label: 'Specialties' }
  ];

  const passengerColumns = [
    { key: 'name', label: 'Name' },
    { key: 'age', label: 'Age' },
    { key: 'gender', label: 'Gender' },
    { key: 'nationality', label: 'Nationality' },
    { key: 'seatType', label: 'Class' },
    { key: 'seatNumber', label: 'Seat' },
    { 
      key: 'isInfant', 
      label: 'Type',
      render: (person) => (
        <Chip 
          label={person.isInfant ? 'Infant' : 'Adult'} 
          color={person.isInfant ? 'warning' : 'default'}
          size="small"
        />
      )
    }
  ];

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate(`/rosters/${id}`)}
            sx={{ mr: 2 }}
          >
            Back to Detail
          </Button>
          <Typography variant="h4">
            Tabular View: {roster.flightNumber}
          </Typography>
        </Box>
      </Box>

      {/* Flight Information */}
      {flight && (
        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Flight Information
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography><strong>Flight:</strong> {flight.flightNumber}</Typography>
                <Typography><strong>Date:</strong> {new Date(flight.flightDate).toLocaleString()}</Typography>
                <Typography><strong>Duration:</strong> {flight.durationMinutes} minutes</Typography>
                <Typography><strong>Distance:</strong> {flight.distanceKm} km</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography><strong>Route:</strong> {flight.sourceAirport?.airportCode} → {flight.destinationAirport?.airportCode}</Typography>
                <Typography><strong>Aircraft:</strong> {flight.vehicleType?.typeName}</Typography>
                <Typography><strong>Total Seats:</strong> {flight.vehicleType?.totalSeats}</Typography>
                <Typography><strong>Database:</strong> {roster.databaseType}</Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      )}

      {/* Tabs for different views */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={activeTab} onChange={(e, newValue) => setActiveTab(newValue)}>
          <Tab 
            icon={<Assignment />} 
            label={`Pilots (${pilots.length})`} 
            iconPosition="start"
          />
          <Tab 
            icon={<People />} 
            label={`Cabin Crew (${cabinCrew.length})`} 
            iconPosition="start"
          />
          <Tab 
            icon={<Person />} 
            label={`Passengers (${passengers.length})`} 
            iconPosition="start"
          />
        </Tabs>
      </Box>

      {/* Tab Content */}
      {activeTab === 0 && (
        <Box>
          <Typography variant="h6" gutterBottom>
            Pilots ({pilots.length})
          </Typography>
          {renderPersonTable(pilots, 'Pilots', pilotColumns)}
        </Box>
      )}

      {activeTab === 1 && (
        <Box>
          <Typography variant="h6" gutterBottom>
            Cabin Crew ({cabinCrew.length})
          </Typography>
          {renderPersonTable(cabinCrew, 'Cabin Crew', cabinColumns)}
        </Box>
      )}

      {activeTab === 2 && (
        <Box>
          <Typography variant="h6" gutterBottom>
            Passengers ({passengers.length})
          </Typography>
          {renderPersonTable(passengers, 'Passengers', passengerColumns)}
        </Box>
      )}
    </Box>
  );
};

export default TabularView;




