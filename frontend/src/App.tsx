import { Routes, Route } from "react-router-dom";
import AuthPage from "./pages/AuthPage";
import Dashboard from "./pages/Dashboard";
import AdminDashboard from "./pages/AdminDashboard";
import PrivateRoute from "./components/PrivateRoute";
import OAuth2SuccessPage from "./pages/OAuth2SuccessPage";
import MyWorkspaces from "./pages/MyWorkspaces";
import WorkspacePage from "./pages/WorkspacePage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<AuthPage />} />
      <Route
        path="/dashboard"
        element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        }
      />
      <Route
        path="/admin"
        element={
          <PrivateRoute>
            <AdminDashboard />
          </PrivateRoute>
        }
      />
      <Route path="/oauth2/success" element={<OAuth2SuccessPage />} />
      <Route
        path="/workspaces"
        element={
          <PrivateRoute>
            <MyWorkspaces />
          </PrivateRoute>
        }
      />

      <Route
        path="/workspace/:id"
        element={
          <PrivateRoute>
            <WorkspacePage />
          </PrivateRoute>
        }
      />
    </Routes>
  );
}

export default App;
