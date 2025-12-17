import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Register from '../components/Register';
import { authAPI } from '../services/api';

jest.mock('../services/api', () => ({
  authAPI: {
    register: jest.fn(),
  },
}));

const renderRegister = () =>
  render(
    <MemoryRouter>
      <Register />
    </MemoryRouter>
  );

describe('Register', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
  });

  test('submits form and navigates on successful registration', async () => {
    authAPI.register.mockResolvedValueOnce({
      data: {
        token: 'test-token',
        user: { username: 'john', role: 'USER' },
      },
    });

    renderRegister();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { name: 'username', value: 'john' },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { name: 'email', value: 'john@example.com' },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { name: 'password', value: 'secret123' },
    });

    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(authAPI.register).toHaveBeenCalledWith({
        username: 'john',
        email: 'john@example.com',
        password: 'secret123',
        role: 'USER',
      });
    });

    expect(localStorage.getItem('jwtToken')).toBe('test-token');
    expect(localStorage.getItem('user')).toContain('john');
  });

  test('shows error message when registration fails', async () => {
    authAPI.register.mockRejectedValueOnce({
      response: { data: { error: 'Registration failed' } },
    });

    renderRegister();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { name: 'username', value: 'john' },
    });
    fireEvent.change(screen.getByLabelText(/email/i), {
      target: { name: 'email', value: 'john@example.com' },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { name: 'password', value: 'secret123' },
    });

    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    expect(await screen.findByText(/registration failed/i)).toBeInTheDocument();
  });
});


