import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

type Workspace = {
  id: string;
  name: string;
  description: string;
  createdAt: string;
};

export default function WorkspacePage() {
  const { id } = useParams<{ id: string }>();
  const { accessToken } = useAuth();
  const [workspace, setWorkspace] = useState<Workspace | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchWorkspace = async () => {
      try {
        const res = await axios.get(`/api/workspaces/${id}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        setWorkspace(res.data);
      } catch (err: any) {
        console.error(err);
        setError('Failed to load workspace');
      }
    };

    if (id) {
      fetchWorkspace();
    }
  }, [id, accessToken]);

  if (error) {
    return (
      <div className="p-6 text-red-600">
        <p>{error}</p>
        <Link to="/workspaces" className="text-green-700 underline">
          ← Back to My Workspaces
        </Link>
      </div>
    );
  }

  if (!workspace) {
    return (
      <div className="p-6 text-gray-700">
        <p>Loading workspace...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-100 to-green-300 p-6">
      <div className="mb-4">
        <Link to="/workspaces" className="text-green-700 underline">
          ← Back to My Workspaces
        </Link>
      </div>
      <h1 className="text-3xl font-bold text-green-700 mb-2">{workspace.name}</h1>
      <p className="text-gray-700 mb-2">{workspace.description}</p>
      <p className="text-sm text-gray-500 mb-4">
        Created: {new Date(workspace.createdAt).toLocaleDateString()}
      </p>

      <div className="mt-8 text-gray-600 italic">🧱 Здесь позже будет: Chat, Files, Members, Audit Log и т.д.</div>
    </div>
  );
}
