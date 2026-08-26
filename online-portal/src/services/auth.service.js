import api from "./api";

// Authentication is now handled by Auth0.
// These methods are left here as stubs to prevent import errors in old components.

export const loginUser = async (email, password) => {
  throw new Error("Authentication is handled by Auth0. Please use the login button.");
};

export const registerUser = async (userData) => {
  throw new Error("Registration is handled by Auth0.");
};

export const logout = () => {
  // Logout is handled by Auth0
};

export const getCurrentUser = async () => {
  try {
    const res = await api.get("/users/me");
    return res.data;
  } catch (err) {
    console.error("getCurrentUser error:", err);
    throw err?.response?.data?.message || "Failed to fetch user data";
  }
};
