import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from '../components/ProtectedRoute';

describe('ProtectedRoute', () => {
  afterEach(() => {
    localStorage.removeItem('jwtToken');
  });

  const renderWithRoutes = () => {
    return render(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route
            path="/protected"
            element={
              <ProtectedRoute>
                <div>Protected Content</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    );
  };

  test('redirects to /login when no jwtToken is present', () => {
    localStorage.removeItem('jwtToken');
    renderWithRoutes();
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  test('renders children when jwtToken is present', () => {
    localStorage.setItem('jwtToken', 'dummy-token');
    renderWithRoutes();
    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });
});


