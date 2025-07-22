
export const AUTH_CONFIG = {
  TOKEN_KEY: 'auth_token',
  USER_KEY: 'user',
  REMEMBER_ME_KEY: 'remember_me',
  SESSION_TIMEOUT: 30 * 60 * 1000, // 30 minutos
};

export const API_ENDPOINTS = {
  LOGIN: '/auth/login',
  LOGOUT: '/auth/logout',
  REFRESH: '/auth/refresh',
  PROFILE: '/auth/profile',
  USER_BY_EMAIL: '/auth/user'
};
