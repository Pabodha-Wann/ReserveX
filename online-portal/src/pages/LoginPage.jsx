import { useContext } from "react";
import { AuthContext } from "../contexts/AuthContext";
import { LockClosedIcon, ShieldCheckIcon } from "@heroicons/react/24/solid";

const LoginPage = () => {
  const { login } = useContext(AuthContext);

  return (
    <div className="min-h-screen flex w-full">
      {/* Left Side - Image Background */}
      <div className="hidden lg:flex lg:w-1/2 relative bg-gray-900">
        <div className="absolute inset-0 bg-black/40 z-10" />
        <img
          src="https://images.unsplash.com/photo-1507842217343-583bb7270b66?ixlib=rb-4.0.3&auto=format&fit=crop&w=2000&q=80"
          alt="Literary Event Stalls"
          className="absolute inset-0 w-full h-full object-cover z-0"
        />
        <div className="relative z-20 flex flex-col justify-center px-16 text-white w-full">
          <div className="mb-8">
            <span className="bg-blue-600 px-4 py-1.5 rounded-full text-sm font-semibold tracking-wider uppercase">
              ReserveX Portal
            </span>
          </div>
          <h1 className="text-5xl font-bold leading-tight mb-6">
            Sri Lanka’s Largest <br />
            <span className="text-blue-400">Literary Exhibition</span>
          </h1>
          <p className="text-lg text-gray-200 max-w-xl leading-relaxed">
            Secure your stall, showcase your publications, and connect with thousands of readers. Our modernized vendor platform makes managing your reservations easier than ever before.
          </p>
        </div>
      </div>

      {/* Right Side - Login Area */}
      <div className="w-full lg:w-1/2 flex flex-col items-center justify-center bg-white px-6 sm:px-12 py-16">
        <div className="w-full max-w-md">
          {/* Mobile Header (hidden on large screens) */}
          <div className="lg:hidden text-center mb-10">
            <h1 className="text-3xl font-bold text-gray-900">Publisher Portal</h1>
            <p className="text-gray-500 mt-2">Manage your exhibition stalls</p>
          </div>

          <div className="bg-white lg:shadow-xl lg:rounded-3xl lg:border border-gray-100 lg:p-10 p-2">
            <div className="mb-8 text-center">
              <div className="mx-auto w-16 h-16 bg-blue-50 text-blue-600 flex items-center justify-center rounded-full mb-4">
                <ShieldCheckIcon className="w-8 h-8" />
              </div>
              <h2 className="text-2xl font-bold text-gray-900">Welcome Back</h2>
              <p className="text-gray-500 mt-2">
                Sign in to manage your stall reservations. Authentication is securely handled by Auth0.
              </p>
            </div>

            <button
              onClick={() => login()}
              className="w-full py-3.5 px-4 rounded-xl bg-blue-600 hover:bg-blue-700 text-white font-semibold text-lg transition-all duration-200 shadow-md hover:shadow-lg flex items-center justify-center gap-3 transform hover:-translate-y-0.5"
            >
              <LockClosedIcon className="w-5 h-5 opacity-90" />
              Sign In / Register
            </button>

            <div className="mt-8 text-center text-xs text-gray-400 flex flex-col items-center gap-1">
              <span>AUTHORIZED VENDOR PORTAL</span>
              <span>Secure Enterprise OAuth2 Enabled</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
