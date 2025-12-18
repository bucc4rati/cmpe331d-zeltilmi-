import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import CrewSelection from '../components/CrewSelection';
import { crewAPI } from '../services/api';

jest.mock('../services/api', () => ({
  crewAPI: {
    getAvailablePilots: jest.fn(),
    getAvailableCabinCrew: jest.fn(),
    assignCrewManually: jest.fn(),
  },
}));

const mockPilots = [
  { id: 1, name: 'Pilot Senior', seniorityLevel: 'SENIOR' },
  { id: 2, name: 'Pilot Junior', seniorityLevel: 'JUNIOR' },
];

const mockCabinCrew = [
  { id: 10, name: 'Chief', attendantType: 'CHIEF' },
  { id: 11, name: 'Regular1', attendantType: 'REGULAR' },
  { id: 12, name: 'Regular2', attendantType: 'REGULAR' },
  { id: 13, name: 'Regular3', attendantType: 'REGULAR' },
  { id: 14, name: 'Regular4', attendantType: 'REGULAR' },
];

describe('CrewSelection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test("shows warning when user role doesn't have permission", () => {
    localStorage.setItem(
      'user',
      JSON.stringify({ username: 'user', role: 'USER' })
    );

    // Avoid fetch errors in useEffect by returning empty arrays
    crewAPI.getAvailablePilots.mockResolvedValueOnce({ data: [] });
    crewAPI.getAvailableCabinCrew.mockResolvedValueOnce({ data: [] });

    render(<CrewSelection rosterId={1} />);

    expect(
      screen.getByText(/you don't have permission to access crew selection/i)
    ).toBeInTheDocument();
  });

  test('loads crew data and allows manual assignment for MANAGER', async () => {
    localStorage.setItem(
      'user',
      JSON.stringify({ username: 'manager', role: 'MANAGER' })
    );

    crewAPI.getAvailablePilots.mockResolvedValueOnce({ data: mockPilots });
    crewAPI.getAvailableCabinCrew.mockResolvedValueOnce({
      data: mockCabinCrew,
    });
    crewAPI.assignCrewManually.mockResolvedValueOnce({ data: {} });

    render(<CrewSelection rosterId={1} />);

    // Wait until crew selection title is rendered
    const crewSelectionTexts = await screen.findAllByText(/crew selection/i);
    expect(crewSelectionTexts.length).toBeGreaterThan(0);

    // Enable manual mode
    const manualToggle = screen.getByLabelText(/manual crew selection/i);
    fireEvent.click(manualToggle);

    // Directly click Assign Crew; with valid mocks this should call the API
    fireEvent.click(screen.getByRole('button', { name: /assign crew/i }));

    await waitFor(() => {
      expect(crewAPI.assignCrewManually).toHaveBeenCalled();
    });
  });
});


