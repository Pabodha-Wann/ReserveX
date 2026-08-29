import React, { createContext, useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { setTokenProvider } from '../services/api';
import AccessDenied from '../components/AccessDenied';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const { 
        user: auth0User, 
        isAuthenticated, 
        isLoading: auth0Loading, 
        loginWithRedirect, 
        logout: auth0Logout,
        getAccessTokenSilently
    } = useAuth0();

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [accessError, setAccessError] = useState(null);

    useEffect(() => {
        // Provide the token fetcher to api.js so it can attach Bearer tokens
        setTokenProvider(getAccessTokenSilently);

        const initUser = async () => {
            if (auth0Loading) return;
            
            if (isAuthenticated && auth0User) {
                // Read roles directly from the Auth0 token
                const roles = auth0User['https://api.reservex.com/roles'] || [];
                const role = roles[0] || 'VENDOR';
                
                if (role !== 'Exhibition Organizer' && role !== 'EMPLOYEE') {
                    // Unauthorized: User is not an admin
                    setAccessError("You must be an Exhibition Organizer to access the Admin Workspace.");
                    setLoading(false);
                    return;
                }

                // Authorized
                setUser({
                    id: auth0User.sub,
                    email: auth0User.email,
                    name: auth0User.name,
                    role: 'EMPLOYEE'
                });
            } else {
                setUser(null);
            }
            setLoading(false);
        };
        initUser();
    }, [isAuthenticated, auth0Loading, auth0User, getAccessTokenSilently, auth0Logout]);

    const login = async () => {
        await loginWithRedirect();
    };

    const logout = () => {
        auth0Logout({ logoutParams: { returnTo: window.location.origin } });
    };

    if (accessError) {
        return <AccessDenied message={accessError} logout={logout} />;
    }

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};