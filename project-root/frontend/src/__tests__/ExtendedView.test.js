import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ExtendedView from '../components/ExtendedView';
import { rosterAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getRosterById: jest.fn(),
  },
}));

const mockRoster = {
  id: 1,
  flightNumber: 'FR123',
  databaseType: 'MYSQL',
  rosterData: JSON.stringify({
    flight: {
      flightNumber: 'FR123',
      flightDate: '2025-01-01T10:00:00Z',
      durationMinutes: 120,
      distanceKm: 800,
      sourceAirport: { airportCode: 'AAA' },
      destinationAirport: { airportCode: 'BBB' },
      vehicleType: {
        typeName: 'A320',
        totalSeats: 180,
        businessSeats: 20,
        economySeats: 160,
        maxPilots: 4,
        maxCabinCrew: 10,
      },
    },
    pilots: [{ name: 'Pilot1' }],
    cabinCrew: [{ name: 'Crew1' }],
    passengers: [{ name: 'Passenger1', seatType: 'BUSINESS', isInfant: false }],
    summary: {
      totalPilots: 1,
      totalCabinCrew: 1,
      totalPassengers: 1,
      totalPeople: 3,
    },
  }),
};

const renderWithRouter = () =>
  render(
    <MemoryRouter initialEntries={['/rosters/1/extended']}>
      <Routes>
        <Route path="/rosters/:id/extended" element={<ExtendedView />} />
      </Routes>
    </MemoryRouter>
  );

describe('ExtendedView', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state then extended view data', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/extended view: fr123/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/flight overview/i)).toBeInTheDocument();
  });

  test('shows error when fetch fails', async () => {
    rosterAPI.getRosterById.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter();

    expect(
      await screen.findByText(/failed to fetch roster details\./i)
    ).toBeInTheDocument();
  });
});




