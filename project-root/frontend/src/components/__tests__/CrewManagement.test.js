import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import CrewManagement from '../CrewManagement';
import { pilotServiceAPI, cabinServiceAPI } from '../../services/api';

// Mock the API services
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('CrewManagement Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    window.confirm = jest.fn(() => true);
  });

  describe('Equivalence Partitioning - Tab Selection', () => {
    test('TC1: Pilots tab - should display pilots', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
      });
    });

    test('TC2: Cabin Crew tab - should display cabin crew', async () => {
      const mockCabinCrew = [
        { id: 1, name: 'Jane Smith', attendantId: 'A001', attendantType: 'CHIEF', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: mockCabinCrew });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const cabinCrewTab = screen.getByRole('tab', { name: /Cabin Crew/i });
        fireEvent.click(cabinCrewTab);
      });

      await waitFor(() => {
        expect(screen.getByText(/Jane Smith/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Search Functionality', () => {
    test('TC3: Valid search query - should filter crew members', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
        { id: 2, name: 'Jane Smith', pilotId: 'P002', seniorityLevel: 'JUNIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const searchInput = screen.getByPlaceholderText(/Search by name/i);
        fireEvent.change(searchInput, { target: { value: 'John' } });
      });

      await waitFor(() => {
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
        expect(screen.queryByText(/Jane Smith/i)).not.toBeInTheDocument();
      });
    });

    test('TC4: Empty search query - should show all members', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
        { id: 2, name: 'Jane Smith', pilotId: 'P002', seniorityLevel: 'JUNIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
        expect(screen.getByText(/Jane Smith/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Add Crew Member', () => {
    test('TC5: Add pilot - should open dialog with pilot form', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add Pilot/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Add Pilot/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/First Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Seniority Level/i)).toBeInTheDocument();
      });
    });

    test('TC6: Add cabin crew - should open dialog with cabin crew form', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const cabinCrewTab = screen.getByRole('tab', { name: /Cabin Crew/i });
        fireEvent.click(cabinCrewTab);
      });

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add Cabin Crew/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Add Cabin Crew/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Position/i)).toBeInTheDocument();
      });
    });

    test('TC7: Invalid form submission - missing name', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add Pilot/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Name and surname are required/i)).toBeInTheDocument();
      });

      expect(pilotServiceAPI.createPilot).not.toHaveBeenCalled();
    });

    test('TC8: Valid form submission - should create pilot', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });
      pilotServiceAPI.createPilot.mockResolvedValue({ data: { id: 1 } });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add Pilot/i });
        fireEvent.click(addButton);
      });

      await waitFor(() => {
        const firstNameInput = screen.getByLabelText(/First Name/i);
        const lastNameInput = screen.getByLabelText(/Last Name/i);
        fireEvent.change(firstNameInput, { target: { value: 'John' } });
        fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
      });

      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(pilotServiceAPI.createPilot).toHaveBeenCalled();
        expect(screen.getByText(/Crew member added successfully/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Edit Crew Member', () => {
    test('TC9: Edit pilot - should populate form with pilot data', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const editButton = screen.getAllByTitle(/Edit/i)[0];
        fireEvent.click(editButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/Edit Pilot/i)).toBeInTheDocument();
        const firstNameInput = screen.getByLabelText(/First Name/i);
        expect(firstNameInput).toHaveValue('John');
      });
    });

    test('TC10: Update pilot - should call update API', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });
      pilotServiceAPI.updatePilot.mockResolvedValue({ data: { id: 1 } });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const editButton = screen.getAllByTitle(/Edit/i)[0];
        fireEvent.click(editButton);
      });

      await waitFor(() => {
        const firstNameInput = screen.getByLabelText(/First Name/i);
        fireEvent.change(firstNameInput, { target: { value: 'Johnny' } });
      });

      await waitFor(() => {
        const updateButton = screen.getByRole('button', { name: /Update/i });
        fireEvent.click(updateButton);
      });

      await waitFor(() => {
        expect(pilotServiceAPI.updatePilot).toHaveBeenCalled();
        expect(screen.getByText(/Crew member updated successfully/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Delete Crew Member', () => {
    test('TC11: Delete pilot - should call delete API', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });
      pilotServiceAPI.deletePilot.mockResolvedValue({});

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const deleteButton = screen.getAllByTitle(/Delete/i)[0];
        fireEvent.click(deleteButton);
      });

      await waitFor(() => {
        expect(window.confirm).toHaveBeenCalled();
        expect(pilotServiceAPI.deletePilot).toHaveBeenCalledWith(1);
      });
    });

    test('TC12: Cancel delete - should not call delete API', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
      ];

      window.confirm.mockReturnValue(false);

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const deleteButton = screen.getAllByTitle(/Delete/i)[0];
        fireEvent.click(deleteButton);
      });

      await waitFor(() => {
        expect(window.confirm).toHaveBeenCalled();
        expect(pilotServiceAPI.deletePilot).not.toHaveBeenCalled();
      });
    });

    test('TC13: Delete unavailable crew - should be disabled', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: false },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const deleteButton = screen.getAllByTitle(/Cannot delete/i)[0];
        expect(deleteButton).toBeDisabled();
      });
    });
  });
});

describe('CrewManagement Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    window.confirm = jest.fn(() => true);
  });

  test('Unit Test: loadCrewMembers - should fetch both pilots and cabin crew', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        expect(pilotServiceAPI.getAllPilots).toHaveBeenCalled();
        expect(cabinServiceAPI.getAllAttendants).toHaveBeenCalled();
      });
    });

  test('Unit Test: filterMembers - should filter by name, surname, or license', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', pilotId: 'P001', seniorityLevel: 'SENIOR', isAvailable: true },
        { id: 2, name: 'Jane Smith', pilotId: 'P002', seniorityLevel: 'JUNIOR', isAvailable: true },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const searchInput = screen.getByPlaceholderText(/Search by name/i);
        fireEvent.change(searchInput, { target: { value: 'P001' } });
      });

      await waitFor(() => {
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
        expect(screen.queryByText(/Jane Smith/i)).not.toBeInTheDocument();
      });
    });

  test('Unit Test: handleOpenDialog - should set form data for edit mode', async () => {
      const mockPilots = [
        {
          id: 1,
          name: 'John Doe',
          pilotId: 'P001',
          seniorityLevel: 'SENIOR',
          maxDistanceKm: 5000,
          age: 30,
          gender: 'Male',
          vehicleRestriction: 'Boeing 737-800',
          nationality: 'Turkish',
          isAvailable: true,
        },
      ];

      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: mockPilots });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });

      renderWithRouter(<CrewManagement />);

      await waitFor(() => {
        const editButton = screen.getAllByTitle(/Edit/i)[0];
        fireEvent.click(editButton);
      });

      await waitFor(() => {
        expect(screen.getByLabelText(/First Name/i)).toHaveValue('John');
        expect(screen.getByLabelText(/Last Name/i)).toHaveValue('Doe');
        expect(screen.getByLabelText(/License ID/i)).toHaveValue('P001');
      });
    });

  test('Integration Test: Complete add pilot flow', async () => {
      pilotServiceAPI.getAllPilots.mockResolvedValue({ data: [] });
      cabinServiceAPI.getAllAttendants.mockResolvedValue({ data: [] });
      pilotServiceAPI.createPilot.mockResolvedValue({ data: { id: 1 } });

      renderWithRouter(<CrewManagement />);

      // Open add dialog
      await waitFor(() => {
        const addButton = screen.getByRole('button', { name: /Add Pilot/i });
        fireEvent.click(addButton);
      });

      // Fill form
      await waitFor(() => {
        const firstNameInput = screen.getByLabelText(/First Name/i);
        const lastNameInput = screen.getByLabelText(/Last Name/i);
        const licenseInput = screen.getByLabelText(/License ID/i);
        const rankSelect = screen.getByLabelText(/Seniority Level/i);

        fireEvent.change(firstNameInput, { target: { value: 'John' } });
        fireEvent.change(lastNameInput, { target: { value: 'Doe' } });
        fireEvent.change(licenseInput, { target: { value: 'P001' } });
        fireEvent.mouseDown(rankSelect);
        fireEvent.click(screen.getByText(/Senior/i));
      });

      // Submit
      await waitFor(() => {
        const submitButton = screen.getByRole('button', { name: /Add/i });
        fireEvent.click(submitButton);
      });

      await waitFor(() => {
        expect(pilotServiceAPI.createPilot).toHaveBeenCalledWith(
          expect.objectContaining({
            name: 'John Doe',
            pilotId: 'P001',
            seniorityLevel: 'SENIOR',
          })
        );
      });
    });
});


