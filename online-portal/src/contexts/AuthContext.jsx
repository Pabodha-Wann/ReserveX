import { createContext, useEffect, useState } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { getCurrentUser } from "../services/auth.service";
import { setTokenProvider } from '../services/api';
import AccessDenied from '../components/AccessDenied';

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
    const [accessError, setAccessError] = useState(null);

    useEffect(() => {
        // Pass the token fetcher to api.js so it can attach Bearer tokens without local storage
        setTokenProvider(getAccessTokenSilently);

        const syncAuth0 = async () => {
            if (isLoading) return;

            if (auth0IsAuthenticated && auth0User) {
                // 1. Read roles directly from the Auth0 token (Instant check!)
                const roles = auth0User['https://api.reservex.com/roles'] || [];
                const role = roles[0] || 'Stall Vendor';

                // 2. Kick them out immediately if they are not a Vendor
                if (role !== 'Stall Vendor' && role !== 'VENDOR') {
                    setAccessError("This portal is for Stall Vendors only. Organizers should use the Admin Workspace.");
                    return;
                }

                // 3. Authorized!
                setIsAuthenticated(true);

                // 4. Fetch the extra local DB details in the background
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
        } catch (error) {
            console.error("Failed to refresh user data:", error);
        }
    };

    if (accessError) {
        return <AccessDenied message={accessError} logout={logout} />;
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, logout, login, refreshUser, loading: isLoading }}>
            {children}
        </AuthContext.Provider>
    );
};