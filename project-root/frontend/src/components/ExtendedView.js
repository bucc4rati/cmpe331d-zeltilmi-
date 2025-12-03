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
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Chip,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Divider,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from '@mui/material';
import {
  ArrowBack,
  ExpandMore,
  FlightTakeoff,
  Person,
  People,
  Assignment,
  BusinessCenter,
  ChildCare,
  Language,
  Work,
  Schedule,
  LocationOn,
  AirplanemodeActive,
  Restaurant,
  Security
} from '@mui/icons-material';
import { rosterAPI } from '../services/api';

const ExtendedView = () => {
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

  const parseJsonField = (field) => {
    if (!field) return [];
    try {
      return JSON.parse(field);
    } catch (e) {
      return field.split(',').map(item => item.trim());
    }
  };

  const getSeniorityColor = (level) => {
    switch (level) {
      case 'SENIOR': return 'success';
      case 'JUNIOR': return 'warning';
      case 'TRAINEE': return 'info';
      default: return 'default';
    }
  };

  const getAttendantTypeColor = (type) => {
    switch (type) {
      case 'CHIEF': return 'error';
      case 'CHEF': return 'warning';
      case 'REGULAR': return 'primary';
      default: return 'default';
    }
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
  const summary = rosterData?.summary;

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
            Extended View: {roster.flightNumber}
          </Typography>
        </Box>
      </Box>

      {/* Flight Overview */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            <FlightTakeoff sx={{ mr: 1, verticalAlign: 'middle' }} />
            Flight Overview
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <List dense>
                <ListItem>
                  <ListItemIcon><AirplanemodeActive /></ListItemIcon>
                  <ListItemText 
                    primary="Flight Number" 
                    secondary={flight?.flightNumber} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><Schedule /></ListItemIcon>
                  <ListItemText 
                    primary="Departure Time" 
                    secondary={flight?.flightDate ? new Date(flight.flightDate).toLocaleString() : 'N/A'} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><Schedule /></ListItemIcon>
                  <ListItemText 
                    primary="Duration" 
                    secondary={`${flight?.durationMinutes || 0} minutes`} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><LocationOn /></ListItemIcon>
                  <ListItemText 
                    primary="Distance" 
                    secondary={`${flight?.distanceKm || 0} km`} 
                  />
                </ListItem>
              </List>
            </Grid>
            <Grid item xs={12} md={6}>
              <List dense>
                <ListItem>
                  <ListItemIcon><LocationOn /></ListItemIcon>
                  <ListItemText 
                    primary="Route" 
                    secondary={`${flight?.sourceAirport?.airportCode || 'N/A'} → ${flight?.destinationAirport?.airportCode || 'N/A'}`} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><AirplanemodeActive /></ListItemIcon>
                  <ListItemText 
                    primary="Aircraft" 
                    secondary={flight?.vehicleType?.typeName || 'N/A'} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><People /></ListItemIcon>
                  <ListItemText 
                    primary="Total Seats" 
                    secondary={flight?.vehicleType?.totalSeats || 0} 
                  />
                </ListItem>
                <ListItem>
                  <ListItemIcon><Assignment /></ListItemIcon>
                  <ListItemText 
                    primary="Database Type" 
                    secondary={roster.databaseType} 
                  />
                </ListItem>
              </List>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Summary Statistics */}
      {summary && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Total Pilots
                </Typography>
                <Typography variant="h4">
                  {summary.totalPilots || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Cabin Crew
                </Typography>
                <Typography variant="h4">
                  {summary.totalCabinCrew || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Passengers
                </Typography>
                <Typography variant="h4">
                  {summary.totalPassengers || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Total People
                </Typography>
                <Typography variant="h4">
                  {summary.totalPeople || 0}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Detailed Information Accordions */}
      <Box sx={{ mb: 3 }}>
        {/* Pilots Section */}
        <Accordion>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">
              <Person sx={{ mr: 1, verticalAlign: 'middle' }} />
              Pilots ({pilots.length})
            </Typography>
          </AccordionSummary>
          <AccordionDetails>
            {pilots.length > 0 ? (
              <Grid container spacing={2}>
                {pilots.map((pilot, index) => (
                  <Grid item xs={12} md={6} key={index}>
                    <Card variant="outlined">
                      <CardContent>
                        <Typography variant="h6" gutterBottom>
                          {pilot.name}
                        </Typography>
                        <Grid container spacing={1}>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Age: {pilot.age}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Gender: {pilot.gender}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Nationality: {pilot.nationality}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Chip 
                              label={pilot.seniorityLevel} 
                              color={getSeniorityColor(pilot.seniorityLevel)}
                              size="small"
                            />
                          </Grid>
                          <Grid item xs={12}>
                            <Typography variant="body2" color="textSecondary">
                              <Work sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                              Aircraft: {pilot.vehicleRestriction}
                            </Typography>
                          </Grid>
                          <Grid item xs={12}>
                            <Typography variant="body2" color="textSecondary">
                              <Language sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                              Languages: {pilot.knownLanguages}
                            </Typography>
                          </Grid>
                          <Grid item xs={12}>
                            <Typography variant="body2" color="textSecondary">
                              Max Distance: {pilot.maxDistanceKm} km
                            </Typography>
                          </Grid>
                        </Grid>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            ) : (
              <Alert severity="info">No pilots assigned.</Alert>
            )}
          </AccordionDetails>
        </Accordion>

        {/* Cabin Crew Section */}
        <Accordion>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">
              <People sx={{ mr: 1, verticalAlign: 'middle' }} />
              Cabin Crew ({cabinCrew.length})
            </Typography>
          </AccordionSummary>
          <AccordionDetails>
            {cabinCrew.length > 0 ? (
              <Grid container spacing={2}>
                {cabinCrew.map((attendant, index) => (
                  <Grid item xs={12} md={6} key={index}>
                    <Card variant="outlined">
                      <CardContent>
                        <Typography variant="h6" gutterBottom>
                          {attendant.name}
                        </Typography>
                        <Grid container spacing={1}>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Age: {attendant.age}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Gender: {attendant.gender}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Typography variant="body2" color="textSecondary">
                              Nationality: {attendant.nationality}
                            </Typography>
                          </Grid>
                          <Grid item xs={6}>
                            <Chip 
                              label={attendant.attendantType} 
                              color={getAttendantTypeColor(attendant.attendantType)}
                              size="small"
                            />
                          </Grid>
                          <Grid item xs={12}>
                            <Typography variant="body2" color="textSecondary">
                              <Language sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                              Languages: {attendant.knownLanguages}
                            </Typography>
                          </Grid>
                          <Grid item xs={12}>
                            <Typography variant="body2" color="textSecondary">
                              <Work sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                              Aircraft: {attendant.vehicleRestrictions}
                            </Typography>
                          </Grid>
                          {attendant.recipeTypes && (
                            <Grid item xs={12}>
                              <Typography variant="body2" color="textSecondary">
                                <Restaurant sx={{ fontSize: 16, mr: 0.5, verticalAlign: 'middle' }} />
                                Specialties: {attendant.recipeTypes}
                              </Typography>
                            </Grid>
                          )}
                        </Grid>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            ) : (
              <Alert severity="info">No cabin crew assigned.</Alert>
            )}
          </AccordionDetails>
        </Accordion>

        {/* Passengers Section */}
        <Accordion>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Typography variant="h6">
              <Person sx={{ mr: 1, verticalAlign: 'middle' }} />
              Passengers ({passengers.length})
            </Typography>
          </AccordionSummary>
          <AccordionDetails>
            {passengers.length > 0 ? (
              <TableContainer component={Paper}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Name</TableCell>
                      <TableCell>Age</TableCell>
                      <TableCell>Gender</TableCell>
                      <TableCell>Nationality</TableCell>
                      <TableCell>Class</TableCell>
                      <TableCell>Seat</TableCell>
                      <TableCell>Type</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {passengers.map((passenger, index) => (
                      <TableRow key={index}>
                        <TableCell>{passenger.name}</TableCell>
                        <TableCell>{passenger.age}</TableCell>
                        <TableCell>{passenger.gender}</TableCell>
                        <TableCell>{passenger.nationality}</TableCell>
                        <TableCell>
                          <Chip 
                            label={passenger.seatType} 
                            color={passenger.seatType === 'BUSINESS' ? 'success' : 'default'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>{passenger.seatNumber || 'Unassigned'}</TableCell>
                        <TableCell>
                          <Chip 
                            label={passenger.isInfant ? 'Infant' : 'Adult'} 
                            color={passenger.isInfant ? 'warning' : 'default'}
                            size="small"
                            icon={passenger.isInfant ? <ChildCare /> : <Person />}
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Alert severity="info">No passengers found.</Alert>
            )}
          </AccordionDetails>
        </Accordion>

        {/* Aircraft Details */}
        {flight?.vehicleType && (
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMore />}>
              <Typography variant="h6">
                <AirplanemodeActive sx={{ mr: 1, verticalAlign: 'middle' }} />
                Aircraft Details
              </Typography>
            </AccordionSummary>
            <AccordionDetails>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <List dense>
                    <ListItem>
                      <ListItemText 
                        primary="Aircraft Type" 
                        secondary={flight.vehicleType.typeName} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Total Seats" 
                        secondary={flight.vehicleType.totalSeats} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Business Seats" 
                        secondary={flight.vehicleType.businessSeats} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Economy Seats" 
                        secondary={flight.vehicleType.economySeats} 
                      />
                    </ListItem>
                  </List>
                </Grid>
                <Grid item xs={12} md={6}>
                  <List dense>
                    <ListItem>
                      <ListItemText 
                        primary="Max Pilots" 
                        secondary={flight.vehicleType.maxPilots} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Max Cabin Crew" 
                        secondary={flight.vehicleType.maxCabinCrew} 
                      />
                    </ListItem>
                    <ListItem>
                      <ListItemText 
                        primary="Standard Menu" 
                        secondary={flight.vehicleType.standardMenu} 
                      />
                    </ListItem>
                  </List>
                </Grid>
              </Grid>
            </AccordionDetails>
          </Accordion>
        )}
      </Box>
    </Box>
  );
};

export default ExtendedView;




