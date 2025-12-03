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
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from '@mui/material';
import {
  ArrowBack,
  Download,
  ExpandMore,
  FlightTakeoff,
  People,
  Assignment,
  Person,
  TableChart,
  AirplanemodeActive,
  ViewList,
  Refresh,
  EventSeat,
  Edit,
  ManageAccounts
} from '@mui/icons-material';
import { rosterAPI, crewAPI } from '../services/api';
import SeatMap from './SeatMap';
import CrewSelection from './CrewSelection';

const RosterDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [roster, setRoster] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [exportDialog, setExportDialog] = useState(false);
  const [editCrewDialog, setEditCrewDialog] = useState(false);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Get user info
    const userStr = localStorage.getItem('user');
    if (userStr) {
      setUser(JSON.parse(userStr));
    }
  }, []);

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
      setError('Failed to fetch roster details. Please check if the backend service is running.');
      console.error('Error fetching roster:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async () => {
    try {
      const response = await rosterAPI.exportRoster(id);
      const blob = new Blob([response.data], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `roster-${roster.flightNumber}-${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      setExportDialog(false);
    } catch (err) {
      console.error('Error exporting roster:', err);
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

  const renderPersonTable = (persons, title) => {
    if (!persons || persons.length === 0) {
      return <Typography color="textSecondary">No {title.toLowerCase()} found.</Typography>;
    }

    return (
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Age</TableCell>
              <TableCell>Gender</TableCell>
              <TableCell>Nationality</TableCell>
              <TableCell>Additional Info</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {persons.map((person, index) => (
              <TableRow key={index}>
                <TableCell>{person.name}</TableCell>
                <TableCell>{person.age}</TableCell>
                <TableCell>{person.gender}</TableCell>
                <TableCell>{person.nationality}</TableCell>
                <TableCell>
                  {person.seatNumber && `Seat: ${person.seatNumber}`}
                  {person.attendantType && `Type: ${person.attendantType}`}
                  {person.seniorityLevel && `Level: ${person.seniorityLevel}`}
                  {person.vehicleRestriction && `Vehicle: ${person.vehicleRestriction}`}
                  {person.recipeTypes && `Recipes: ${person.recipeTypes}`}
                </TableCell>
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
  const summary = rosterData?.summary || {};

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/')}
            sx={{ mr: 2 }}
          >
            Back to Dashboard
          </Button>
          <Typography variant="h4">
            Flight Roster: {roster.flightNumber}
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={fetchRoster}
            disabled={loading}
          >
            Refresh
          </Button>
          {user?.role === 'ADMIN' && (
            <Button
              variant="contained"
              color="secondary"
              startIcon={<ManageAccounts />}
              onClick={() => setEditCrewDialog(true)}
            >
              Manage Crew
            </Button>
          )}
          <Button
            variant="outlined"
            startIcon={<TableChart />}
            onClick={() => navigate(`/rosters/${id}/tabular`)}
          >
            Tabular View
          </Button>
          <Button
            variant="outlined"
            startIcon={<AirplanemodeActive />}
            onClick={() => navigate(`/rosters/${id}/plane`)}
          >
            Plane View
          </Button>
          <Button
            variant="outlined"
            startIcon={<ViewList />}
            onClick={() => navigate(`/rosters/${id}/extended`)}
          >
            Extended View
          </Button>
          <Button
            variant="outlined"
            startIcon={<EventSeat />}
            onClick={() => navigate(`/rosters/${id}/seats`)}
          >
            Seat Map
          </Button>
          <Button
            variant="contained"
            startIcon={<Download />}
            onClick={() => setExportDialog(true)}
          >
            Export JSON
          </Button>
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
                <Typography><strong>Flight Number:</strong> {flight.flightNumber}</Typography>
                <Typography><strong>Date:</strong> {new Date(flight.flightDate).toLocaleString()}</Typography>
                <Typography><strong>Duration:</strong> {flight.durationMinutes} minutes</Typography>
                <Typography><strong>Distance:</strong> {flight.distanceKm} km</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography><strong>From:</strong> {flight.sourceAirport?.airportName} ({flight.sourceAirport?.airportCode})</Typography>
                <Typography><strong>To:</strong> {flight.destinationAirport?.airportName} ({flight.destinationAirport?.airportCode})</Typography>
                <Typography><strong>Aircraft:</strong> {flight.vehicleType?.typeName}</Typography>
                <Typography><strong>Total Seats:</strong> {flight.vehicleType?.totalSeats}</Typography>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      )}

      {/* Summary Statistics */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Assignment color="primary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Pilots
                  </Typography>
                  <Typography variant="h5">
                    {summary.totalPilots || 0}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <People color="secondary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Cabin Crew
                  </Typography>
                  <Typography variant="h5">
                    {summary.totalCabinCrew || 0}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Person color="success" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Passengers
                  </Typography>
                  <Typography variant="h5">
                    {summary.totalPassengers || 0}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <FlightTakeoff color="info" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total People
                  </Typography>
                  <Typography variant="h5">
                    {summary.totalPeople || 0}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Detailed Information */}
      <Accordion>
        <AccordionSummary expandIcon={<ExpandMore />}>
          <Typography variant="h6">Pilots ({pilots.length})</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {renderPersonTable(pilots, 'Pilots')}
        </AccordionDetails>
      </Accordion>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMore />}>
          <Typography variant="h6">Cabin Crew ({cabinCrew.length})</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {renderPersonTable(cabinCrew, 'Cabin Crew')}
        </AccordionDetails>
      </Accordion>

      <Accordion>
        <AccordionSummary expandIcon={<ExpandMore />}>
          <Typography variant="h6">Passengers ({passengers.length})</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {renderPersonTable(passengers, 'Passengers')}
        </AccordionDetails>
      </Accordion>

      {/* Edit Crew Dialog */}
      <Dialog 
        open={editCrewDialog} 
        onClose={() => setEditCrewDialog(false)}
        maxWidth="lg"
        fullWidth
      >
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <ManageAccounts sx={{ mr: 1 }} />
            Manage Crew for {roster?.flightNumber}
          </Box>
        </DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
            Select crew members to assign to this roster. Changes will update the roster immediately.
          </Typography>
          <CrewSelection 
            rosterId={id}
            onCrewAssigned={async () => {
              await fetchRoster();
              setEditCrewDialog(false);
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditCrewDialog(false)}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* Export Dialog */}
      <Dialog open={exportDialog} onClose={() => setExportDialog(false)}>
        <DialogTitle>Export Roster</DialogTitle>
        <DialogContent>
          <Typography>
            This will download the roster data as a JSON file.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setExportDialog(false)}>Cancel</Button>
          <Button onClick={handleExport} variant="contained">
            Download JSON
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default RosterDetail;
