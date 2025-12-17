import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import RosterList from '../components/RosterList';
import { rosterAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getAllRosters: jest.fn(),
    deleteRoster: jest.fn(),
    exportRoster: jest.fn(),
  },
}));

const mockRosters = [
  {
    id: 1,
    flightNumber: 'FR123',
    rosterName: 'Morning Flight',
    databaseType: 'MYSQL',
    createdDate: '2025-01-01T10:00:00Z',
    isActive: true,
    rosterData: JSON.stringify({
      pilots: [{ name: 'Pilot1' }],
      cabinCrew: [{ name: 'Crew1' }],
      passengers: [{ name: 'Passenger1' }],
    }),
  },
];

const renderRosterList = () =>
  render(
    <MemoryRouter>
      <RosterList />
    </MemoryRouter>
  );

describe('RosterList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('shows loading state and then renders rosters', async () => {
    rosterAPI.getAllRosters.mockResolvedValueOnce({ data: mockRosters });

    renderRosterList();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(await screen.findByText(/flight rosters/i)).toBeInTheDocument();
    expect(screen.getByText('FR123')).toBeInTheDocument();
  });

  test('shows info when no rosters are found', async () => {
    rosterAPI.getAllRosters.mockResolvedValueOnce({ data: [] });

    renderRosterList();

    expect(await screen.findByText(/no rosters found/i)).toBeInTheDocument();
  });

  test('handles error when fetching rosters fails', async () => {
    rosterAPI.getAllRosters.mockRejectedValueOnce(new Error('Network error'));

    renderRosterList();

    expect(
      await screen.findByText(/failed to fetch rosters/i)
    ).toBeInTheDocument();
  });

  test('opens delete dialog and deletes roster', async () => {
    rosterAPI.getAllRosters.mockResolvedValueOnce({ data: mockRosters });
    rosterAPI.deleteRoster.mockResolvedValueOnce({});

    renderRosterList();

    const deleteButtons = await screen.findAllByTitle(/delete/i);
    fireEvent.click(deleteButtons[0]);

    expect(
      screen.getByText(/are you sure you want to delete the roster/i)
    ).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /delete/i }));

    await waitFor(() => {
      expect(rosterAPI.deleteRoster).toHaveBeenCalledWith(1);
    });
  });
});


