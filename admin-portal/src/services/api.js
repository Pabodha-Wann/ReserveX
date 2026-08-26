import axios from 'axios';

// Create an axios instance
const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Make sure this matches your Spring Boot port
    headers: {
        'Content-Type': 'application/json',
    },
});

let getAccessToken = null;

// Call this from AuthContext when the app loads
export const setTokenProvider = (provider) => {
    getAccessToken = provider;
};

// Add a request interceptor to attach the JWT token
api.interceptors.request.use(
    async (config) => {
        if (getAccessToken) {
            try {
                const token = await getAccessToken();
                config.headers.Authorization = `Bearer ${token}`;
            } catch (error) {
                console.error('Error fetching Auth0 token for API request', error);
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default api;
