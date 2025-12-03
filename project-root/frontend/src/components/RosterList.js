import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  Alert,
  CircularProgress,
  Grid,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper
} from '@mui/material';
import {
  Add,
  Visibility,
  Delete,
  Download,
  ArrowBack,
  FlightTakeoff,
  People,
  Assignment
} from '@mui/icons-material';
import { rosterAPI } from '../services/api';

const RosterList = () => {
  const navigate = useNavigate();
  const [rosters, setRosters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialog, setDeleteDialog] = useState({ open: false, roster: null });
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchRosters();
  }, []);

  const fetchRosters = async () => {
    try {
      setLoading(true);
      const response = await rosterAPI.getAllRosters();
      setRosters(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch rosters. Please check if the backend service is running.');
      console.error('Error fetching rosters:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteDialog.roster) return;

    setDeleting(true);
    try {
      await rosterAPI.deleteRoster(deleteDialog.roster.id);
      setRosters(rosters.filter(r => r.id !== deleteDialog.roster.id));
      
      // Remove hasRosters flag from flight in localStorage
      const flights = JSON.parse(localStorage.getItem('flights') || '[]');
      const updatedFlights = flights.map(flight => 
        flight.number === deleteDialog.roster.flightNumber 
          ? { ...flight, hasRosters: false }
          : flight
      );
      localStorage.setItem('flights', JSON.stringify(updatedFlights));
      
      setDeleteDialog({ open: false, roster: null });
    } catch (err) {
      console.error('Error deleting roster:', err);
    } finally {
      setDeleting(false);
    }
  };

  const handleExport = async (roster) => {
    try {
      const response = await rosterAPI.exportRoster(roster.id);
      const blob = new Blob([response.data], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `roster-${roster.flightNumber}-${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      console.error('Error exporting roster:', err);
    }
  };

  const getRosterStats = (roster) => {
    try {
      const rosterData = JSON.parse(roster.rosterData);
      
      // Calculate stats from actual data instead of summary
      const pilots = rosterData.pilots || [];
      const cabinCrew = rosterData.cabinCrew || [];
      const passengers = rosterData.passengers || [];
      
      return {
        totalPilots: pilots.length,
        totalCabinCrew: cabinCrew.length,
        totalPassengers: passengers.length,
        totalPeople: pilots.length + cabinCrew.length + passengers.length
      };
    } catch (e) {
      console.error('Error parsing roster data:', e);
      return {
        totalPilots: 0,
        totalCabinCrew: 0,
        totalPassengers: 0,
        totalPeople: 0
      };
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

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
            Flight Rosters
          </Typography>
        </Box>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => navigate('/create')}
        >
          Create New Roster
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {rosters.length === 0 ? (
        <Alert severity="info">
          No rosters found. Create your first roster to get started!
        </Alert>
      ) : (
        <>
          {/* Summary Cards */}
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={4}>
              <Card>
                <CardContent>
                  <Box display="flex" alignItems="center">
                    <FlightTakeoff color="primary" sx={{ mr: 2 }} />
                    <Box>
                      <Typography color="textSecondary" gutterBottom>
                        Total Rosters
                      </Typography>
                      <Typography variant="h5">
                        {rosters.length}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Card>
                <CardContent>
                  <Box display="flex" alignItems="center">
                    <People color="secondary" sx={{ mr: 2 }} />
                    <Box>
                      <Typography color="textSecondary" gutterBottom>
                        Total People
                      </Typography>
                      <Typography variant="h5">
                        {rosters.reduce((sum, roster) => {
                          const stats = getRosterStats(roster);
                          return sum + (stats.totalPeople || 0);
                        }, 0)}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={4}>
              <Card>
                <CardContent>
                  <Box display="flex" alignItems="center">
                    <Assignment color="success" sx={{ mr: 2 }} />
                    <Box>
                      <Typography color="textSecondary" gutterBottom>
                        Active Rosters
                      </Typography>
                      <Typography variant="h5">
                        {rosters.filter(r => r.isActive).length}
                      </Typography>
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* Rosters Table */}
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Flight Number</TableCell>
                  <TableCell>Roster Name</TableCell>
                  <TableCell>Database Type</TableCell>
                  <TableCell>Created Date</TableCell>
                  <TableCell>Stats</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rosters.map((roster) => {
                  const stats = getRosterStats(roster);
                  return (
                    <TableRow key={roster.id} hover>
                      <TableCell>
                        <Typography variant="h6">
                          {roster.flightNumber}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2">
                          {roster.rosterName}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip 
                          label={roster.databaseType} 
                          color="primary" 
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        {new Date(roster.createdDate).toLocaleDateString()}
                      </TableCell>
                      <TableCell>
                        <Box>
                          <Typography variant="caption" display="block">
                            Pilots: {stats.totalPilots || 0}
                          </Typography>
                          <Typography variant="caption" display="block">
                            Crew: {stats.totalCabinCrew || 0}
                          </Typography>
                          <Typography variant="caption" display="block">
                            Passengers: {stats.totalPassengers || 0}
                          </Typography>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Box sx={{ display: 'flex', gap: 1 }}>
                          <IconButton
                            size="small"
                            onClick={() => navigate(`/rosters/${roster.id}`)}
                            title="View Details"
                          >
                            <Visibility />
                          </IconButton>
                          <IconButton
                            size="small"
                            onClick={() => handleExport(roster)}
                            title="Export JSON"
                          >
                            <Download />
                          </IconButton>
                          <IconButton
                            size="small"
                            onClick={() => setDeleteDialog({ open: true, roster })}
                            title="Delete"
                            color="error"
                          >
                            <Delete />
                          </IconButton>
                        </Box>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog
        open={deleteDialog.open}
        onClose={() => setDeleteDialog({ open: false, roster: null })}
      >
        <DialogTitle>Delete Roster</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete the roster for flight {deleteDialog.roster?.flightNumber}?
            This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog({ open: false, roster: null })}>
            Cancel
          </Button>
          <Button
            onClick={handleDelete}
            color="error"
            disabled={deleting}
            startIcon={deleting ? <CircularProgress size={20} /> : <Delete />}
          >
            {deleting ? 'Deleting...' : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default RosterList;
