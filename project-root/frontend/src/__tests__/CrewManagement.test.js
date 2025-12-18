import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CrewManagement from '../components/CrewManagement';
import { pilotServiceAPI, cabinServiceAPI } from '../services/api';

jest.mock('../services/api', () => ({
  crewAPI: {},
  pilotServiceAPI: {
    getAllPilots: jest.fn(),
  },
  cabinServiceAPI: {
    getAllAttendants: jest.fn(),
  },
}));

const renderCrewManagement = () =>
  render(
    <MemoryRouter>
      <CrewManagement />
    </MemoryRouter>
  );

describe('CrewManagement', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading then shows header and tabs', async () => {
    pilotServiceAPI.getAllPilots.mockResolvedValueOnce({ data: [] });
    cabinServiceAPI.getAllAttendants.mockResolvedValueOnce({ data: [] });

    renderCrewManagement();

    // Loading spinner
    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    await waitFor(() => {
      expect(
        screen.getByText(/crew management/i)
      ).toBeInTheDocument();
    });

    expect(screen.getByText(/pilots/i)).toBeInTheDocument();
    expect(screen.getByText(/cabin crew/i)).toBeInTheDocument();
  });

  test('shows error message when loading crew members fails', async () => {
    pilotServiceAPI.getAllPilots.mockRejectedValueOnce(
      new Error('Network error')
    );
    cabinServiceAPI.getAllAttendants.mockResolvedValueOnce({ data: [] });

    renderCrewManagement();

    expect(
      await screen.findByText(/failed to load crew members from backend/i)
    ).toBeInTheDocument();
  });
});




