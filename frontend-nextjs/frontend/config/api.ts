
export const TARGET_URL = "http://13.51.85.43:8080";

export const TARGET_URL_LOCAL = "http://localhost:8080";

export const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL || TARGET_URL;

export const API_VERSION = process.env.NEXT_PUBLIC_API_VERSION || 'v1';

export const API_BASE_PATH = `${API_BASE_URL}/api/${API_VERSION}`;

export const API_TIMEOUT = 30000;

export const apiConfig = {
  baseUrl: API_BASE_URL,
  version: API_VERSION,
  basePath: API_BASE_PATH,
  timeout: API_TIMEOUT,
};

export default apiConfig;