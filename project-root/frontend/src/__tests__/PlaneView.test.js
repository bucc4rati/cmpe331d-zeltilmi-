import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import PlaneView from '../components/PlaneView';
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
        totalSeats: 10,
        seatingPlan: JSON.stringify({
          rows: 2,
          seatsPerRow: 5,
          businessRows: 1,
        }),
      },
    },
    pilots: [],
    cabinCrew: [],
    passengers: [],
  }),
};

const renderWithRouter = () =>
  render(
    <MemoryRouter initialEntries={['/rosters/1/plane']}>
      <Routes>
        <Route path="/rosters/:id/plane" element={<PlaneView />} />
      </Routes>
    </MemoryRouter>
  );

describe('PlaneView', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state then seat legend and statistics', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/plane view: fr123/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/seat legend/i)).toBeInTheDocument();
    expect(screen.getByText(/total passengers/i)).toBeInTheDocument();
  });

  test('shows error message when fetch fails', async () => {
    rosterAPI.getRosterById.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter();

    expect(
      await screen.findByText(/failed to fetch roster details\./i)
    ).toBeInTheDocument();
  });
});




