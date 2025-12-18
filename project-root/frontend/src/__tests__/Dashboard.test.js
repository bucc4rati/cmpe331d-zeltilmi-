import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from '../components/Dashboard';
import { rosterAPI, healthAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getAllRosters: jest.fn(),
  },
  healthAPI: {
    checkHealth: jest.fn(),
  },
}));

const mockRosters = [
  {
    id: 1,
    flightNumber: 'FR123',
    rosterName: 'Morning Flight',
    databaseType: 'MYSQL',
    createdDate: '2025-01-01T10:00:00Z',
    rosterData: JSON.stringify({
      summary: {
        totalPilots: 2,
        totalCabinCrew: 4,
        totalPassengers: 100,
        totalPeople: 106,
      },
    }),
  },
];

const mockHealth = {
  status: 'UP',
  service: 'Main System',
  port: 8080,
};

const renderDashboard = () =>
  render(
    <MemoryRouter>
      <Dashboard />
    </MemoryRouter>
  );

describe('Dashboard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('renders loading state then dashboard data', async () => {
    rosterAPI.getAllRosters.mockResolvedValueOnce({ data: mockRosters });
    healthAPI.checkHealth.mockResolvedValueOnce({ data: mockHealth });

    renderDashboard();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/flight roster dashboard/i)
    ).toBeInTheDocument();
    expect(screen.getByText('FR123')).toBeInTheDocument();
    expect(
      screen.getByText(/system status: up - main system \(port 8080\)/i)
    ).toBeInTheDocument();
  });

  test('shows error message when fetch fails', async () => {
    rosterAPI.getAllRosters.mockRejectedValueOnce(new Error('Network error'));
    healthAPI.checkHealth.mockResolvedValueOnce({ data: mockHealth });

    renderDashboard();

    expect(
      await screen.findByText(/failed to fetch data\. please check if the backend services are running\./i)
    ).toBeInTheDocument();
  });
});




