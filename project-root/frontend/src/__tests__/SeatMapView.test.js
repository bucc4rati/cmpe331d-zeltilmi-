import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import SeatMapView from '../components/SeatMapView';
import { rosterAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getRosterById: jest.fn(),
  },
}));

const mockRoster = {
  id: 1,
  flightNumber: 'FR123',
  rosterData: JSON.stringify({
    seatMap: {
      layout: [
        [{ seatNumber: '1A', seatType: 'BUSINESS', isOccupied: false }],
      ],
      totalRows: 1,
      seatsPerRow: 1,
      businessRows: 1,
    },
    passengers: [],
  }),
};

const renderWithRouter = () =>
  render(
    <MemoryRouter initialEntries={['/rosters/1/seats']}>
      <Routes>
        <Route path="/rosters/:id/seats" element={<SeatMapView />} />
      </Routes>
    </MemoryRouter>
  );

describe('SeatMapView', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state then seat map', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/seat map: FR123/i)
    ).toBeInTheDocument();

    // There are multiple "Seat Map" texts (page title + inner component),
    // so we check that at least one exists instead of using getByText
    const seatMapTitles = screen.getAllByText(/seat map/i);
    expect(seatMapTitles.length).toBeGreaterThan(0);
  });

  test('shows error message when fetch fails', async () => {
    rosterAPI.getRosterById.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter();

    expect(
      await screen.findByText(/failed to fetch roster details/i)
    ).toBeInTheDocument();
  });
});


