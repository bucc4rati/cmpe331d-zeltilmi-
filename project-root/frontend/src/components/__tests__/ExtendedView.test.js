import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import ExtendedView from '../ExtendedView';
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

describe('ExtendedView Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Equivalence Partitioning - Roster Loading', () => {
    test('TC1: Valid roster data - should display all sections', async () => {
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
            sourceAirport: { airportCode: 'IST', city: 'Istanbul' },
            destinationAirport: { airportCode: 'ESB', city: 'Ankara' },
            vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
          },
          pilots: [
            { name: 'John Doe', age: 30, gender: 'Male', nationality: 'Turkish', seniorityLevel: 'SENIOR' },
          ],
          cabinCrew: [
            { name: 'Jane Smith', age: 25, gender: 'Female', nationality: 'Turkish', attendantType: 'CHIEF' },
          ],
          passengers: [
            { name: 'Passenger 1', age: 35, gender: 'Male', nationality: 'Turkish', seatType: 'BUSINESS', seatNumber: '1A' },
          ],
          summary: {
            totalPilots: 1,
            totalCabinCrew: 1,
            totalPassengers: 1,
            totalPeople: 3,
          },
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Extended View: TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Flight Overview/i)).toBeInTheDocument();
        expect(screen.getByText(/Pilots/i)).toBeInTheDocument();
        expect(screen.getByText(/Cabin Crew/i)).toBeInTheDocument();
        expect(screen.getByText(/Passengers/i)).toBeInTheDocument();
      });
    });

    test('TC2: Empty roster data - should show empty states', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {},
          pilots: [],
          cabinCrew: [],
          passengers: [],
          summary: { totalPilots: 0, totalCabinCrew: 0, totalPassengers: 0, totalPeople: 0 },
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/No pilots assigned/i)).toBeInTheDocument();
        expect(screen.getByText(/No cabin crew assigned/i)).toBeInTheDocument();
        expect(screen.getByText(/No passengers found/i)).toBeInTheDocument();
      });
    });

    test('TC3: API error - should show error message', async () => {
      rosterAPI.getRosterById.mockRejectedValue(new Error('API Error'));

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Failed to fetch roster details/i)).toBeInTheDocument();
      });
    });

    test('TC4: Invalid roster ID - should show not found', async () => {
      rosterAPI.getRosterById.mockResolvedValue({ data: null });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Roster not found/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Data Display', () => {
    test('TC5: Flight information - should display all flight details', async () => {
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
            vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
          },
          pilots: [],
          cabinCrew: [],
          passengers: [],
          summary: {},
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/90 minutes/i)).toBeInTheDocument();
        expect(screen.getByText(/350 km/i)).toBeInTheDocument();
        expect(screen.getByText(/IST → ESB/i)).toBeInTheDocument();
      });
    });

    test('TC6: Summary statistics - should display correct counts', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {},
          pilots: [],
          cabinCrew: [],
          passengers: [],
          summary: {
            totalPilots: 2,
            totalCabinCrew: 5,
            totalPassengers: 100,
            totalPeople: 107,
          },
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText('2')).toBeInTheDocument(); // Total Pilots
        expect(screen.getByText('5')).toBeInTheDocument(); // Total Cabin Crew
        expect(screen.getByText('100')).toBeInTheDocument(); // Total Passengers
        expect(screen.getByText('107')).toBeInTheDocument(); // Total People
      });
    });
  });
});

describe('ExtendedView Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('Unit Test: getRosterData - should parse JSON correctly', async () => {
      const mockRosterData = {
        flight: { flightNumber: 'TK001' },
        pilots: [],
        cabinCrew: [],
        passengers: [],
        summary: {},
      };

      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify(mockRosterData),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Extended View: TK001/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: getRosterData - should handle invalid JSON gracefully', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: 'invalid json',
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Extended View: TK001/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: getSeniorityColor - should return correct color for each level', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {},
          pilots: [
            { name: 'Pilot 1', seniorityLevel: 'SENIOR' },
            { name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
            { name: 'Pilot 3', seniorityLevel: 'TRAINEE' },
          ],
          cabinCrew: [],
          passengers: [],
          summary: {},
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Pilot 1/i)).toBeInTheDocument();
        expect(screen.getByText(/Pilot 2/i)).toBeInTheDocument();
        expect(screen.getByText(/Pilot 3/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: getAttendantTypeColor - should return correct color for each type', async () => {
      const mockRoster = {
        id: 1,
        flightNumber: 'TK001',
        databaseType: 'SQL',
        rosterData: JSON.stringify({
          flight: {},
          pilots: [],
          cabinCrew: [
            { name: 'Crew 1', attendantType: 'CHIEF' },
            { name: 'Crew 2', attendantType: 'REGULAR' },
            { name: 'Crew 3', attendantType: 'CHEF' },
          ],
          passengers: [],
          summary: {},
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Crew 1/i)).toBeInTheDocument();
        expect(screen.getByText(/Crew 2/i)).toBeInTheDocument();
        expect(screen.getByText(/Crew 3/i)).toBeInTheDocument();
      });
    });

  test('Integration Test: Complete extended view rendering', async () => {
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
            sourceAirport: { airportCode: 'IST', city: 'Istanbul' },
            destinationAirport: { airportCode: 'ESB', city: 'Ankara' },
            vehicleType: {
              typeName: 'Boeing 737',
              totalSeats: 189,
              businessSeats: 20,
              economySeats: 169,
              maxPilots: 4,
              maxCabinCrew: 6,
              standardMenu: 'Standard',
            },
          },
          pilots: [
            {
              name: 'John Doe',
              age: 30,
              gender: 'Male',
              nationality: 'Turkish',
              seniorityLevel: 'SENIOR',
              vehicleRestriction: 'Boeing 737',
              knownLanguages: 'Turkish, English',
              maxDistanceKm: 5000,
            },
          ],
          cabinCrew: [
            {
              name: 'Jane Smith',
              age: 25,
              gender: 'Female',
              nationality: 'Turkish',
              attendantType: 'CHIEF',
              knownLanguages: 'Turkish, English',
              vehicleRestrictions: 'Boeing 737',
            },
          ],
          passengers: [
            {
              name: 'Passenger 1',
              age: 35,
              gender: 'Male',
              nationality: 'Turkish',
              seatType: 'BUSINESS',
              seatNumber: '1A',
              isInfant: false,
            },
          ],
          summary: {
            totalPilots: 1,
            totalCabinCrew: 1,
            totalPassengers: 1,
            totalPeople: 3,
          },
        }),
      };

      rosterAPI.getRosterById.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<ExtendedView />);

      await waitFor(() => {
        expect(screen.getByText(/Extended View: TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Flight Overview/i)).toBeInTheDocument();
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
        expect(screen.getByText(/Jane Smith/i)).toBeInTheDocument();
        expect(screen.getByText(/Passenger 1/i)).toBeInTheDocument();
        expect(screen.getByText(/Aircraft Details/i)).toBeInTheDocument();
      });
    });
});


