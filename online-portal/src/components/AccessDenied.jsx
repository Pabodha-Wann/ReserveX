import React, { useEffect, useState } from 'react';
import './AccessDenied.css';

const AccessDenied = ({ message, logout }) => {
    const [countdown, setCountdown] = useState(5);

    useEffect(() => {
        if (countdown === 0) {
            logout();
            return;
        }
        
        const timer = setInterval(() => {
            setCountdown(prev => prev - 1);
        }, 1000);

        return () => clearInterval(timer);
    }, [countdown, logout]);

    return (
        <div style={styles.container}>
            <style>{`
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `}</style>
            <div style={styles.card}>
                <div style={styles.iconContainer}>
                    <span style={styles.icon}>⛔</span>
                </div>
                <h1 style={styles.title}>Access Denied</h1>
                <p style={styles.message}>{message}</p>
                <div style={styles.loader}></div>
                <p style={styles.countdownText}>
                    Logging you out securely in {countdown} seconds...
                </p>
            </div>
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        backgroundColor: '#f8fafc',
        fontFamily: "'Inter', sans-serif",
    },
    card: {
        backgroundColor: 'white',
        padding: '50px 40px',
        borderRadius: '16px',
        boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        maxWidth: '450px',
        width: '90%',
        textAlign: 'center',
        borderTop: '5px solid #ef4444',
    },
    iconContainer: {
        width: '80px',
        height: '80px',
        backgroundColor: '#fee2e2',
        borderRadius: '50%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        margin: '0 auto 20px auto',
    },
    icon: {
        fontSize: '40px',
    },
    title: {
        color: '#0f172a',
        fontSize: '24px',
        fontWeight: 'bold',
        margin: '0 0 15px 0',
    },
    message: {
        color: '#475569',
        fontSize: '16px',
        lineHeight: '1.5',
        margin: '0 0 30px 0',
    },
    countdownText: {
        color: '#64748b',
        fontSize: '14px',
        marginBottom: '20px',
    },
    loader: {
        border: '3px solid #f1f5f9',
        borderTop: '3px solid #ef4444',
        borderRadius: '50%',
        width: '24px',
        height: '24px',
        animation: 'spin 1s linear infinite',
        margin: '0 auto 20px auto',
    }
};

export default AccessDenied;
