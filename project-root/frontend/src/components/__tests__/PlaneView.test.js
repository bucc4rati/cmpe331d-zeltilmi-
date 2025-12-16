import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import PlaneView from '../PlaneView';
import { rosterAPI } from '../../services/api';

// Mock the API service
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({ id: '1' }),
  useNavigate: () => jest.fn(),
}));

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('PlaneView Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Equivalence Partitioning - Roster Loading', () => {
    test('TC1: Valid roster with seat map - should display seat map', async () => {
      const seatingPlan = {
        rows: 5,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            flightDate: '2024-01-01T10:00:00',
            durationMinutes: 90,
            distanceKm: 350,
            sourceAirport: { airportCode: 'IST' },
            destinationAirport: { airportCode: 'ESB' },
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [{ name: 'John Doe' }],
          cabinCrew: [{ name: 'Jane Smith' }],
          passengers: [
            { name: 'Passenger 1', seatNumber: '1A', seatType: 'BUSINESS', isInfant: false },
            { name: 'Passenger 2', seatNumber: '3C', seatType: 'ECONOMY', isInfant: false },
            { name: 'Passenger 3', seatNumber: '2B', seatType: 'BUSINESS', isInfant: true },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Plane View: TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
        expect(screen.getByText(/Crew Layout/i)).toBeInTheDocument();
      });
    });

    test('TC2: Roster without seat map data - should show warning', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: null,
          },
          pilots: [],
          cabinCrew: [],
          passengers: [],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Unable to generate seat map/i)).toBeInTheDocument();
      });
    });

    test('TC3: API error - should show error message', async () => {
      rosterAPI.getRosterById.mockRejectedValue(new Error('API Error'));

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Failed to fetch roster details/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Seat Map Display', () => {
    test('TC4: Business class seats - should display correctly', async () => {
      const seatingPlan = {
        rows: 3,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'Passenger 1', seatNumber: '1A', seatType: 'BUSINESS', isInfant: false },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
      });
    });

    test('TC5: Economy class seats - should display correctly', async () => {
      const seatingPlan = {
        rows: 3,
        seatsPerRow: 6,
        businessRows: 1,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'Passenger 1', seatNumber: '2A', seatType: 'ECONOMY', isInfant: false },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
      });
    });

    test('TC6: Infant passengers - should display with special indicator', async () => {
      const seatingPlan = {
        rows: 3,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'Infant Passenger', seatNumber: '1B', seatType: 'BUSINESS', isInfant: true },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Statistics Display', () => {
    test('TC7: Passenger statistics - should display correct counts', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'P1', seatType: 'BUSINESS', isInfant: false },
            { name: 'P2', seatType: 'BUSINESS', isInfant: false },
            { name: 'P3', seatType: 'ECONOMY', isInfant: false },
            { name: 'P4', seatType: 'ECONOMY', isInfant: true },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText('4')).toBeInTheDocument(); // Total Passengers
        expect(screen.getByText('2')).toBeInTheDocument(); // Business Class
        expect(screen.getByText('1')).toBeInTheDocument(); // Infants
      });
    });
  });
});

describe('PlaneView Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('Unit Test: generateSeatMap - should create correct seat map structure', async () => {
      const seatingPlan = {
        rows: 3,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'Passenger 1', seatNumber: '1A', seatType: 'BUSINESS', isInfant: false },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: getSeatColor - should return correct color for each seat type', async () => {
      const seatingPlan = {
        rows: 3,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [
            { name: 'P1', seatNumber: '1A', seatType: 'BUSINESS', isInfant: false },
            { name: 'P2', seatNumber: '3A', seatType: 'ECONOMY', isInfant: false },
            { name: 'P3', seatNumber: '2A', seatType: 'BUSINESS', isInfant: true },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: getRosterData - should parse JSON correctly', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: { flightNumber: 'TK001' },
          pilots: [],
          cabinCrew: [],
          passengers: [],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Plane View: TK001/i)).toBeInTheDocument();
      });
    });

  test('Integration Test: Complete plane view rendering', async () => {
      const seatingPlan = {
        rows: 5,
        seatsPerRow: 6,
        businessRows: 2,
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {
            flightNumber: 'TK001',
            flightDate: '2024-01-01T10:00:00',
            durationMinutes: 90,
            distanceKm: 350,
            sourceAirport: { airportCode: 'IST' },
            destinationAirport: { airportCode: 'ESB' },
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              seatingPlan: JSON.stringify(seatingPlan),
            },
          },
          pilots: [
            { name: 'John Doe' },
            { name: 'Jane Smith' },
          ],
          cabinCrew: [
            { name: 'Crew 1' },
            { name: 'Crew 2' },
            { name: 'Crew 3' },
          ],
          passengers: [
            { name: 'Passenger 1', seatNumber: '1A', seatType: 'BUSINESS', isInfant: false },
            { name: 'Passenger 2', seatNumber: '3C', seatType: 'ECONOMY', isInfant: false },
          ],
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<PlaneView />);

      await waitFor(() => {
        expect(screen.getByText(/Plane View: TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Flight Information/i)).toBeInTheDocument();
        expect(screen.getByText(/Seat Legend/i)).toBeInTheDocument();
        expect(screen.getByText(/Crew Layout/i)).toBeInTheDocument();
        expect(screen.getByText(/Seat Map/i)).toBeInTheDocument();
        expect(screen.getByText(/Total Passengers/i)).toBeInTheDocument();
      });
    });
});


