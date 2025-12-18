import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CreateRoster from '../components/CreateRoster';
import { rosterAPI, flightAPI } from '../services/api';

jest.mock('../services/api', () => ({
  rosterAPI: {
    getAllRosters: jest.fn(),
    createRoster: jest.fn(),
    getRosterById: jest.fn(),
  },
  flightAPI: {
    getAllFlights: jest.fn(),
  },
}));

const mockFlights = [
  {
    flightNumber: 'FR123',
    sourceAirport: { city: 'Istanbul' },
    destinationAirport: { city: 'Ankara' },
    vehicleType: { typeName: 'Boeing 737', totalSeats: 180 },
  },
];

const mockRoster = {
  id: 1,
  flightNumber: 'FR123',
};

const renderCreateRoster = () =>
  render(
    <MemoryRouter>
      <CreateRoster />
    </MemoryRouter>
  );

describe('CreateRoster', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
  });

  test('renders available flights after loading', async () => {
    renderCreateRoster();

    expect(
      await screen.findByText(/available flights/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/fr123/i)).toBeInTheDocument();
  });

  test('shows validation error when submitting without flight number', async () => {
    renderCreateRoster();

    await screen.findByText(/available flights/i);

    const createButton = screen.getByRole('button', {
      name: /create roster/i,
    });

    // When no flight number is selected, the button should be disabled
    expect(createButton).toBeDisabled();
  });

  test('creates roster successfully and shows success message', async () => {
    rosterAPI.createRoster.mockResolvedValueOnce({ data: mockRoster });

    renderCreateRoster();

    await screen.findByText(/available flights/i);

    // Query the first combobox (flight number select)
    const [select] = screen.getAllByRole('combobox');
    fireEvent.mouseDown(select);
    const option = await screen.findByText(/fr123 - istanbul/i);
    fireEvent.click(option);

    fireEvent.click(
      screen.getByRole('button', { name: /create roster/i })
    );

    expect(
      await screen.findByText(/roster created successfully for flight fr123/i)
    ).toBeInTheDocument();
  });
});


