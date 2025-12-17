import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import TabularView from '../components/TabularView';
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
      vehicleType: { typeName: 'A320', totalSeats: 180 },
    },
    pilots: [{ name: 'Pilot1', age: 40 }],
    cabinCrew: [{ name: 'Crew1', age: 30 }],
    passengers: [{ name: 'Passenger1', age: 25, isInfant: false }],
  }),
};

const renderWithRouter = () =>
  render(
    <MemoryRouter initialEntries={['/rosters/1/tabular']}>
      <Routes>
        <Route path="/rosters/:id/tabular" element={<TabularView />} />
      </Routes>
    </MemoryRouter>
  );

describe('TabularView', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading state then tabular view', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/tabular view: FR123/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/flight information/i)).toBeInTheDocument();
  });

  test('switches between tabs', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    await screen.findByText(/tabular view: FR123/i);

    fireEvent.click(screen.getByRole('tab', { name: /cabin crew/i }));
    const cabinTexts = screen.getAllByText(/cabin crew \(1\)/i);
    expect(cabinTexts.length).toBeGreaterThan(0);

    fireEvent.click(screen.getByRole('tab', { name: /passengers/i }));
    const passengerTexts = screen.getAllByText(/passengers \(1\)/i);
    expect(passengerTexts.length).toBeGreaterThan(0);
  });

  test('shows error message when fetch fails', async () => {
    rosterAPI.getRosterById.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter();

    expect(
      await screen.findByText(/failed to fetch roster details/i)
    ).toBeInTheDocument();
  });
});


