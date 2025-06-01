import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const OAuth2SuccessPage = () => {
  const navigate = useNavigate();
  const { setToken } = useAuth();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");

    console.log("ACCESS TOKEN =", accessToken);
    console.log("REFRESH TOKEN =", refreshToken);

    if (accessToken && refreshToken) {
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      setToken(accessToken);

      setTimeout(() => {
        navigate("/dashboard");
      }, 500);
    } else {
      console.warn("Missing tokens, redirecting to login");
      navigate("/");
    }
  }, [navigate, setToken]);

  return (
    <div className="flex items-center justify-center h-screen bg-gradient-to-br from-green-100 to-green-300">
      <div className="bg-white p-8 rounded-xl shadow-md text-center w-[320px]">
        <div className="w-10 h-10 border-4 border-green-500 border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
        <h2 className="text-green-700 text-lg font-semibold">Authenticating...</h2>
      </div>
    </div>
  );
};

export default OAuth2SuccessPage;
