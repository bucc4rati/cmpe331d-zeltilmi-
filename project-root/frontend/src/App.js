import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Container, AppBar, Toolbar, Typography, Box, Button } from '@mui/material';
import FlightIcon from '@mui/icons-material/Flight';
import LogoutIcon from '@mui/icons-material/Logout';

// Components
import Dashboard from './components/Dashboard';
import RosterList from './components/RosterList';
import RosterDetail from './components/RosterDetail';
import CreateRoster from './components/CreateRoster';
import TabularView from './components/TabularView';
import PlaneView from './components/PlaneView';
import ExtendedView from './components/ExtendedView';
import SeatMapView from './components/SeatMapView';
import Login from './components/Login';
import Register from './components/Register';
import ProtectedRoute from './components/ProtectedRoute';
import FlightManagement from './components/FlightManagement';
import CrewManagement from './components/CrewManagement';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Check authentication status on component mount
    const token = localStorage.getItem('jwtToken');
    const user = localStorage.getItem('user');
    console.log('Token:', token);
    console.log('User:', user);
    console.log('isAuthenticated will be:', !!token);
    setIsAuthenticated(!!token);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    // Navigate to login page
    window.location.href = '/login';
  };

  // Debug için
  console.log('isAuthenticated:', isAuthenticated);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ flexGrow: 1 }}>
          <AppBar position="static">
            <Toolbar>
              <FlightIcon sx={{ mr: 2 }} />
              <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                Flight Roster Management System
              </Typography>
              {isAuthenticated && (
                <Button color="inherit" onClick={handleLogout} startIcon={<LogoutIcon />}>
                  Logout
                </Button>
              )}
              {/* Debug: Show authentication status */}
              <Typography variant="caption" sx={{ ml: 2, color: 'white' }}>
                Auth: {isAuthenticated ? 'Yes' : 'No'}
              </Typography>
            </Toolbar>
          </AppBar>
          
          <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/rosters" element={<ProtectedRoute><RosterList /></ProtectedRoute>} />
              <Route path="/rosters/:id" element={<ProtectedRoute><RosterDetail /></ProtectedRoute>} />
                  <Route path="/rosters/:id/tabular" element={<ProtectedRoute><TabularView /></ProtectedRoute>} />
                  <Route path="/rosters/:id/plane" element={<ProtectedRoute><PlaneView /></ProtectedRoute>} />
                  <Route path="/rosters/:id/extended" element={<ProtectedRoute><ExtendedView /></ProtectedRoute>} />
                  <Route path="/rosters/:id/seats" element={<ProtectedRoute><SeatMapView /></ProtectedRoute>} />
              <Route path="/create" element={<ProtectedRoute><CreateRoster /></ProtectedRoute>} />
              <Route path="/flights" element={<ProtectedRoute><FlightManagement /></ProtectedRoute>} />
              <Route path="/crew-management" element={<ProtectedRoute><CrewManagement /></ProtectedRoute>} />
            </Routes>
          </Container>
        </Box>
      </Router>
    </ThemeProvider>
  );
}

export default App;