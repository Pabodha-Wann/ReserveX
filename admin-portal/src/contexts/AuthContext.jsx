import React, { createContext, useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { setTokenProvider } from '../services/api';

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

    useEffect(() => {
        // Provide the token fetcher to api.js so it can attach Bearer tokens
        setTokenProvider(getAccessTokenSilently);

        const initUser = async () => {
            if (auth0Loading) return;
            
            if (isAuthenticated && auth0User) {
                // Map Auth0 user to what your app expects
                setUser({
                    ...auth0User,
                    email: auth0User.email,
                    role: auth0User['https://api.reservex.com/roles']?.[0] || 'EXHIBITION_ORGANIZER'
                });
            } else {
                setUser(null);
            }
            setLoading(false);
        };
        initUser();
    }, [isAuthenticated, auth0Loading, auth0User, getAccessTokenSilently]);

    const login = async () => {
        await loginWithRedirect();
    };

    const logout = () => {
        auth0Logout({ logoutParams: { returnTo: window.location.origin } });
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};