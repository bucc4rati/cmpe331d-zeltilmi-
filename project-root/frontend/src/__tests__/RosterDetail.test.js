import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import RosterDetail from '../components/RosterDetail';
import { rosterAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getRosterById: jest.fn(),
    exportRoster: jest.fn(),
  },
  crewAPI: {},
}));

const mockRoster = {
  id: 1,
  flightNumber: 'FR123',
  rosterData: JSON.stringify({
    flight: {
      flightNumber: 'FR123',
      flightDate: '2025-01-01T10:00:00Z',
      durationMinutes: 120,
      distanceKm: 800,
      sourceAirport: { airportName: 'A', airportCode: 'AAA' },
      destinationAirport: { airportName: 'B', airportCode: 'BBB' },
      vehicleType: { typeName: 'A320', totalSeats: 180 },
    },
    pilots: [{ name: 'Pilot1', age: 40, gender: 'M', nationality: 'TR' }],
    cabinCrew: [{ name: 'Crew1', age: 30, gender: 'F', nationality: 'TR' }],
    passengers: [{ name: 'Passenger1', age: 25, gender: 'F', nationality: 'TR' }],
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
    <MemoryRouter initialEntries={['/rosters/1']}>
      <Routes>
        <Route path="/rosters/:id" element={<RosterDetail />} />
      </Routes>
    </MemoryRouter>
  );

describe('RosterDetail', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    localStorage.setItem(
      'user',
      JSON.stringify({ username: 'admin', role: 'ADMIN' })
    );
  });

  test('renders loading state then roster details', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/flight roster: FR123/i)
    ).toBeInTheDocument();

    // "Pilots" text appears in multiple places; ensure at least one is rendered
    const pilotsTexts = screen.getAllByText(/pilots/i);
    expect(pilotsTexts.length).toBeGreaterThan(0);
  });

  test('shows error message when fetch fails', async () => {
    rosterAPI.getRosterById.mockRejectedValueOnce(new Error('Network error'));

    renderWithRouter();

    expect(
      await screen.findByText(/failed to fetch roster details/i)
    ).toBeInTheDocument();
  });

  test('opens export dialog when Export JSON is clicked', async () => {
    rosterAPI.getRosterById.mockResolvedValueOnce({ data: mockRoster });

    renderWithRouter();

    const exportButton = await screen.findByRole('button', {
      name: /export json/i,
    });
    fireEvent.click(exportButton);

    expect(screen.getByText(/this will download the roster data/i)).toBeInTheDocument();
  });
});


