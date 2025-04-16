/**
 * Get the JWT token from localStorage
 * @returns The JWT token or null if not found
 */
export function getToken(): string | null {
  if (typeof window !== "undefined") {
    return localStorage.getItem("token")
  }
  return null
}

/**
 * Set the JWT token in localStorage
 * @param token - The JWT token to store
 */
export function setToken(token: string): void {
  if (typeof window !== "undefined") {
    localStorage.setItem("token", token)
  }
}

/**
 * Remove the JWT token from localStorage
 */
export function removeToken(): void {
  if (typeof window !== "undefined") {
    localStorage.removeItem("token")
  }
}

/**
 * Get the user data from localStorage
 * @returns The user data or null if not found
 */
export function getUser(): any | null {
  if (typeof window !== "undefined") {
    const user = localStorage.getItem("user")
    return user ? JSON.parse(user) : null
  }
  return null
}

/**
 * Set the user data in localStorage
 * @param user - The user data to store
 */
export function setUser(user: any): void {
  if (typeof window !== "undefined") {
    localStorage.setItem("user", JSON.stringify(user))
  }
}

/**
 * Remove the user data from localStorage
 */
export function removeUser(): void {
  if (typeof window !== "undefined") {
    localStorage.removeItem("user")
  }
}

/**
 * Check if the user is authenticated
 * @returns True if the user is authenticated, false otherwise
 */
export function isAuthenticated(): boolean {
  return !!getToken()
}
