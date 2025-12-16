import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import CreateRoster from '../CreateRoster';
import { rosterAPI, flightAPI } from '../../services/api';

// Mock the API services
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

// Mock CrewSelection component
jest.mock('../CrewSelection', () => {
  return function MockCrewSelection({ rosterId, onCrewAssigned }) {
    return (
      <div data-testid="crew-selection">
        <p>Crew Selection for Roster {rosterId}</p>
        <button onClick={() => onCrewAssigned && onCrewAssigned({ id: rosterId })}>
          Assign Crew
        </button>
      </div>
    );
  };
});

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('CreateRoster Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  describe('Equivalence Partitioning - Flight Number Selection', () => {
    test('TC1: Valid flight number selection - should enable create button', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
        { flightNumber: 'TK002', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Izmir' }, vehicleType: { typeName: 'Airbus A320' } },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        expect(screen.getByText(/Create New Flight Roster/i)).toBeInTheDocument();
      });

      const flightSelect = screen.getByLabelText(/Flight Number/i);
      fireEvent.mouseDown(flightSelect);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText(/TK001/i));

      const createButton = screen.getByRole('button', { name: /Create Roster/i });
      expect(createButton).not.toBeDisabled();
    });

    test('TC2: No flight number selected - should disable create button', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const createButton = screen.getByRole('button', { name: /Create Roster/i });
        expect(createButton).toBeDisabled();
      });
    });

    test('TC3: Flight with existing roster - should show warning and disable option', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
      ];
      const mockRosters = [{ id: 1, flightNumber: 'TK001' }];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        expect(screen.getByText(/Already exists/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Database Type Selection', () => {
    test('TC4: SQL database type - should be default and selectable', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const dbSelect = screen.getByLabelText(/Database Type/i);
        expect(dbSelect).toHaveTextContent(/SQL/i);
      });
    });

    test('TC5: NoSQL database type - should be selectable', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const dbSelect = screen.getByLabelText(/Database Type/i);
        fireEvent.mouseDown(dbSelect);
      });

      await waitFor(() => {
        expect(screen.getByText(/NoSQL Database/i)).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText(/NoSQL Database/i));
      expect(screen.getByText(/NoSQL Database/i)).toBeInTheDocument();
    });
  });

  describe('Equivalence Partitioning - Search and Filter', () => {
    test('TC6: Valid search query - should filter flights', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
        { flightNumber: 'TK002', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Izmir' }, vehicleType: { typeName: 'Airbus A320' } },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const searchInput = screen.getByPlaceholderText(/Search by flight number/i);
        fireEvent.change(searchInput, { target: { value: 'TK001' } });
      });

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.queryByText(/TK002/i)).not.toBeInTheDocument();
      });
    });

    test('TC7: Empty search query - should show all flights', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
        { flightNumber: 'TK002', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Izmir' }, vehicleType: { typeName: 'Airbus A320' } },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.getByText(/TK002/i)).toBeInTheDocument();
      });
    });

    test('TC8: Aircraft filter - should filter by aircraft type', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
        { flightNumber: 'TK002', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Izmir' }, vehicleType: { typeName: 'Airbus A320' } },
      ];

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const filter737 = screen.getByRole('button', { name: /737/i });
        fireEvent.click(filter737);
      });

      await waitFor(() => {
        expect(screen.getByText(/TK001/i)).toBeInTheDocument();
        expect(screen.queryByText(/TK002/i)).not.toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Form Submission', () => {
    test('TC9: Valid form submission - should create roster successfully', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
      ];
      const mockRoster = { id: 1, flightNumber: 'TK001', databaseType: 'SQL' };

      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
      rosterAPI.createRoster.mockResolvedValue({ data: mockRoster });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const flightSelect = screen.getByLabelText(/Flight Number/i);
        fireEvent.mouseDown(flightSelect);
      });

      await waitFor(() => {
        fireEvent.click(screen.getByText(/TK001/i));
      });

      const form = screen.getByRole('form') || screen.getByText(/Create Roster/i).closest('form');
      fireEvent.submit(form);

      await waitFor(() => {
        expect(rosterAPI.createRoster).toHaveBeenCalledWith('TK001', 'SQL');
      });

      await waitFor(() => {
        expect(screen.getByText(/Roster created successfully/i)).toBeInTheDocument();
      });
    });

    test('TC10: Invalid form submission - missing flight number', async () => {
      rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
      flightAPI.getAllFlights.mockResolvedValue({ data: [] });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const form = screen.getByRole('form') || screen.getByText(/Create Roster/i).closest('form');
        fireEvent.submit(form);
      });

      await waitFor(() => {
        expect(screen.getByText(/Please select a flight number/i)).toBeInTheDocument();
      });

      expect(rosterAPI.createRoster).not.toHaveBeenCalled();
    });

    test('TC11: Duplicate roster - should show error', async () => {
      const mockFlights = [
        { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
      ];
      const mockRosters = [{ id: 1, flightNumber: 'TK001' }];

      rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
      flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

      renderWithRouter(<CreateRoster />);

      await waitFor(() => {
        const flightSelect = screen.getByLabelText(/Flight Number/i);
        fireEvent.mouseDown(flightSelect);
      });

      await waitFor(() => {
        const form = screen.getByRole('form') || screen.getByText(/Create Roster/i).closest('form');
        fireEvent.submit(form);
      });

      await waitFor(() => {
        expect(screen.getByText(/Roster already exists/i)).toBeInTheDocument();
      });
    });
  });
});

describe('CreateRoster Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('Unit Test: fetchAvailableFlights - should handle successful API calls', async () => {
    const mockFlights = [
      { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737', totalSeats: 189 } },
    ];
    const mockRosters = [];

    rosterAPI.getAllRosters.mockResolvedValue({ data: mockRosters });
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

    renderWithRouter(<CreateRoster />);

    await waitFor(() => {
      expect(rosterAPI.getAllRosters).toHaveBeenCalled();
      expect(flightAPI.getAllFlights).toHaveBeenCalled();
    });

    await waitFor(() => {
      expect(screen.getByText(/TK001/i)).toBeInTheDocument();
    });
  });

  test('Unit Test: fetchAvailableFlights - should handle API failure with localStorage fallback', async () => {
    const localStorageFlights = [
      { number: 'TK001', route: 'Istanbul → Ankara', aircraft: 'Boeing 737', capacity: 189 },
    ];

    localStorage.setItem('flights', JSON.stringify(localStorageFlights));
    rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
    flightAPI.getAllFlights.mockRejectedValue(new Error('API Error'));

    renderWithRouter(<CreateRoster />);

    await waitFor(() => {
      expect(screen.getByText(/TK001/i)).toBeInTheDocument();
    });
  });

  test('Unit Test: getFilteredFlights - should filter by search query', async () => {
    const mockFlights = [
      { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
      { flightNumber: 'TK002', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Izmir' }, vehicleType: { typeName: 'Airbus A320' } },
    ];

    rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });

    renderWithRouter(<CreateRoster />);

    await waitFor(() => {
      const searchInput = screen.getByPlaceholderText(/Search by flight number/i);
      fireEvent.change(searchInput, { target: { value: 'Ankara' } });
    });

    await waitFor(() => {
      expect(screen.getByText(/TK001/i)).toBeInTheDocument();
      expect(screen.queryByText(/TK002/i)).not.toBeInTheDocument();
    });
  });

  test('Unit Test: handleSubmit - should handle API error gracefully', async () => {
    const mockFlights = [
      { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
    ];

    rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
    rosterAPI.createRoster.mockRejectedValue({
      response: { data: { message: 'Flight not found' } },
    });

    renderWithRouter(<CreateRoster />);

    await waitFor(() => {
      const flightSelect = screen.getByLabelText(/Flight Number/i);
      fireEvent.mouseDown(flightSelect);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText(/TK001/i));
    });

    const form = screen.getByRole('form') || screen.getByText(/Create Roster/i).closest('form');
    fireEvent.submit(form);

    await waitFor(() => {
      expect(screen.getByText(/Flight not found/i)).toBeInTheDocument();
    });
  });

  test('Integration Test: Complete roster creation flow', async () => {
    const mockFlights = [
      { flightNumber: 'TK001', sourceAirport: { city: 'Istanbul' }, destinationAirport: { city: 'Ankara' }, vehicleType: { typeName: 'Boeing 737' } },
    ];
    const mockRoster = { id: 1, flightNumber: 'TK001', databaseType: 'SQL' };

    rosterAPI.getAllRosters.mockResolvedValue({ data: [] });
    flightAPI.getAllFlights.mockResolvedValue({ data: mockFlights });
    rosterAPI.createRoster.mockResolvedValue({ data: mockRoster });

    renderWithRouter(<CreateRoster />);

    // Select flight
    await waitFor(() => {
      const flightSelect = screen.getByLabelText(/Flight Number/i);
      fireEvent.mouseDown(flightSelect);
    });

    await waitFor(() => {
      fireEvent.click(screen.getByText(/TK001/i));
    });

    // Select database type
    const dbSelect = screen.getByLabelText(/Database Type/i);
    fireEvent.mouseDown(dbSelect);
    await waitFor(() => {
      fireEvent.click(screen.getByText(/NoSQL Database/i));
    });

    // Submit form
    const form = screen.getByRole('form') || screen.getByText(/Create Roster/i).closest('form');
    fireEvent.submit(form);

    await waitFor(() => {
      expect(rosterAPI.createRoster).toHaveBeenCalledWith('TK001', 'NoSQL');
      expect(screen.getByText(/Roster created successfully/i)).toBeInTheDocument();
    });

    // Check crew selection appears
    await waitFor(() => {
      expect(screen.getByTestId('crew-selection')).toBeInTheDocument();
    });
  });
});


