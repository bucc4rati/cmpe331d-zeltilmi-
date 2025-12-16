import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import CrewSelection from '../CrewSelection';
import { crewAPI } from '../../services/api';

// Mock the API service
jest.mock('../../services/api');

const renderComponent = (props = {}) => {
  return render(<CrewSelection rosterId="1" {...props} />);
};

describe('CrewSelection Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  describe('Equivalence Partitioning - User Role Access', () => {
    test('TC1: USER role - should show permission denied', () => {
      localStorage.setItem('user', JSON.stringify({ role: 'USER' }));

      renderComponent();

      expect(screen.getByText(/You don't have permission/i)).toBeInTheDocument();
    });

    test('TC2: MANAGER role - should allow access', async () => {
      localStorage.setItem('user', JSON.stringify({ role: 'MANAGER' }));
      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText(/Crew Selection/i)).toBeInTheDocument();
      });
    });

    test('TC3: ADMIN role - should allow access', async () => {
      localStorage.setItem('user', JSON.stringify({ role: 'ADMIN' }));
      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText(/Crew Selection/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Pilot Selection', () => {
    beforeEach(() => {
      localStorage.setItem('user', JSON.stringify({ role: 'ADMIN' }));
    });

    test('TC4: Valid pilot selection - should accept selection', async () => {
      const mockPilots = [
        { id: 1, name: 'John Doe', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Jane Smith', seniorityLevel: 'JUNIOR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
      });

      await waitFor(() => {
        expect(screen.getByText(/John Doe/i)).toBeInTheDocument();
      });
    });

    test('TC5: Maximum 2 pilots - should disable additional selection', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
        { id: 3, name: 'Pilot 3', seniorityLevel: 'SENIOR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
      });

      // Select 2 pilots
      await waitFor(() => {
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 2/i));
      });

      // Try to select third pilot
      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        const pilot3Option = screen.getByText(/Pilot 3/i).closest('[role="option"]');
        expect(pilot3Option).toHaveAttribute('aria-disabled', 'true');
      });
    });

    test('TC6: Pilot validation - missing SENIOR pilot', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'JUNIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
      });

      await waitFor(() => {
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/At least 1 SENIOR pilot is required/i)).toBeInTheDocument();
      });
    });

    test('TC7: Pilot validation - missing JUNIOR pilot', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'SENIOR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/At least 1 JUNIOR pilot is required/i)).toBeInTheDocument();
      });
    });

    test('TC8: Pilot validation - too many TRAINEE pilots', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
        { id: 3, name: 'Pilot 3', seniorityLevel: 'TRAINEE' },
        { id: 4, name: 'Pilot 4', seniorityLevel: 'TRAINEE' },
        { id: 5, name: 'Pilot 5', seniorityLevel: 'TRAINEE' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      // Select pilots including 3 TRAINEE
      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 2/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 3/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 4/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 5/i));
      });

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/At most 2 TRAINEE pilots allowed/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Cabin Crew Selection', () => {
    beforeEach(() => {
      localStorage.setItem('user', JSON.stringify({ role: 'ADMIN' }));
    });

    test('TC9: Valid cabin crew selection - should accept selection', async () => {
      const mockCabinCrew = [
        { id: 1, name: 'Attendant 1', attendantType: 'CHIEF' },
        { id: 2, name: 'Attendant 2', attendantType: 'REGULAR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        const crewSelect = screen.getByLabelText(/Select Cabin Crew/i);
        fireEvent.mouseDown(crewSelect);
      });

      await waitFor(() => {
        expect(screen.getByText(/Attendant 1/i)).toBeInTheDocument();
      });
    });

    test('TC10: Cabin crew validation - missing CHIEF', async () => {
      const mockCabinCrew = [
        { id: 1, name: 'Attendant 1', attendantType: 'REGULAR' },
        { id: 2, name: 'Attendant 2', attendantType: 'REGULAR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        const crewSelect = screen.getByLabelText(/Select Cabin Crew/i);
        fireEvent.mouseDown(crewSelect);
        fireEvent.click(screen.getByText(/Attendant 1/i));
      });

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/At least 1 CHIEF attendant is required/i)).toBeInTheDocument();
      });
    });

    test('TC11: Cabin crew validation - too many CHIEF', async () => {
      const mockCabinCrew = Array.from({ length: 6 }, (_, i) => ({
        id: i + 1,
        name: `CHIEF ${i + 1}`,
        attendantType: 'CHIEF',
      }));

      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      // Select 5 CHIEF attendants
      for (let i = 0; i < 5; i++) {
        await waitFor(() => {
          const crewSelect = screen.getByLabelText(/Select Cabin Crew/i);
          fireEvent.mouseDown(crewSelect);
          fireEvent.click(screen.getByText(`CHIEF ${i + 1}`));
        });
      }

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(screen.getByText(/At most 4 CHIEF attendants allowed/i)).toBeInTheDocument();
      });
    });
  });

  describe('Equivalence Partitioning - Assignment Mode', () => {
    beforeEach(() => {
      localStorage.setItem('user', JSON.stringify({ role: 'ADMIN' }));
    });

    test('TC12: Automatic mode - should show automatic message', async () => {
      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        expect(screen.getByText(/Automatic crew assignment/i)).toBeInTheDocument();
      });
    });

    test('TC13: Manual mode toggle - should show selection UI', async () => {
      crewAPI.getAvailablePilots.mockResolvedValue({ data: [] });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: [] });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        expect(screen.getByLabelText(/Select Pilots/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Select Cabin Crew/i)).toBeInTheDocument();
      });
    });
  });
});

describe('CrewSelection Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    localStorage.setItem('user', JSON.stringify({ role: 'ADMIN' }));
  });

  test('Unit Test: fetchCrewData - should fetch both pilots and cabin crew', async () => {
      const mockPilots = [{ id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' }];
      const mockCabinCrew = [{ id: 1, name: 'Crew 1', attendantType: 'CHIEF' }];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });

      renderComponent();

      await waitFor(() => {
        expect(crewAPI.getAvailablePilots).toHaveBeenCalled();
        expect(crewAPI.getAvailableCabinCrew).toHaveBeenCalled();
      });
    });

  test('Unit Test: handleModeChange - should reset selections when switching to automatic', async () => {
      const mockPilots = [{ id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' }];
      const mockCabinCrew = [{ id: 1, name: 'Crew 1', attendantType: 'CHIEF' }];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });

      renderComponent();

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      // Select some crew
      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      // Switch back to automatic
      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      await waitFor(() => {
        expect(screen.getByText(/Automatic assignment enabled/i)).toBeInTheDocument();
      });
    });

  test('Unit Test: handleAssignCrew - should call API with correct data', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
      ];
      const mockCabinCrew = [
        { id: 1, name: 'CHIEF 1', attendantType: 'CHIEF' },
        { id: 2, name: 'REGULAR 1', attendantType: 'REGULAR' },
        { id: 3, name: 'REGULAR 2', attendantType: 'REGULAR' },
        { id: 4, name: 'REGULAR 3', attendantType: 'REGULAR' },
        { id: 5, name: 'REGULAR 4', attendantType: 'REGULAR' },
      ];

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });
      crewAPI.assignCrewManually.mockResolvedValue({ data: { id: 1 } });

      const onCrewAssigned = jest.fn();
      renderComponent({ onCrewAssigned });

      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      // Select pilots
      await waitFor(() => {
        const pilotSelect = screen.getByLabelText(/Select Pilots/i);
        fireEvent.mouseDown(pilotSelect);
        fireEvent.click(screen.getByText(/Pilot 1/i));
      });

      await waitFor(() => {
        fireEvent.mouseDown(screen.getByLabelText(/Select Pilots/i));
        fireEvent.click(screen.getByText(/Pilot 2/i));
      });

      // Select cabin crew
      for (let i = 0; i < 5; i++) {
        await waitFor(() => {
          const crewSelect = screen.getByLabelText(/Select Cabin Crew/i);
          fireEvent.mouseDown(crewSelect);
          fireEvent.click(screen.getByText(new RegExp(`REGULAR ${i + 1}|CHIEF 1`)));
        });
      }

      await waitFor(() => {
        const assignButton = screen.getByRole('button', { name: /Assign Crew/i });
        fireEvent.click(assignButton);
      });

      await waitFor(() => {
        expect(crewAPI.assignCrewManually).toHaveBeenCalledWith('1', {
          pilotIds: [1, 2],
          cabinCrewIds: expect.arrayContaining([1, 2, 3, 4, 5]),
          assignmentType: 'MANUAL',
        });
        expect(onCrewAssigned).toHaveBeenCalled();
      });
    });

  test('Integration Test: Complete crew assignment flow', async () => {
      const mockPilots = [
        { id: 1, name: 'Pilot 1', seniorityLevel: 'SENIOR' },
        { id: 2, name: 'Pilot 2', seniorityLevel: 'JUNIOR' },
      ];
      const mockCabinCrew = Array.from({ length: 5 }, (_, i) => ({
        id: i + 1,
        name: i === 0 ? 'CHIEF 1' : `REGULAR ${i}`,
        attendantType: i === 0 ? 'CHIEF' : 'REGULAR',
      }));

      crewAPI.getAvailablePilots.mockResolvedValue({ data: mockPilots });
      crewAPI.getAvailableCabinCrew.mockResolvedValue({ data: mockCabinCrew });
      crewAPI.assignCrewManually.mockResolvedValue({ data: { id: 1 } });

      renderComponent();

      // Enable manual mode
      await waitFor(() => {
        const switchControl = screen.getByLabelText(/Manual Crew Selection/i);
        fireEvent.click(switchControl);
      });

      // Select crew and assign
      await waitFor(() => {
        expect(screen.getByText(/Crew assigned successfully/i)).toBeInTheDocument();
      });
    });
});


