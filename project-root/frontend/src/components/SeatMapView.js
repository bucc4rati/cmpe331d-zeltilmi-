import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  Alert,
  CircularProgress,
  Card,
  CardContent
} from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import { rosterAPI } from '../services/api';
import SeatMap from './SeatMap';

const SeatMapView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [roster, setRoster] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchRoster();
  }, [id]);

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

  const getRosterData = () => {
    if (!roster || !roster.rosterData) return null;
    try {
      return JSON.parse(roster.rosterData);
    } catch (e) {
      return null;
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
  const seatMap = rosterData?.seatMap;
  const passengers = rosterData?.passengers || [];

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center' }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate(`/rosters/${id}`)}
          sx={{ mr: 2 }}
        >
          Back to Roster
        </Button>
        <Typography variant="h4">
          Seat Map: {roster.flightNumber}
        </Typography>
      </Box>

      <Card>
        <CardContent>
          <SeatMap seatMap={seatMap} passengers={passengers} />
        </CardContent>
      </Card>
    </Box>
  );
};

export default SeatMapView;




