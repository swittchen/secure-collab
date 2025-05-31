import axios from 'axios';

const API = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true, // если нужны куки (например, refresh token)
});

export const login = (email: string, password: string) =>
  API.post('/auth/login', { email, password });

export const register = (email: string, password: string, fullName: string) =>
  API.post('/auth/register', { email, password, fullName });
