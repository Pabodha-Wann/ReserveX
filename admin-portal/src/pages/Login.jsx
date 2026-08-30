import React, { useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const { login, user } = useContext(AuthContext);
    const navigate = useNavigate();

    // Redirect to dashboard immediately if already logged in
    useEffect(() => {
        if (user) {
            navigate('/dashboard');
        }
    }, [user, navigate]);

    const handleLoginClick = () => {
        login();
    };

    return (
        <div style={styles.container}>
            {/* Left Side: Image Background */}
            <div style={styles.leftPane}>
                <div style={styles.overlay}></div>
                <div style={styles.leftContent}>
                    <h1 style={styles.heroTitle}>Admin Workspace</h1>
                    <p style={styles.heroSubtitle}>
                        Colombo International Bookfair Management System.
                        Secure access to vendor applications, stall allocations, and event monitoring.
                    </p>
                </div>
            </div>

            {/* Right Side: Login Panel */}
            <div style={styles.rightPane}>
                <div style={styles.card}>
                    <div style={styles.headerContainer}>
                        <img
                            src="/logo.jpeg"
                            alt="Logo"
                            style={styles.logo}
                            onError={(e) => e.target.style.display = 'none'} // Hide if logo missing
                        />
                        <div>
                            <h2 style={styles.title}>Welcome Back</h2>
                            <p style={styles.subtitle}>Secure Enterprise Authentication</p>
                        </div>
                    </div>

                    <button
                        onClick={handleLoginClick}
                        style={styles.button}
                        onMouseOver={(e) => e.currentTarget.style.transform = 'translateY(-2px)'}
                        onMouseOut={(e) => e.currentTarget.style.transform = 'translateY(0)'}
                    >
                        <span style={styles.buttonIcon}>🔒</span> Sign In with Auth0
                    </button>

                    <div style={styles.footerText}>
                        AUTHORIZED PERSONNEL ONLY<br />
                        Protected by OAuth2 & OIDC
                    </div>
                </div>
            </div>
        </div>
    );
};

// Modern, inline CSS styles
const styles = {
    container: {
        display: 'flex',
        height: '100vh',
        width: '100%',
        fontFamily: "'Inter', 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
        backgroundColor: '#ffffff',
    },
    leftPane: {
        flex: 1,
        position: 'relative',
        backgroundImage: 'url("https://images.unsplash.com/photo-1497366216548-37526070297c?ixlib=rb-4.0.3&auto=format&fit=crop&w=2000&q=80")',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        padding: '60px',
    },
    overlay: {
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(15, 23, 42, 0.7)', // Dark slate overlay
        zIndex: 1,
    },
    leftContent: {
        position: 'relative',
        zIndex: 2,
        color: '#ffffff',
        maxWidth: '500px',
    },
    heroTitle: {
        fontSize: '48px',
        fontWeight: '800',
        marginBottom: '20px',
        lineHeight: '1.2',
    },
    heroSubtitle: {
        fontSize: '18px',
        lineHeight: '1.6',
        color: '#e2e8f0',
    },
    rightPane: {
        flex: 1,
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#f8fafc',
    },
    card: {
        width: '100%',
        maxWidth: '420px',
        padding: '50px 40px',
        backgroundColor: '#ffffff',
        borderRadius: '16px',
        boxShadow: '0 10px 25px rgba(0,0,0,0.05)',
        textAlign: 'center',
        border: '1px solid #f1f5f9',
    },
    headerContainer: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '20px',
        marginBottom: '40px',
    },
    logo: {
        height: '64px',
        width: 'auto',
        objectFit: 'contain',
        borderRadius: '8px',
    },
    title: {
        margin: '0 0 8px 0',
        color: '#0f172a',
        fontSize: '28px',
        fontWeight: '700',
    },
    subtitle: {
        margin: 0,
        color: '#64748b',
        fontSize: '15px',
    },
    button: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '12px',
        width: '100%',
        padding: '16px',
        backgroundColor: '#2563eb', // Modern blue
        color: 'white',
        border: 'none',
        borderRadius: '12px',
        fontSize: '16px',
        fontWeight: '600',
        cursor: 'pointer',
        transition: 'all 0.2s ease',
        boxShadow: '0 4px 6px rgba(37, 99, 235, 0.2)',
    },
    buttonIcon: {
        fontSize: '18px',
    },
    footerText: {
        marginTop: '40px',
        fontSize: '12px',
        color: '#94a3b8',
        lineHeight: '1.6',
        textTransform: 'uppercase',
        letterSpacing: '0.05em',
        borderTop: '1px solid #e2e8f0',
        paddingTop: '20px',
    }
};

export default Login;