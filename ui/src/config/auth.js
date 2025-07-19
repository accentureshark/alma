
export const AUTH_CONFIG = {
  TOKEN_KEY: 'auth_token',
  USER_KEY: 'user',
  REMEMBER_ME_KEY: 'remember_me',
  SESSION_TIMEOUT: 30 * 60 * 1000, // 30 minutos
};

export const API_ENDPOINTS = {
  LOGIN: '/api/auth/login',
  LOGOUT: '/api/auth/logout',
  REFRESH: '/api/auth/refresh',
  PROFILE: '/api/auth/profile',
  USER_BY_EMAIL: '/api/auth/user'
};

export const API_BASE_URL = 'http://localhost:8080';
