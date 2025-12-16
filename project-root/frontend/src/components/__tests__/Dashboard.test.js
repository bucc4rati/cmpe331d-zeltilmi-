import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Dashboard from '../Dashboard';
import { rosterAPI, healthAPI } from '../../services/api';

// Mock the API services
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('Dashboard Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  describe('Equivalence Partitioning - Data Loading', () => {
    test('TC1: Successful data load - should display statistics', async () => {
      const mockRosters = [
        {
          id: 1,
          flightNumber: 'TK001',
          rosterName: 'Roster 1',
          databaseType: 'SQL',
          createdDate: '2024-01-01',
          rosterData: JSON.stringify({
            summary: {
              totalPilots: 2,
              totalCabinCrew: 5,
              totalPassengers: 100,
              totalPeople: 107,
            },
          }),
        },
      ];

      const mockHealth = { status: 'UP', service: 'Main System', port: 8080 };

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: mockHealth });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText(/Flight Roster Dashboard/i)).toBeInTheDocument();
      });

      await waitFor(() => {
        expect(screen.getByText('1')).toBeInTheDocument(); // Total Rosters
        expect(screen.getByText('107')).toBeInTheDocument(); // Total People
        expect(screen.getByText('2')).toBeInTheDocument(); // Total Pilots
        expect(screen.getByText('100')).toBeInTheDocument(); // Total Passengers
      });
    });

    test('TC2: Empty rosters list - should show empty state', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText(/No rosters found/i)).toBeInTheDocument();
      });
    });

    test('TC3: API error - should show error message', async () => {
      rosterAPI.getAllRosters.mockRejectedValue(new Error('API Error'));
      healthAPI.checkHealth.mockRejectedValue(new Error('API Error'));

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText(/Failed to fetch data/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - User Role Display', () => {
    test('TC4: ADMIN user - should show admin buttons', async () => {
      const user = { id: 1, username: 'admin', role: 'ADMIN' };
      localStorage.setItem('user', JSON.stringify(user));

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText(/View Flights/i)).toBeInTheDocument();
        expect(screen.getByText(/Manage Crews/i)).toBeInTheDocument();
        expect(screen.getByText(/Admin Panel Access/i)).toBeInTheDocument();
      });
    });

    test('TC5: USER role - should not show admin buttons', async () => {
      const user = { id: 1, username: 'user', role: 'USER' };
      localStorage.setItem('user', JSON.stringify(user));

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.queryByText(/View Flights/i)).not.toBeInTheDocument();
        expect(screen.queryByText(/Manage Crews/i)).not.toBeInTheDocument();
      });
    });

    test('TC6: No user in localStorage - should not show admin buttons', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.queryByText(/View Flights/i)).not.toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Statistics Calculation', () => {
    test('TC7: Multiple rosters - should sum statistics correctly', async () => {
      const mockRosters = [
        {
          id: 1,
          flightNumber: 'TK001',
          rosterData: JSON.stringify({
            summary: { totalPilots: 2, totalCabinCrew: 5, totalPassengers: 100, totalPeople: 107 },
          }),
        },
        {
          id: 2,
          flightNumber: 'TK002',
          rosterData: JSON.stringify({
            summary: { totalPilots: 2, totalCabinCrew: 6, totalPassengers: 150, totalPeople: 158 },
          }),
        },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText('2')).toBeInTheDocument(); // Total Rosters
        expect(screen.getByText('265')).toBeInTheDocument(); // Total People (107 + 158)
        expect(screen.getByText('4')).toBeInTheDocument(); // Total Pilots (2 + 2)
        expect(screen.getByText('250')).toBeInTheDocument(); // Total Passengers (100 + 150)
      });
    });

    test('TC8: Invalid roster data - should handle gracefully', async () => {
      const mockRosters = [
        {
          id: 1,
          flightNumber: 'TK001',
          rosterData: 'invalid json',
        },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText('1')).toBeInTheDocument(); // Total Rosters
        expect(screen.getByText('0')).toBeInTheDocument(); // Total People (defaults to 0)
      });
    });
  });
});

describe('Dashboard Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('Unit Test: fetchData - should call both APIs', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(rosterAPI.getAllRosters).toHaveBeenCalled();
        expect(healthAPI.checkHealth).toHaveBeenCalled();
      });
    });

  test('Unit Test: getTotalStats - should calculate statistics correctly', async () => {
      const mockRosters = [
        {
          id: 1,
          rosterData: JSON.stringify({
            summary: { totalPilots: 2, totalCabinCrew: 5, totalPassengers: 100, totalPeople: 107 },
          }),
        },
        {
          id: 2,
          rosterData: JSON.stringify({
            summary: { totalPilots: 1, totalCabinCrew: 4, totalPassengers: 50, totalPeople: 55 },
          }),
        },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText('3')).toBeInTheDocument(); // Total Pilots (2 + 1)
        expect(screen.getByText('162')).toBeInTheDocument(); // Total People (107 + 55)
      });
    });

  test('Unit Test: Loading state - should show loading indicator', () => {
      rosterAPI.getAllRosters.mockImplementation(() => new Promise(() => {}));
      healthAPI.checkHealth.mockImplementation(() => new Promise(() => {}));

      renderWithRouter(<Dashboard />);

      // Check for loading indicator (CircularProgress)
      const loadingIndicator = document.querySelector('[role="progressbar"]');
      expect(loadingIndicator).toBeInTheDocument();
    });

  test('Integration Test: Complete dashboard rendering flow', async () => {
      const mockRosters = [
        {
          id: 1,
          flightNumber: 'TK001',
          rosterName: 'Roster 1',
          databaseType: 'SQL',
          createdDate: '2024-01-01T00:00:00',
          rosterData: JSON.stringify({
            summary: { totalPilots: 2, totalCabinCrew: 5, totalPassengers: 100, totalPeople: 107 },
          }),
        },
      ];

      const mockHealth = { status: 'UP', service: 'Main System', port: 8080 };

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: mockHealth });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        expect(screen.getByText(/Flight Roster Dashboard/i)).toBeInTheDocument();
        expect(screen.getByText(/System Status/i)).toBeInTheDocument();
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Create New Roster/i)).toBeInTheDocument();
        expect(screen.getByText(/View All Rosters/i)).toBeInTheDocument();
      });
    });

  test('Integration Test: Roster card click navigation', async () => {
      const mockNavigate = jest.fn();
      jest.spyOn(require('react-router-dom'), 'useNavigate').mockReturnValue(mockNavigate);

      const mockRosters = [
        {
          id: 1,
          flightNumber: 'TK001',
          rosterName: 'Roster 1',
          databaseType: 'SQL',
          createdDate: '2024-01-01',
          rosterData: JSON.stringify({
            summary: { totalPilots: 2, totalCabinCrew: 5, totalPassengers: 100, totalPeople: 107 },
          }),
        },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      healthAPI.checkHealth.mockResolvedValue({ data: { status: 'UP' } });

      renderWithRouter(<Dashboard />);

      await waitFor(() => {
        const rosterCard = screen.getByText(/TK001/i).closest('[class*="MuiCard"]');
        fireEvent.click(rosterCard);
        expect(mockNavigate).toHaveBeenCalledWith('/rosters/1');
      });
    });
});


