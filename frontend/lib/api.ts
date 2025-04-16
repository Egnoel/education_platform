import { getToken } from "@/lib/auth"

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080"

/**
 * Custom API client for making HTTP requests to the backend
 */
export const api = {
  /**
   * Make a GET request
   * @param endpoint - API endpoint to call
   * @param options - Additional fetch options
   * @returns Promise with the response data
   */
  async get(endpoint: string, options = {}) {
    return await request(endpoint, {
      method: "GET",
      ...options,
    })
  },

  /**
   * Make a POST request
   * @param endpoint - API endpoint to call
   * @param data - Data to send in the request body
   * @param options - Additional fetch options
   * @returns Promise with the response data
   */
  async post(endpoint: string, data: any, options = {}) {
    return await request(endpoint, {
      method: "POST",
      body: JSON.stringify(data),
      ...options,
    })
  },

  /**
   * Make a PUT request
   * @param endpoint - API endpoint to call
   * @param data - Data to send in the request body
   * @param options - Additional fetch options
   * @returns Promise with the response data
   */
  async put(endpoint: string, data: any, options = {}) {
    return await request(endpoint, {
      method: "PUT",
      body: JSON.stringify(data),
      ...options,
    })
  },

  /**
   * Make a DELETE request
   * @param endpoint - API endpoint to call
   * @param options - Additional fetch options
   * @returns Promise with the response data
   */
  async delete(endpoint: string, options = {}) {
    return await request(endpoint, {
      method: "DELETE",
      ...options,
    })
  },
}

/**
 * Base request function that handles authentication and error handling
 */
async function request(endpoint: string, options = {}) {
 // const url = `${API_URL}${endpoint.startsWith("/") ? endpoint : `/${endpoint}`}`
  const url = `${API_URL}${endpoint}`
  const token = getToken()

  const headers = {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options as any).headers,
  }

  const config = {
    ...options,
    headers,
  }

  try {
    const response = await fetch(url, config)

    console.log(response.status)
    // Handle 401 Unauthorized - redirect to login
    if (response.status === 401) {
      // Clear token and redirect to login
      localStorage.removeItem("token")
      localStorage.removeItem("user")
      window.location.href = "/login"
      throw new Error("Unauthorized")
    }

    // Handle 403 Forbidden
    if (response.status === 403) {
      throw new Error("Forbidden: You don't have permission to access this resource")
    }

    // Handle 404 Not Found
    if (response.status === 404) {
      throw new Error("Resource not found")
    }

    // Handle 500 Internal Server Error
    if (response.status >= 500) {
      throw new Error("Server error")
    }

    // For other error status codes
    if (!response.ok) {
      throw new Error(`Request failed with status ${response.status}`)
    }

    // Check if the response is JSON
    const contentType = response.headers.get("content-type")
    if (contentType && contentType.includes("application/json")) {
      return await response.json()
    }

    return await response.text()
  } catch (error) {
    console.error("API request error:", error)
    throw error
  }
}
