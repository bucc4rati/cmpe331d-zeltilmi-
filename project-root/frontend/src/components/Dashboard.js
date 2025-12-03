import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Box,
  Chip,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  FlightTakeoff,
  People,
  Assignment,
  Add,
  Visibility,
  AdminPanelSettings,
  ManageAccounts
} from '@mui/icons-material';
import { rosterAPI, healthAPI } from '../services/api';

const Dashboard = () => {
  const navigate = useNavigate();
  const [rosters, setRosters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [systemHealth, setSystemHealth] = useState(null);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Get user info from localStorage
    const userStr = localStorage.getItem('user');
    if (userStr) {
      setUser(JSON.parse(userStr));
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [rostersResponse, healthResponse] = await Promise.all([
        rosterAPI.getAllRosters(),
        healthAPI.checkHealth()
      ]);
      
      setRosters(rostersResponse.data);
      setSystemHealth(healthResponse.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch data. Please check if the backend services are running.');
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const getTotalStats = () => {
    let totalPilots = 0;
    let totalCabinCrew = 0;
    let totalPassengers = 0;
    let totalPeople = 0;

    rosters.forEach(roster => {
      try {
        const rosterData = JSON.parse(roster.rosterData);
        const summary = rosterData.summary;
        totalPilots += summary.totalPilots || 0;
        totalCabinCrew += summary.totalCabinCrew || 0;
        totalPassengers += summary.totalPassengers || 0;
        totalPeople += summary.totalPeople || 0;
      } catch (e) {
        console.error('Error parsing roster data:', e);
      }
    });

    return { totalPilots, totalCabinCrew, totalPassengers, totalPeople };
  };

  const stats = getTotalStats();

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Flight Roster Dashboard
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {systemHealth && (
        <Alert severity="success" sx={{ mb: 3 }}>
          System Status: {systemHealth.status} - {systemHealth.service} (Port {systemHealth.port})
        </Alert>
      )}

      {/* Statistics Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <FlightTakeoff color="primary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Rosters
                  </Typography>
                  <Typography variant="h4">
                    {rosters.length}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <People color="secondary" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total People
                  </Typography>
                  <Typography variant="h4">
                    {stats.totalPeople}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Assignment color="success" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Pilots
                  </Typography>
                  <Typography variant="h4">
                    {stats.totalPilots}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <People color="info" sx={{ mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Passengers
                  </Typography>
                  <Typography variant="h4">
                    {stats.totalPassengers}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Action Buttons */}
      <Box sx={{ mb: 3 }}>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => navigate('/create')}
          sx={{ mr: 2, mb: 1 }}
        >
          Create New Roster
        </Button>
        <Button
          variant="outlined"
          startIcon={<Visibility />}
          onClick={() => navigate('/rosters')}
          sx={{ mr: 2, mb: 1 }}
        >
          View All Rosters
        </Button>
        
        {/* Admin Only Buttons */}
        {user?.role === 'ADMIN' && (
          <>
            <Button
              variant="outlined"
              color="secondary"
              startIcon={<FlightTakeoff />}
              onClick={() => navigate('/flights')}
              sx={{ mr: 2, mb: 1 }}
            >
              View Flights
            </Button>
            <Button
              variant="outlined"
              color="secondary"
              startIcon={<ManageAccounts />}
              onClick={() => navigate('/crew-management')}
              sx={{ mb: 1 }}
            >
              Manage Crews
            </Button>
          </>
        )}
      </Box>
      
      {/* Admin Badge */}
      {user?.role === 'ADMIN' && (
        <Alert severity="info" icon={<AdminPanelSettings />} sx={{ mb: 3 }}>
          Admin Panel Access - You can manage flights and crew members
        </Alert>
      )}

      {/* Recent Rosters */}
      <Typography variant="h5" gutterBottom>
        Recent Rosters
      </Typography>
      
      {rosters.length === 0 ? (
        <Alert severity="info">
          No rosters found. Create your first roster to get started!
        </Alert>
      ) : (
        <Grid container spacing={2}>
          {rosters.slice(0, 6).map((roster) => (
            <Grid item xs={12} sm={6} md={4} key={roster.id}>
              <Card 
                sx={{ 
                  cursor: 'pointer',
                  '&:hover': { boxShadow: 6 }
                }}
                onClick={() => navigate(`/rosters/${roster.id}`)}
              >
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {roster.flightNumber}
                  </Typography>
                  <Typography color="textSecondary" gutterBottom>
                    {roster.rosterName}
                  </Typography>
                  <Box sx={{ mt: 2 }}>
                    <Chip 
                      label={roster.databaseType} 
                      size="small" 
                      color="primary" 
                      sx={{ mr: 1 }}
                    />
                    <Chip 
                      label={new Date(roster.createdDate).toLocaleDateString()} 
                      size="small" 
                      variant="outlined"
                    />
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Box>
  );
};

export default Dashboard;




