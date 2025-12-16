import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from '../Login';
import { authAPI } from '../../services/api';

// Mock the API service
jest.mock('../../services/api');
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => jest.fn(),
}));

// Mock window.location
delete window.location;
window.location = { href: '' };

const renderWithRouter = (component) => {
  return render(<BrowserRouter>{component}</BrowserRouter>);
};

describe('Login Component - Black Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    window.location.href = '';
  });

  describe('Equivalence Partitioning - Username Input', () => {
    test('TC1: Valid username - should accept input', () => {
      authAPI.login.mockResolvedValue({
        data: { token: 'test-token', user: { id: 1, username: 'testuser', role: 'USER' } },
      });

      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });

      expect(usernameInput).toHaveValue('testuser');
    });

    test('TC2: Empty username - should show required validation', () => {
      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      expect(usernameInput).toBeRequired();
    });

    test('TC3: Long username - should accept input', () => {
      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      const longUsername = 'a'.repeat(100);
      fireEvent.change(usernameInput, { target: { value: longUsername } });

      expect(usernameInput).toHaveValue(longUsername);
    });
  });

  describe('Equivalence Partitioning - Password Input', () => {
    test('TC4: Valid password - should accept input', () => {
      renderWithRouter(<Login />);

      const passwordInput = screen.getByLabelText(/Password/i);
      fireEvent.change(passwordInput, { target: { value: 'password123' } });

      expect(passwordInput).toHaveValue('password123');
      expect(passwordInput).toHaveAttribute('type', 'password');
    });

    test('TC5: Empty password - should show required validation', () => {
      renderWithRouter(<Login />);

      const passwordInput = screen.getByLabelText(/Password/i);
      expect(passwordInput).toBeRequired();
    });

    test('TC6: Short password - should accept input', () => {
      renderWithRouter(<Login />);

      const passwordInput = screen.getByLabelText(/Password/i);
      fireEvent.change(passwordInput, { target: { value: '123' } });

      expect(passwordInput).toHaveValue('123');
    });
  });

  describe('Equivalence Partitioning - Form Submission', () => {
    test('TC7: Valid credentials - should login successfully', async () => {
      const mockResponse = {
        data: {
          token: 'test-token-123',
          user: { id: 1, username: 'testuser', role: 'ADMIN' },
        },
      };

      authAPI.login.mockResolvedValue(mockResponse);

      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      const passwordInput = screen.getByLabelText(/Password/i);
      const submitButton = screen.getByRole('button', { name: /Sign In/i });

      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: 'password123' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(authAPI.login).toHaveBeenCalledWith({
          username: 'testuser',
          password: 'password123',
        });
      });

      await waitFor(() => {
        expect(localStorage.getItem('jwtToken')).toBe('test-token-123');
        expect(localStorage.getItem('user')).toBe(JSON.stringify(mockResponse.data.user));
      });
    });

    test('TC8: Invalid credentials - should show error message', async () => {
      authAPI.login.mockRejectedValue({
        response: { data: { error: 'Invalid credentials' } },
      });

      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      const passwordInput = screen.getByLabelText(/Password/i);
      const submitButton = screen.getByRole('button', { name: /Sign In/i });

      fireEvent.change(usernameInput, { target: { value: 'wronguser' } });
      fireEvent.change(passwordInput, { target: { value: 'wrongpass' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/Invalid credentials/i)).toBeInTheDocument();
      });
    });

    test('TC9: Network error - should show generic error', async () => {
      authAPI.login.mockRejectedValue(new Error('Network Error'));

      renderWithRouter(<Login />);

      const usernameInput = screen.getByLabelText(/Username/i);
      const passwordInput = screen.getByLabelText(/Password/i);
      const submitButton = screen.getByRole('button', { name: /Sign In/i });

      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: 'password123' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/Login failed/i)).toBeInTheDocument();
      });
    });

    test('TC10: Empty form submission - should prevent submission', () => {
      renderWithRouter(<Login />);

      const submitButton = screen.getByRole('button', { name: /Sign In/i });
      const form = submitButton.closest('form');

      fireEvent.submit(form);

      expect(authAPI.login).not.toHaveBeenCalled();
    });
  });
});

describe('Login Component - White Box Testing', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.clear();
    window.location.href = '';
  });

  test('Unit Test: handleChange - should update form state', () => {
    renderWithRouter(<Login />);

    const usernameInput = screen.getByLabelText(/Username/i);
    const passwordInput = screen.getByLabelText(/Password/i);

    fireEvent.change(usernameInput, { target: { name: 'username', value: 'newuser' } });
    fireEvent.change(passwordInput, { target: { name: 'password', value: 'newpass' } });

    expect(usernameInput).toHaveValue('newuser');
    expect(passwordInput).toHaveValue('newpass');
  });

  test('Unit Test: handleSubmit - should set loading state', async () => {
    authAPI.login.mockImplementation(() => new Promise((resolve) => setTimeout(() => resolve({
      data: { token: 'token', user: { id: 1, username: 'user' } },
    }), 100)));

    renderWithRouter(<Login />);

    const usernameInput = screen.getByLabelText(/Username/i);
    const passwordInput = screen.getByLabelText(/Password/i);
    const submitButton = screen.getByRole('button', { name: /Sign In/i });

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    expect(submitButton).toBeDisabled();
    expect(screen.getByText(/Signing In/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
  });

  test('Unit Test: handleSubmit - should clear error on new submission', async () => {
    authAPI.login
      .mockRejectedValueOnce({ response: { data: { error: 'First error' } } })
      .mockResolvedValueOnce({
        data: { token: 'token', user: { id: 1, username: 'user' } },
      });

    renderWithRouter(<Login />);

    const usernameInput = screen.getByLabelText(/Username/i);
    const passwordInput = screen.getByLabelText(/Password/i);
    const submitButton = screen.getByRole('button', { name: /Sign In/i });

    // First submission - error
    fireEvent.change(usernameInput, { target: { value: 'wrong' } });
    fireEvent.change(passwordInput, { target: { value: 'wrong' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/First error/i)).toBeInTheDocument();
    });

    // Second submission - success
    fireEvent.change(usernameInput, { target: { value: 'correct' } });
    fireEvent.change(passwordInput, { target: { value: 'correct' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.queryByText(/First error/i)).not.toBeInTheDocument();
    });
  });

  test('Integration Test: Complete login flow with navigation', async () => {
    const mockResponse = {
      data: {
        token: 'test-token',
        user: { id: 1, username: 'testuser', role: 'ADMIN' },
      },
    };

    authAPI.login.mockResolvedValue(mockResponse);

    renderWithRouter(<Login />);

    const usernameInput = screen.getByLabelText(/Username/i);
    const passwordInput = screen.getByLabelText(/Password/i);
    const submitButton = screen.getByRole('button', { name: /Sign In/i });

    // Fill form
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    // Submit
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(authAPI.login).toHaveBeenCalledTimes(1);
      expect(localStorage.getItem('jwtToken')).toBe('test-token');
      expect(localStorage.getItem('user')).toBe(JSON.stringify(mockResponse.data.user));
    });
  });

  test('Integration Test: Register link navigation', () => {
    const mockNavigate = jest.fn();
    jest.spyOn(require('react-router-dom'), 'useNavigate').mockReturnValue(mockNavigate);

    renderWithRouter(<Login />);

    const registerLink = screen.getByText(/Sign up here/i);
    fireEvent.click(registerLink);

    expect(mockNavigate).toHaveBeenCalledWith('/register');
  });
});


