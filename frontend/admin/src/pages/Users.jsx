import { useState, useEffect } from "react";
import axiosClient from "../api/axiosClient";

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    // Memasukkan deklarasi fungsi ke dalam useEffect agar aman dari memory leaks / warning
    const fetchUsers = async () => {
      try {
        const response = await axiosClient.get('/api/users');
        setUsers(response.data);
      } catch (err) {
        setError('Gagal mengambil data pengguna');
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const toggleUserStatus = async (id, currentStatus) => {
    try {
      const newStatus = !currentStatus;
      await axiosClient.patch(`/api/users/${id}/status?active=${newStatus}`);
      
      setUsers(users.map(user => 
        user.id === id ? { ...user, isActive: newStatus } : user
      ));
    } catch (err) {
      alert(err.response?.data?.message || 'Gagal mengubah status user');
    }
  };
    
  return (
    <div className="space-y-6">
      
      {/* Header Halaman */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-slate-800">Manajemen User</h1>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm">
          + Tambah User
        </button>
      </div>

      {/* Alert Error */}
      {error && (
        <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg text-sm">
          {error}
        </div>
      )}

      {/* Kontainer Tabel */}
      <div className="bg-white rounded-xl border border-slate-200 overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
              <tr>
                <th className="px-6 py-4">Nama Lengkap</th>
                <th className="px-6 py-4">Username</th>
                <th className="px-6 py-4">Role</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4 text-right">Aksi</th>
              </tr>
            </thead>
            
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-slate-400">Memuat data...</td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-slate-400">Belum ada data user.</td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-slate-800">{user.fullName || '-'}</td>
                    <td className="px-6 py-4">{user.username}</td>
                    
                    {/* Badge Role */}
                    <td className="px-6 py-4">
                      <span className="px-2.5 py-1 bg-slate-100 border border-slate-200 text-slate-600 rounded-md text-xs font-medium">
                        {user.role}
                      </span>
                    </td>
                    
                    {/* Badge Status */}
                    <td className="px-6 py-4">
                      {user.isActive ? (
                        <span className="text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-md text-xs font-medium border border-emerald-200">
                          Aktif
                        </span>
                      ) : (
                        <span className="text-rose-700 bg-rose-50 px-2.5 py-1 rounded-md text-xs font-medium border border-rose-200">
                          Nonaktif
                        </span>
                      )}
                    </td>
                    
                    {/* Tombol Aksi */}
                    <td className="px-6 py-4 text-right space-x-4">
                      <button 
                        onClick={() => toggleUserStatus(user.id, user.isActive)}
                        className={`text-sm font-medium transition ${user.isActive ? 'text-rose-500 hover:text-rose-700' : 'text-emerald-600 hover:text-emerald-800'}`}
                      >
                        {user.isActive ? 'Nonaktifkan' : 'Aktifkan'}
                      </button>
                      <button className="text-blue-600 hover:text-blue-800 text-sm font-medium transition">
                        Edit
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}