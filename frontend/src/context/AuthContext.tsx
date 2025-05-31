import { createContext, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import axios from 'axios';

// Типы
type User = {
  email: string;
  fullName: string;
  role: 'ADMIN' | 'EDITOR' | 'VIEWER';
};

type AuthContextType = {
  user: User | null;
  isLoggedIn: boolean;
  accessToken: string | null;
  logout: () => void;
  setToken: (token: string) => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [accessToken, setAccessToken] = useState<string | null>(
    localStorage.getItem('accessToken')
  );
  const [user, setUser] = useState<User | null>(null);

  const isLoggedIn = !!accessToken;

  const setToken = (token: string) => {
    localStorage.setItem('accessToken', token);
    setAccessToken(token);
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setAccessToken(null);
    setUser(null);
  };

  // Auto-fetch user on first load if token exists
  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await axios.get('/api/user/me', {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        setUser(res.data);
      } catch (err) {
        console.error('AuthContext fetchMe error', err);
        logout();
      }
    };

    if (accessToken && !user) {
      fetchMe();
    }
  }, [accessToken]);

  return (
    <AuthContext.Provider
      value={{ user, isLoggedIn, accessToken, logout, setToken }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
