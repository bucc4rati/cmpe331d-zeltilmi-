import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

// Mock axios to avoid Jest trying to load the real ESM axios module
jest.mock('axios', () => ({
  __esModule: true,
  default: {
    create: jest.fn(() => ({
      get: jest.fn(),
      post: jest.fn(),
      delete: jest.fn(),
    })),
  },
}));

import FlightManagement from '../components/FlightManagement';
import { flightAPI } from '../services/api';

jest.mock('../services/api', () => ({
  flightAPI: {
    getAllFlights: jest.fn(),
    getAllAirports: jest.fn(),
    getAllVehicleTypes: jest.fn(),
    createFlight: jest.fn(),
    updateFlight: jest.fn(),
    deleteFlight: jest.fn(),
  },
}));

const mockFlights = [
  {
    flightNumber: 'FR123',
    flightDate: '2025-01-01T10:00:00Z',
    durationMinutes: 120,
    distanceKm: 800,
    sourceAirport: { city: 'Istanbul', airportName: 'IST' },
    destinationAirport: { city: 'Ankara', airportName: 'ESB' },
    vehicleType: { typeName: 'Boeing 737', totalSeats: 180 },
  },
];

const mockAirports = [
  { id: 1, city: 'Istanbul', airportCode: 'IST', airportName: 'IST', country: 'TR' },
  { id: 2, city: 'Ankara', airportCode: 'ESB', airportName: 'ESB', country: 'TR' },
];

const mockVehicleTypes = [
  {
    id: 1,
    typeName: 'Boeing 737-800',
    totalSeats: 180,
    businessSeats: 20,
    economySeats: 160,
    maxPilots: 4,
    maxCabinCrew: 10,
  },
];

const renderFlightManagement = () =>
  render(
    <MemoryRouter>
      <FlightManagement />
    </MemoryRouter>
  );

describe('FlightManagement', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    jest.clearAllMocks();
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
    flightAPI.getAllAirports.mockResolvedValue({ data: mockAirports });
    flightAPI.getAllVehicleTypes.mockResolvedValue({ data: mockVehicleTypes });
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      json: async () => [],
    });
  });

  afterAll(() => {
    global.fetch = originalFetch;
  });

  test('renders loading state then flight cards', async () => {
    renderFlightManagement();

    expect(screen.getByRole('progressbar')).toBeInTheDocument();

    expect(
      await screen.findByText(/flight management/i)
    ).toBeInTheDocument();
    // Flight cards are loaded asynchronously; wait for the flight number
    expect(
      await screen.findByText(/fr123/i)
    ).toBeInTheDocument();
  });

  test('opens add flight dialog', async () => {
    renderFlightManagement();

    await screen.findByText(/flight management/i);

    // Click the toolbar button
    const [addButton] = screen.getAllByRole('button', {
      name: /add new flight/i,
    });
    fireEvent.click(addButton);

    // Now the dialog should be open with its own title
    const titles = await screen.findAllByText(/add new flight/i);
    expect(titles.length).toBeGreaterThan(0);
    expect(screen.getByLabelText(/flight number/i)).toBeInTheDocument();
  });
});




