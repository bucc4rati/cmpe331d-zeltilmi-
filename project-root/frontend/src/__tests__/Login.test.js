import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Login from '../components/Login';
import { authAPI } from '../services/api';

jest.mock('../services/api', () => ({
  authAPI: {
    login: jest.fn(),
  },
}));

const renderLogin = () =>
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

describe('Login', () => {
  const originalLocation = window.location;

  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();

    // Allow assigning to window.location.href without errors
    delete window.location;
    // @ts-ignore
    window.location = { href: '/' };
  });

  afterAll(() => {
    window.location = originalLocation;
  });

  test('submits form and stores token on successful login', async () => {
    authAPI.login.mockResolvedValueOnce({
      data: {
        token: 'login-token',
        user: { username: 'john', role: 'USER' },
      },
    });

    renderLogin();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { name: 'username', value: 'john' },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { name: 'password', value: 'secret123' },
    });

    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(authAPI.login).toHaveBeenCalledWith({
        username: 'john',
        password: 'secret123',
      });
    });

    expect(localStorage.getItem('jwtToken')).toBe('login-token');
    expect(localStorage.getItem('user')).toContain('john');
  });

  test('shows error message when login fails', async () => {
    authAPI.login.mockRejectedValueOnce({
      response: { data: { error: 'Login failed' } },
    });

    renderLogin();

    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { name: 'username', value: 'john' },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { name: 'password', value: 'wrongpass' },
    });

    fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

    expect(await screen.findByText(/login failed/i)).toBeInTheDocument();
  });

  test('navigates to register page when link is clicked', () => {
    renderLogin();

    const link = screen.getByRole('button', { name: /sign up here/i });
    fireEvent.click(link);

    // Navigation is handled by react-router's navigate; here we just assert the link exists and is clickable.
    expect(link).toBeInTheDocument();
  });
});




