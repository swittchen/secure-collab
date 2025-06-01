import React, { useState } from "react";
import { login, register } from "../api/auth";

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      if (isLogin) {
        const res = await login(email, password);
        localStorage.setItem("accessToken", res.data.accessToken);
        window.location.href = "/dashboard";
      } else {
        await register(email, password, fullName);
        alert("Registration successful, please login.");
        setIsLogin(true);
      }
    } catch (err: any) {
      setError(err?.response?.data?.message || "Authentication failed");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-300 via-white to-green-100 flex items-center justify-center px-4">
      <div className="backdrop-blur-lg bg-white/60 border border-white/30 shadow-2xl rounded-3xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center text-green-700 mb-6 drop-shadow-md">
          SecureCollab
        </h1>

        <div className="flex justify-center mb-6">
          <button
            onClick={() => setIsLogin(true)}
            className={`px-4 py-2 rounded-l-full transition ${
              isLogin ? "bg-green-600 text-white" : "bg-gray-200 text-gray-700"
            }`}
          >
            Login
          </button>
          <button
            onClick={() => setIsLogin(false)}
            className={`px-4 py-2 rounded-r-full transition ${
              !isLogin ? "bg-green-600 text-white" : "bg-gray-200 text-gray-700"
            }`}
          >
            Register
          </button>
        </div>

        {error && <p className="text-red-600 text-sm mb-4">{error}</p>}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <input
            type="email"
            required
            placeholder="Email"
            className="w-full px-4 py-2 rounded-xl bg-white/80 border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-400"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            required
            placeholder="Password"
            className="w-full px-4 py-2 rounded-xl bg-white/80 border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-400"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {!isLogin && (
            <input
              type="text"
              required
              placeholder="Full Name"
              className="w-full px-4 py-2 rounded-xl bg-white/80 border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-400"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
            />
          )}
          <button
            type="submit"
            className="w-full py-2 mt-2 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-xl shadow-md transition"
          >
            {isLogin ? "Log In" : "Register"}
          </button>
        </form>
        {isLogin && (
          <div className="mt-4 text-center">
            <p className="text-sm text-gray-500 mb-2">or</p>
            <a
              href="http://localhost:8080/oauth2/authorization/google"
              className="inline-block px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-xl transition"
            >
              Login with Google
            </a>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuthPage;
