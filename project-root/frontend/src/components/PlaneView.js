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
  Grid,
  Paper,
  Chip,
  Tooltip
} from '@mui/material';
import {
  ArrowBack,
  FlightTakeoff,
  Person,
  BusinessCenter,
  ChildCare
} from '@mui/icons-material';
import { rosterAPI } from '../services/api';

const PlaneView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [roster, setRoster] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

  const generateSeatMap = (vehicleType, passengers) => {
    if (!vehicleType) return null;

    try {
      const seatingPlan = JSON.parse(vehicleType.seatingPlan);
      const rows = seatingPlan.rows || 30;
      const seatsPerRow = seatingPlan.seatsPerRow || 6;
      const businessRows = seatingPlan.businessRows || 3;

      const seatMap = [];
      const seatLetters = ['A', 'B', 'C', 'D', 'E', 'F'];

      // Create passenger map for quick lookup
      const passengerMap = {};
      passengers.forEach(passenger => {
        if (passenger.seatNumber) {
          passengerMap[passenger.seatNumber] = passenger;
        }
      });

      for (let row = 1; row <= rows; row++) {
        const seatRow = [];
        for (let seat = 0; seat < seatsPerRow; seat++) {
          const seatNumber = `${row}${seatLetters[seat]}`;
          const passenger = passengerMap[seatNumber];
          const isBusiness = row <= businessRows;
          
          seatRow.push({
            seatNumber,
            passenger,
            isBusiness,
            isEmpty: !passenger
          });
        }
        seatMap.push(seatRow);
      }

      return seatMap;
    } catch (e) {
      console.error('Error parsing seating plan:', e);
      return null;
    }
  };

  const getSeatColor = (seat) => {
    if (seat.passenger) {
      if (seat.passenger.isInfant) return '#ff9800'; // Orange for infants
      if (seat.passenger.seatType === 'BUSINESS') return '#4caf50'; // Green for business
      return '#2196f3'; // Blue for economy
    }
    if (seat.isBusiness) return '#e0e0e0'; // Light gray for empty business
    return '#f5f5f5'; // Very light gray for empty economy
  };

  const getSeatIcon = (seat) => {
    if (seat.passenger) {
      if (seat.passenger.isInfant) return <ChildCare />;
      if (seat.passenger.seatType === 'BUSINESS') return <BusinessCenter />;
      return <Person />;
    }
    return null;
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
  const seatMap = generateSeatMap(flight?.vehicleType, passengers);

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
            Plane View: {roster.flightNumber}
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

      {/* Legend */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Seat Legend
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={3}>
              <Box display="flex" alignItems="center">
                <Box
                  sx={{
                    width: 30,
                    height: 30,
                    backgroundColor: '#4caf50',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    mr: 1,
                    borderRadius: 1
                  }}
                >
                  <BusinessCenter fontSize="small" />
                </Box>
                <Typography>Business Class</Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box display="flex" alignItems="center">
                <Box
                  sx={{
                    width: 30,
                    height: 30,
                    backgroundColor: '#2196f3',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    mr: 1,
                    borderRadius: 1
                  }}
                >
                  <Person fontSize="small" />
                </Box>
                <Typography>Economy Class</Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box display="flex" alignItems="center">
                <Box
                  sx={{
                    width: 30,
                    height: 30,
                    backgroundColor: '#ff9800',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    mr: 1,
                    borderRadius: 1
                  }}
                >
                  <ChildCare fontSize="small" />
                </Box>
                <Typography>Infant</Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Box display="flex" alignItems="center">
                <Box
                  sx={{
                    width: 30,
                    height: 30,
                    backgroundColor: '#e0e0e0',
                    border: '1px solid #ccc',
                    mr: 1,
                    borderRadius: 1
                  }}
                />
                <Typography>Empty Seat</Typography>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Crew Zones (Cockpit & Crew Areas) */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Crew Layout
          </Typography>
          <Grid container spacing={2}>
            {/* Cockpit */}
            <Grid item xs={12} md={4}>
              <Typography variant="subtitle2" gutterBottom>
                Cockpit (Pilots)
              </Typography>
              <Paper elevation={0} sx={{ p: 2, border: '1px dashed #90caf9', background: '#e3f2fd', borderRadius: 1 }}>
                {pilots.length === 0 ? (
                  <Typography color="textSecondary">No pilots assigned</Typography>
                ) : (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {pilots.slice(0, 2).map((p, idx) => (
                      <Chip key={idx} color="primary" variant="filled" icon={<FlightTakeoff />} label={p.name || `${p.firstName || ''} ${p.lastName || ''}`.trim()} />
                    ))}
                  </Box>
                )}
              </Paper>
            </Grid>

            {/* Front Galley / Jumpseats */}
            <Grid item xs={12} md={4}>
              <Typography variant="subtitle2" gutterBottom>
                Front Crew Area
              </Typography>
              <Paper elevation={0} sx={{ p: 2, border: '1px dashed #c8e6c9', background: '#e8f5e9', borderRadius: 1 }}>
                {cabinCrew.length === 0 ? (
                  <Typography color="textSecondary">No cabin crew assigned</Typography>
                ) : (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {cabinCrew.slice(0, Math.ceil(cabinCrew.length / 2)).map((c, idx) => (
                      <Chip key={idx} color="success" variant="outlined" label={(c.name || `${c.firstName || ''} ${c.lastName || ''}`).trim()} />
                    ))}
                  </Box>
                )}
              </Paper>
            </Grid>

            {/* Rear Galley / Jumpseats */}
            <Grid item xs={12} md={4}>
              <Typography variant="subtitle2" gutterBottom>
                Rear Crew Area
              </Typography>
              <Paper elevation={0} sx={{ p: 2, border: '1px dashed #c8e6c9', background: '#e8f5e9', borderRadius: 1 }}>
                {cabinCrew.length === 0 ? (
                  <Typography color="textSecondary">No cabin crew assigned</Typography>
                ) : (
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {cabinCrew.slice(Math.ceil(cabinCrew.length / 2)).map((c, idx) => (
                      <Chip key={idx} color="success" variant="outlined" label={(c.name || `${c.firstName || ''} ${c.lastName || ''}`).trim()} />
                    ))}
                  </Box>
                )}
              </Paper>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Seat Map */}
      {seatMap ? (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Seat Map - {flight?.vehicleType?.typeName}
            </Typography>
            <Box sx={{ overflowX: 'auto' }}>
              <Box sx={{ display: 'inline-block', minWidth: '100%' }}>
                {seatMap.map((row, rowIndex) => (
                  <Box key={rowIndex} sx={{ display: 'flex', mb: 0.5, justifyContent: 'center' }}>
                    <Typography
                      variant="caption"
                      sx={{
                        width: 30,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        mr: 1,
                        fontWeight: 'bold'
                      }}
                    >
                      {rowIndex + 1}
                    </Typography>
                    {row.map((seat, seatIndex) => (
                      <Tooltip
                        key={seatIndex}
                        title={
                          seat.passenger
                            ? `${seat.passenger.name} (${seat.passenger.seatType})`
                            : `Seat ${seat.seatNumber} - ${seat.isBusiness ? 'Business' : 'Economy'}`
                        }
                        arrow
                      >
                        <Box
                          sx={{
                            width: 40,
                            height: 40,
                            backgroundColor: getSeatColor(seat),
                            border: '1px solid #ccc',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            mr: 0.5,
                            borderRadius: 1,
                            cursor: 'pointer',
                            '&:hover': {
                              opacity: 0.8
                            }
                          }}
                        >
                          {getSeatIcon(seat)}
                        </Box>
                      </Tooltip>
                    ))}
                  </Box>
                ))}
              </Box>
            </Box>
          </CardContent>
        </Card>
      ) : (
        <Alert severity="warning">
          Unable to generate seat map. Seating plan data may be missing or invalid.
        </Alert>
      )}

      {/* Statistics */}
      <Grid container spacing={2} sx={{ mt: 3 }}>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Passengers
              </Typography>
              <Typography variant="h4">
                {passengers.length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Business Class
              </Typography>
              <Typography variant="h4">
                {passengers.filter(p => p.seatType === 'BUSINESS').length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Infants
              </Typography>
              <Typography variant="h4">
                {passengers.filter(p => p.isInfant).length}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default PlaneView;

