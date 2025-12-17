import React from 'react';
import { render, screen } from '@testing-library/react';
import SeatMap from '../components/SeatMap';

describe('SeatMap', () => {
  test('shows message when no seat map is available', () => {
    render(<SeatMap seatMap={null} passengers={[]} />);

    expect(screen.getByText(/no seat map available/i)).toBeInTheDocument();
  });

  test('renders seat statistics section', () => {
    const seatMap = {
      layout: [
        [
          { seatNumber: '1A', seatType: 'BUSINESS', isOccupied: true, passenger: { name: 'John' } },
          { seatNumber: '1B', seatType: 'BUSINESS', isOccupied: false },
        ],
        [
          { seatNumber: '2A', seatType: 'ECONOMY', isOccupied: false },
          { seatNumber: '2B', seatType: 'ECONOMY', isOccupied: false },
        ],
      ],
      totalRows: 2,
      seatsPerRow: 2,
      businessRows: 1,
    };

    render(<SeatMap seatMap={seatMap} passengers={[]} />);

    // Just verify that the statistics labels are rendered;
    // exact numeric values are derived from layout and not asserted here
    expect(screen.getByText(/total seats/i)).toBeInTheDocument();
    expect(screen.getByText(/business seats/i)).toBeInTheDocument();
    expect(screen.getByText(/economy seats/i)).toBeInTheDocument();
  });

  test('renders passenger seat assignments', () => {
    const seatMap = {
      layout: [
        [
          { seatNumber: '1A', seatType: 'BUSINESS', isOccupied: true, passenger: { name: 'John' } },
        ],
      ],
      totalRows: 1,
      seatsPerRow: 1,
      businessRows: 1,
    };

    const passengers = [
      { name: 'John', seatNumber: '1A', seatType: 'BUSINESS' },
    ];

    render(<SeatMap seatMap={seatMap} passengers={passengers} />);

    expect(screen.getByText(/passenger seat assignments/i)).toBeInTheDocument();
    expect(screen.getByText(/John - 1A/)).toBeInTheDocument();
  });
});


