import React, { useState, useContext } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { updateProfile } from '../services/auth.service';
import './BusinessNamePrompt.css';

const BusinessNamePrompt = () => {
    const { isAuthenticated, refreshUser, user } = useContext(AuthContext);
    const [businessName, setBusinessName] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!businessName.trim()) {
            setError('Business name cannot be empty.');
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            await updateProfile({ businessName: businessName.trim() });
            await refreshUser(); // Fetch the updated user profile globally
        } catch (err) {
            setError(err || 'Failed to update business name.');
            setIsSubmitting(false);
        }
    };

    // If the user is not logged in, or already has a business name, don't render anything
    if (!isAuthenticated || !user || user.businessName) {
        return null;
    }

    return (
        <div className="business-name-overlay">
            <div className="business-name-modal">
                <h2>Welcome to ReserveX!</h2>
                <p>Before you can start reserving stalls, please provide the name of your organization or business.</p>
                
                <form className="business-name-form" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        className="business-name-input"
                        placeholder="Enter Business Name"
                        value={businessName}
                        onChange={(e) => setBusinessName(e.target.value)}
                        autoFocus
                    />
                    
                    {error && <p className="error-message">{error}</p>}
                    
                    <button 
                        type="submit" 
                        className="business-name-submit"
                        disabled={isSubmitting || !businessName.trim()}
                    >
                        {isSubmitting ? 'Saving...' : 'Continue'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default BusinessNamePrompt;
