export interface AuthResponse {
  token: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export type AccountRole = AuthResponse['role'];
