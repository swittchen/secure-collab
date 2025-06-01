import { useEffect, useState } from 'react';
import axios from 'axios';

type User = {
  id: number;
  fullName: string;
  email: string;
  role: string;
};

export default function AdminDashboard() {
  const [users, setUsers] = useState<User[]>([]);

  const fetchUsers = async () => {
    const token = localStorage.getItem('accessToken');
    const res = await axios.get<User[]>('/api/users', {
      headers: { Authorization: `Bearer ${token}` },
    });
    setUsers(res.data);
  };

  useEffect(() => {
    fetchUsers().catch(console.error);
  }, []);

  const changeRole = async (id: number, role: string) => {
    const token = localStorage.getItem('accessToken');
    await axios.patch(`/api/users/${id}/role`, { role }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    await fetchUsers();
  };

  const deleteUser = async (id: number) => {
    const token = localStorage.getItem('accessToken');
    await axios.delete(`/api/users/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    setUsers(users.filter((u) => u.id !== id));
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4 text-green-700">Admin Dashboard</h1>
      <table className="min-w-full border border-gray-300 bg-white shadow-md rounded-xl">
        <thead>
          <tr className="bg-gray-100 text-left">
            <th className="p-3 border-b">ID</th>
            <th className="p-3 border-b">Full Name</th>
            <th className="p-3 border-b">Email</th>
            <th className="p-3 border-b">Role</th>
            <th className="p-3 border-b">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id} className="hover:bg-gray-50">
              <td className="p-3 border-b">{user.id}</td>
              <td className="p-3 border-b">{user.fullName}</td>
              <td className="p-3 border-b">{user.email}</td>
              <td className="p-3 border-b font-semibold">{user.role}</td>
              <td className="p-3 border-b flex gap-2 flex-wrap">
                <button
                  onClick={() => changeRole(user.id, 'ADMIN')}
                  className="px-2 py-1 bg-green-600 text-white text-xs rounded"
                >
                  Promote
                </button>
                <button
                  onClick={() => changeRole(user.id, 'VIEWER')}
                  className="px-2 py-1 bg-yellow-500 text-white text-xs rounded"
                >
                  Demote
                </button>
                <button
                  onClick={() => deleteUser(user.id)}
                  className="px-2 py-1 bg-red-600 text-white text-xs rounded"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
    
  );
}
