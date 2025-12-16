import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import FlightManagement from '../FlightManagement';
import { flightAPI } from '../../services/api';

// Mock the API service
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

// Mock fetch for checkRosterStatus
global.fetch = jest.fn();

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('FlightManagement Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    global.fetch.mockResolvedValue({
      ok: true,
      json: async () => [],
    });
  });

  describe('Equivalence Partitioning - Flight Loading', () => {
    test('TC1: Successful flight load - should display flights', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
      });
    });

    test('TC2: Empty flights list - should show empty state', async () => {
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        expect(screen.getByText(/No flights found/i)).toBeInTheDocument();
      });
    });

    test('TC3: API error - should show error and fallback to localStorage', async () => {
      const localStorageFlights = [
        { number: 'TK001', route: 'Istanbul → Ankara', aircraft: 'Boeing 737', capacity: 189 },
      ];
      localStorage.setItem('flights', JSON.stringify(localStorageFlights));

      flightAPI.getAllFlights.mockRejectedValue(new Error('API Error'));
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Search Functionality', () => {
    test('TC4: Valid search query - should filter flights', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737' },
        },
        {
          flightNumber: 'TK002',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Izmir' },
          vehicleType: { typeName: 'Airbus A320' },
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const searchInput = screen.getByPlaceholderText(/Search flights/i);
        fireEvent.change(searchInput, { target: { value: 'TK001' } });
      });

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.queryByText(/TK002/i)).not.toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Add Flight', () => {
    test('TC5: Valid flight data - should create flight', async () => {
      const mockAirports = [
        { id: 1, city: 'Istanbul', airportCode: 'IST', airportName: 'Istanbul Airport', country: 'Turkey' },
        { id: 2, city: 'Ankara', airportCode: 'ESB', airportName: 'Ankara Airport', country: 'Turkey' },
      ];
      const mockVehicleTypes = [
        { id: 1, typeName: 'Boeing 737-800', totalSeats: 189 },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: mockAirports });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: mockVehicleTypes });
      flightAPI.createFlight.mockResolvedValue({ data: { flightNumber: 'TK001' } });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add New Flight/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const flightNumberInput = screen.getByLabelText(/Flight Number/i);
        const originInput = screen.getByLabelText(/Origin City/i);
        const destinationInput = screen.getByLabelText(/Destination City/i);

        fireEvent.change(flightNumberInput, { target: { value: 'TK001' } });
        fireEvent.change(originInput, { target: { value: 'Istanbul' } });
        fireEvent.change(destinationInput, { target: { value: 'Ankara' } });
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add Flight/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(flightAPI.createFlight).toHaveBeenCalled();
        expect(screen.getByText(/Flight added successfully/i)).toBeInTheDocument();
      });
    });

    test('TC6: Invalid flight data - missing required fields', async () => {
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add New Flight/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add Flight/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Flight number, origin, and destination are required/i)).toBeInTheDocument();
      });

      expect(flightAPI.createFlight).not.toHaveBeenCalled();
    });

    test('TC7: Invalid airport - should show error', async () => {
      const mockAirports = [
        { id: 1, city: 'Istanbul', airportCode: 'IST' },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: mockAirports });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add New Flight/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const flightNumberInput = screen.getByLabelText(/Flight Number/i);
        const originInput = screen.getByLabelText(/Origin City/i);
        const destinationInput = screen.getByLabelText(/Destination City/i);

        fireEvent.change(flightNumberInput, { target: { value: 'TK001' } });
        fireEvent.change(originInput, { target: { value: 'Istanbul' } });
        fireEvent.change(destinationInput, { target: { value: 'InvalidCity' } });
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add Flight/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Airport not found/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Edit Flight', () => {
    test('TC8: Edit flight without rosters - should allow editing', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
          flightDate: '2024-01-01T10:00:00',
          durationMinutes: 90,
          distanceKm: 350,
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });
      flightAPI.updateFlight.mockResolvedValue({ data: { flightNumber: 'TK001' } });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const editButton = screen.getAllByTitle(/Edit flight/i)[0];
        fireEvent.click(editButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Edit Flight/i)).toBeInTheDocument();
        const flightNumberInput = screen.getByLabelText(/Flight Number/i);
        expect(flightNumberInput).toBeDisabled();
      });
    });

    test('TC9: Edit flight with rosters - should prevent editing', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737' },
          hasRosters: true,
        },
      ];

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => [{ flightNumber: 'TK001' }],
      });

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const editButton = screen.getAllByTitle(/Cannot edit/i)[0];
        expect(editButton).toBeDisabled();
      });
    });
  });

  describe('Equivalence Partitioning - Delete Flight', () => {
    test('TC10: Delete flight without rosters - should allow deletion', async () => {
      window.confirm = jest.fn(() => true);

      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737' },
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });
      flightAPI.deleteFlight.mockResolvedValue({});

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const deleteButton = screen.getAllByTitle(/Delete flight/i)[0];
        fireEvent.click(deleteButton);
      });

      await waitFor(() => {
        expect(window.confirm).toHaveBeenCalled();
        expect(flightAPI.deleteFlight).toHaveBeenCalledWith('TK001');
      });
    });

    test('TC11: Delete flight with rosters - should prevent deletion', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737' },
          hasRosters: true,
        },
      ];

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => [{ flightNumber: 'TK001' }],
      });

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const deleteButton = screen.getAllByTitle(/Cannot delete/i)[0];
        expect(deleteButton).toBeDisabled();
      });
    });
  });
});

describe('FlightManagement Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    global.fetch.mockResolvedValue({
      ok: true,
      json: async () => [],
    });
  });

  test('Unit Test: loadFlights - should format backend flights correctly', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737', totalSeats: 189 },
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/Istanbul → Ankara/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: checkRosterStatus - should update flights with hasRosters flag', async () => {
      const mockFlights = [
        {
          flightNumber: 'TK001',
          sourceAirport: { city: 'Istanbul' },
          destinationAirport: { city: 'Ankara' },
          vehicleType: { typeName: 'Boeing 737' },
        },
      ];

      global.fetch.mockResolvedValue({
        ok: true,
        json: async () => [{ flightNumber: 'TK001' }],
      });

      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      flightAPI.getAllAirports.mockResolvedValue({ data: [] });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: [] });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/rosters');
      });

      await waitFor(() => {
        expect(screen.getByText(/Has Rosters/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: handleSubmit - should format backend data correctly', async () => {
      const mockAirports = [
        {
          id: 1,
          city: 'Istanbul',
          airportCode: 'IST',
          airportName: 'Istanbul Airport',
          country: 'Turkey',
        },
        {
          id: 2,
          city: 'Ankara',
          airportCode: 'ESB',
          airportName: 'Ankara Airport',
          country: 'Turkey',
        },
      ];
      const mockVehicleTypes = [
        {
          id: 1,
          typeName: 'Boeing 737-800',
          totalSeats: 189,
          businessSeats: 20,
          economySeats: 169,
          maxPilots: 4,
          maxCabinCrew: 6,
        },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: mockAirports });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: mockVehicleTypes });
      flightAPI.createFlight.mockResolvedValue({ data: {} });

      renderWithRouter(<FlightManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add New Flight/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const flightNumberInput = screen.getByLabelText(/Flight Number/i);
        const originInput = screen.getByLabelText(/Origin City/i);
        const destinationInput = screen.getByLabelText(/Destination City/i);

        fireEvent.change(flightNumberInput, { target: { value: 'TK001' } });
        fireEvent.change(originInput, { target: { value: 'Istanbul' } });
        fireEvent.change(destinationInput, { target: { value: 'Ankara' } });
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add Flight/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(flightAPI.createFlight).toHaveBeenCalledWith(
          expect.objectContaining({
            flightNumber: 'TK001',
            sourceAirport: expect.objectContaining({ city: 'Istanbul' }),
            destinationAirport: expect.objectContaining({ city: 'Ankara' }),
            vehicleType: expect.objectContaining({ typeName: 'Boeing 737-800' }),
          })
        );
      });
    });

  test('Integration Test: Complete flight management flow', async () => {
      const mockAirports = [
        { id: 1, city: 'Istanbul', airportCode: 'IST', airportName: 'Istanbul Airport', country: 'Turkey' },
        { id: 2, city: 'Ankara', airportCode: 'ESB', airportName: 'Ankara Airport', country: 'Turkey' },
      ];
      const mockVehicleTypes = [
        { id: 1, typeName: 'Boeing 737-800', totalSeats: 189 },
      ];

      flightAPI.getAllFlights.mockResolvedValue({ data: [] });
      flightAPI.getAllAirports.mockResolvedValue({ data: mockAirports });
      flightAPI.getAllVehicleTypes.mockResolvedValue({ data: mockVehicleTypes });
      flightAPI.createFlight.mockResolvedValue({ data: { flightNumber: 'TK001' } });

      renderWithRouter(<FlightManagement />);

      // Add flight
      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add New Flight/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const flightNumberInput = screen.getByLabelText(/Flight Number/i);
        const originInput = screen.getByLabelText(/Origin City/i);
        const destinationInput = screen.getByLabelText(/Destination City/i);

        fireEvent.change(flightNumberInput, { target: { value: 'TK001' } });
        fireEvent.change(originInput, { target: { value: 'Istanbul' } });
        fireEvent.change(destinationInput, { target: { value: 'Ankara' } });
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add Flight/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Flight added successfully/i)).toBeInTheDocument();
      });
    });
});


