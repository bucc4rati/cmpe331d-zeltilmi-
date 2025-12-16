import React from 'react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { render, screen, cleanup } from '@testing-library/react';
import ProtectedRoute from '../components/ProtectedRoute';

describe('ProtectedRoute', () => {
  afterEach(() => {
    localStorage.clear();
    cleanup();
  });

  const renderWithRoutes = (initialPath = '/') =>
    render(
      <MemoryRouter initialEntries={[initialPath]}>
        <Routes>
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <div>Secret Content</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    );

  test('redirects to login when no jwtToken in localStorage', () => {
    renderWithRoutes('/');
    expect(screen.getByText('Login Page')).toBeInTheDocument();
  });

  test('renders children when jwtToken exists', () => {
    localStorage.setItem('jwtToken', 'valid-token');
    renderWithRoutes('/');
    expect(screen.getByText('Secret Content')).toBeInTheDocument();
  });
});

