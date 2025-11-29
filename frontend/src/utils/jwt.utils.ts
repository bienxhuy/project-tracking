/**
 * Utility functions for JWT token handling
 */

/**
 * Decode JWT token without verification
 * @param token JWT token string
 * @returns Decoded token payload
 */
export function decodeJwt(token: string): any {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Error decoding JWT:', error);
    return null;
  }
}

/**
 * Check if JWT token is expired
 * @param token JWT token string
 * @returns true if token is expired or invalid
 */
export function isTokenExpired(token: string | null): boolean {
  if (!token) return true;

  try {
    const decoded = decodeJwt(token);
    if (!decoded || !decoded.exp) return true;

    // JWT exp is in seconds, Date.now() is in milliseconds
    const expirationTime = decoded.exp * 1000;
    const currentTime = Date.now();

    // Add 1 minute buffer to refresh before actual expiration
    return currentTime >= expirationTime - 60000;
  } catch (error) {
    console.error('Error checking token expiration:', error);
    return true;
  }
}

/**
 * Get token expiration time in readable format
 * @param token JWT token string
 * @returns Expiration date string or null
 */
export function getTokenExpiration(token: string | null): string | null {
  if (!token) return null;

  try {
    const decoded = decodeJwt(token);
    if (!decoded || !decoded.exp) return null;

    const expirationTime = decoded.exp * 1000;
    return new Date(expirationTime).toISOString();
  } catch (error) {
    return null;
  }
}

/**
 * Get user ID from JWT token
 * @param token JWT token string
 * @returns User ID or null
 */
export function getUserIdFromToken(token: string | null): string | null {
  if (!token) return null;

  try {
    const decoded = decodeJwt(token);
    return decoded?.sub || decoded?.userId || null;
  } catch (error) {
    return null;
  }
}
