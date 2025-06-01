import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const Dashboard = () => {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center text-xl text-green-700 gap-4">
      <div>Добро пожаловать, {user?.fullName}!</div>
      <div className="text-sm text-gray-600">Роль: {user?.role}</div>
      <button
        className="px-4 py-2 bg-red-600 text-white rounded-xl"
        onClick={logout}
      >
        {" "}
        Logout{" "}
      </button>
      {user?.role === "ADMIN" && (
        <Link to="/admin" className="text-green-600 underline">
          Go to Admin Dashboard
        </Link>
      )}
      <Link to="/workspaces" className="text-green-600 underline">
        Go to My Workspaces
      </Link>
    </div>
  );
};

export default Dashboard;
