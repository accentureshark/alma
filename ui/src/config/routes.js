
export const ROUTES = {
  LOGIN: '/login',
  REGISTER: '/register',
  HOME: '/home',
  DASHBOARD: '/dashboard',
  PROFILE: '/profile'
};

export const PUBLIC_ROUTES = [
  ROUTES.LOGIN,
  ROUTES.REGISTER
];

export const PROTECTED_ROUTES = [
  ROUTES.HOME,
  ROUTES.DASHBOARD,
  ROUTES.PROFILE
];
