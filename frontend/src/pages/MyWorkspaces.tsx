import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import axios from 'axios';
import CreateWorkspaceModal from '../components/CreateWorkspaceModal';

type Workspace = {
  id: string;
  name: string;
  description?: string;
  createdAt?: string;
};

export default function MyWorkspaces() {
  const { accessToken } = useAuth();
  const [workspaces, setWorkspaces] = useState<Workspace[]>([]);
  const [error, setError] = useState('');

  // ⬇️ вынесена наружу
  const fetchWorkspaces = async () => {
    try {
      const res = await axios.get('/api/workspaces', {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      setWorkspaces(res.data);
    } catch (err: any) {
      console.error(err);
      setError('Failed to load workspaces');
    }
  };

  useEffect(() => {
    fetchWorkspaces();
  }, [accessToken]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-100 to-green-300 p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-green-700">My Workspaces</h1>
        <CreateWorkspaceModal onCreated={fetchWorkspaces} />
      </div>

      {error && <div className="text-red-600 mb-4">{error}</div>}

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        {workspaces.map((ws) => (
          <div
            key={ws.id}
            className="bg-white shadow-xl rounded-2xl p-4 border border-gray-200"
          >
            <h2 className="text-xl font-semibold text-green-700">{ws.name}</h2>
            <p className="text-gray-600 mb-2 text-sm">{ws.description}</p>
            <p className="text-xs text-gray-400">
              Created:{' '}
              {ws.createdAt
                ? new Date(ws.createdAt).toLocaleDateString()
                : '—'}
            </p>
            <Link
              to={`/workspace/${ws.id}`}
              className="inline-block mt-3 px-4 py-2 bg-green-600 text-white rounded-xl hover:bg-green-700 text-sm"
            >
              Open Workspace
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}
