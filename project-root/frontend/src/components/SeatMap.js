import React from 'react';
import {
  Box,
  Typography,
  Grid,
  Paper,
  Chip,
  Card,
  CardContent
} from '@mui/material';
import { Person, Business, FlightClass } from '@mui/icons-material';

const SeatMap = ({ seatMap, passengers = [] }) => {
  if (!seatMap || !seatMap.layout) {
    return (
      <Box>
        <Typography variant="h6" gutterBottom>
          Seat Map
        </Typography>
        <Typography color="textSecondary">
          No seat map available for this flight.
        </Typography>
      </Box>
    );
  }

  const { layout, totalRows, seatsPerRow, businessRows } = seatMap;

  const getSeatIcon = (seatInfo) => {
    if (seatInfo.isOccupied) {
      return <Person color="primary" />;
    }
    return null;
  };

  const getSeatColor = (seatInfo) => {
    if (seatInfo.isOccupied) {
      return seatInfo.seatType === 'BUSINESS' ? '#4caf50' : '#2196f3';
    }
    return seatInfo.seatType === 'BUSINESS' ? '#e8f5e8' : '#e3f2fd';
  };

  const getSeatBorder = (seatInfo) => {
    if (seatInfo.isOccupied) {
      return '2px solid #1976d2';
    }
    return '1px solid #ccc';
  };

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        Seat Map
      </Typography>
      
      {/* Legend */}
      <Box sx={{ mb: 2, display: 'flex', gap: 2, flexWrap: 'wrap' }}>
        <Chip 
          icon={<Business />} 
          label="Business Class" 
          color="success" 
          variant="outlined" 
        />
        <Chip 
          icon={<FlightClass />} 
          label="Economy Class" 
          color="primary" 
          variant="outlined" 
        />
        <Chip 
          icon={<Person />} 
          label="Occupied" 
          color="primary" 
        />
      </Box>

      {/* Seat Map */}
      <Paper sx={{ p: 2, overflow: 'auto' }}>
        <Grid container spacing={1} justifyContent="center">
          {layout.map((row, rowIndex) => (
            <Grid item xs={12} key={rowIndex}>
              <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                {/* Row number */}
                <Box 
                  sx={{ 
                    width: 30, 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    fontWeight: 'bold',
                    fontSize: '0.8rem'
                  }}
                >
                  {rowIndex + 1}
                </Box>
                
                {/* Seats */}
                {row.map((seat, seatIndex) => (
                  <Box
                    key={seatIndex}
                    sx={{
                      width: 40,
                      height: 40,
                      border: getSeatBorder(seat),
                      borderRadius: 1,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      backgroundColor: getSeatColor(seat),
                      cursor: seat.isOccupied ? 'pointer' : 'default',
                      '&:hover': {
                        opacity: 0.8
                      }
                    }}
                    title={seat.isOccupied ? 
                      `Seat ${seat.seatNumber} - ${seat.passenger?.name || 'Passenger'}` : 
                      `Seat ${seat.seatNumber} - Available`
                    }
                  >
                    {getSeatIcon(seat)}
                  </Box>
                ))}
              </Box>
            </Grid>
          ))}
        </Grid>
      </Paper>

      {/* Statistics */}
      <Box sx={{ mt: 2 }}>
        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Total Seats
                </Typography>
                <Typography variant="h6">
                  {totalRows * seatsPerRow}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Business Seats
                </Typography>
                <Typography variant="h6">
                  {businessRows * seatsPerRow}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom>
                  Economy Seats
                </Typography>
                <Typography variant="h6">
                  {(totalRows - businessRows) * seatsPerRow}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>

      {/* Occupied Seats List */}
      {passengers.length > 0 && (
        <Box sx={{ mt: 3 }}>
          <Typography variant="h6" gutterBottom>
            Passenger Seat Assignments
          </Typography>
          <Grid container spacing={1}>
            {passengers
              .filter(p => p.seatNumber)
              .map((passenger, index) => (
                <Grid item xs={12} sm={6} md={4} key={index}>
                  <Chip
                    icon={<Person />}
                    label={`${passenger.name} - ${passenger.seatNumber}`}
                    color={passenger.seatType === 'BUSINESS' ? 'success' : 'primary'}
                    variant="outlined"
                  />
                </Grid>
              ))}
          </Grid>
        </Box>
      )}
    </Box>
  );
};

export default SeatMap;
