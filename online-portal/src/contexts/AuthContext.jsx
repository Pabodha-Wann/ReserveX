import { createContext, useEffect, useState } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { getCurrentUser } from "../services/auth.service";
import { setTokenProvider } from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const {
        user: auth0User,
        isAuthenticated: auth0IsAuthenticated,
        isLoading,
        loginWithRedirect,
        logout: auth0Logout,
        getAccessTokenSilently
    } = useAuth0();

    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        // Pass the token fetcher to api.js so it can attach Bearer tokens without local storage
        setTokenProvider(getAccessTokenSilently);

        const syncAuth0 = async () => {
            if (isLoading) return;

            if (auth0IsAuthenticated) {
                setIsAuthenticated(true);
                // Fetch the extra local DB details (like noOfCurrentBookings, businessName)
                await refreshUser();
            } else {
                setIsAuthenticated(false);
                setUser(null);
            }
        };
        syncAuth0();
    }, [auth0IsAuthenticated, isLoading, getAccessTokenSilently]);

    const login = () => {
        loginWithRedirect();
    };

    const logout = () => {
        setIsAuthenticated(false);
        setUser(null);
        auth0Logout({ logoutParams: { returnTo: window.location.origin } });
    };

    const refreshUser = async () => {
        try {
            const userData = await getCurrentUser();
            const updatedUser = {
                ...userData,
                noOfCurrentBookings: userData.noOfCurrentBookings ?? 0
            };
            setUser(updatedUser);
            return updatedUser;
        } catch (error) {
            console.error("Failed to refresh user data:", error);
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, logout, login, refreshUser, loading: isLoading }}>
            {children}
        </AuthContext.Provider>
    );
};