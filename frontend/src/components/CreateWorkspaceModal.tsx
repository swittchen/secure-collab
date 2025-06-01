import { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

type Props = {
  onCreated: () => void;
};

export default function CreateWorkspaceModal({ onCreated }: Props) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState('');
  const { accessToken } = useAuth();
  const [error, setError] = useState('');

  const handleCreate = async () => {
    setError('');
    try {
      await axios.post(
        '/api/workspaces',
        { name },
        { headers: { Authorization: `Bearer ${accessToken}` } }
      );
      setOpen(false);
      setName('');
      onCreated();
    } catch (err: any) {
      console.error(err);
      setError('Failed to create workspace');
    }
  };

  return (
    <div>
      <button
        onClick={() => setOpen(true)}
        className="px-4 py-2 bg-green-600 text-white rounded-xl hover:bg-green-700"
      >
        + Create Workspace
      </button>

      {open && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
          <div className="bg-white p-6 rounded-xl shadow-xl w-[90%] max-w-md">
            <h2 className="text-xl font-bold text-green-700 mb-4">
              Create New Workspace
            </h2>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Workspace name"
              className="w-full px-4 py-2 border rounded-xl mb-4"
            />
            {error && <div className="text-red-600 mb-2">{error}</div>}
            <div className="flex justify-end gap-3">
              <button
                onClick={() => setOpen(false)}
                className="px-4 py-2 bg-gray-300 rounded-xl"
              >
                Cancel
              </button>
              <button
                onClick={handleCreate}
                className="px-4 py-2 bg-green-600 text-white rounded-xl hover:bg-green-700"
              >
                Create
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
