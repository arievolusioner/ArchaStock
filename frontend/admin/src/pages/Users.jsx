import { useState, useEffect } from "react";
import axiosClient from "../api/axiosClient";
import { MoreVertical, Eye, Edit2, Power, X } from "lucide-react";

export default function Users() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [activeDropdown, setActiveDropdown] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const [modalConfig, setModalConfig] = useState({
    isOpen: false,
    mode: 'ADD',
    userId: null
  });
  
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    password: '',
    phoneNumber: '',
    role: 'KASIR' 
  });

  useEffect(() => {
    fetchUsers();
  }, []);

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

  const openModal = (mode, user = null) => {
    setModalConfig({ isOpen: true, mode, userId: user ? user.id : null });
    
    if (user) {
      setFormData({
        fullName: user.fullName || '',
        username: user.username || '',
        password: '',
        phoneNumber: user.phoneNumber || '',
        role: user.role || 'KASIR'
      });
    } else {
      setFormData({ fullName: '', username: '', password: '', phoneNumber: '', role: 'KASIR' });
    }
    
    setActiveDropdown(null); 
  };

  const closeModal = () => {
    setModalConfig({ ...modalConfig, isOpen: false });
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      if (modalConfig.mode === 'ADD') {
        await axiosClient.post('/api/users', formData);
      } else if (modalConfig.mode === 'EDIT') {
        await axiosClient.put(`/api/users/${modalConfig.userId}`, {
          fullName: formData.fullName,
          phoneNumber: formData.phoneNumber,
          role: formData.role
        });
      }
      
      closeModal();
      fetchUsers();
    } catch (err) {
      alert(err.response?.data?.message || `Gagal ${modalConfig.mode === 'ADD' ? 'menambahkan' : 'mengubah'} data user`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const isViewMode = modalConfig.mode === 'VIEW';

  return (
    <div className="space-y-6">
      
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-slate-800">Manajemen User</h1>
        <button 
          onClick={() => openModal('ADD')}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm"
        >
          + Tambah User
        </button>
      </div>

      {error && (
        <div className="bg-rose-50 border border-rose-200 text-rose-600 p-4 rounded-lg text-sm">
          {error}
        </div>
      )}

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm">
        <table className="w-full text-left text-sm text-slate-600">
          <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
            <tr>
              <th className="px-6 py-4 rounded-tl-xl">Nama Lengkap</th>
              <th className="px-6 py-4">Username</th>
              <th className="px-6 py-4">Role</th>
              <th className="px-6 py-4">Status</th>
              <th className="px-6 py-4 text-center rounded-tr-xl">Aksi</th>
            </tr>
          </thead>
          
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr><td colSpan="5" className="px-6 py-8 text-center text-slate-400">Memuat data...</td></tr>
            ) : users.length === 0 ? (
              <tr><td colSpan="5" className="px-6 py-8 text-center text-slate-400">Belum ada data user.</td></tr>
            ) : (
              users.map((user) => (
                <tr key={user.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 font-medium text-slate-800">{user.fullName || '-'}</td>
                  <td className="px-6 py-4">{user.username}</td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 bg-slate-100 border border-slate-200 text-slate-600 rounded-md text-xs font-medium">
                      {user.role}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    {user.isActive ? (
                      <span className="text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-md text-xs font-medium border border-emerald-200">Aktif</span>
                    ) : (
                      <span className="text-rose-700 bg-rose-50 px-2.5 py-1 rounded-md text-xs font-medium border border-rose-200">Nonaktif</span>
                    )}
                  </td>
                  
                  <td className="px-6 py-4 text-center relative">
                    <button 
                      onClick={() => setActiveDropdown(activeDropdown === user.id ? null : user.id)}
                      className="text-slate-400 hover:text-slate-700 hover:bg-slate-200 p-1.5 rounded-md transition"
                    >
                      <MoreVertical className="w-5 h-5" />
                    </button>

                    {activeDropdown === user.id && (
                      <>
                        <div className="fixed inset-0 z-40" onClick={() => setActiveDropdown(null)}></div>
                        <div className="absolute right-8 top-10 mt-1 w-36 bg-white border border-slate-200 rounded-lg shadow-xl py-1.5 z-50 flex flex-col overflow-hidden">
                          <button 
                            onClick={() => openModal('VIEW', user)}
                            className="flex items-center px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-50 hover:text-blue-600 text-left transition relative z-50"
                          >
                            <Eye className="w-4 h-4 mr-2" /> View
                          </button>
                          <button 
                            onClick={() => openModal('EDIT', user)}
                            className="flex items-center px-4 py-2 text-xs font-medium text-slate-600 hover:bg-slate-50 hover:text-blue-600 text-left transition relative z-50"
                          >
                            <Edit2 className="w-4 h-4 mr-2" /> Edit
                          </button>
                          <button 
                            onClick={() => {
                              toggleUserStatus(user.id, user.isActive);
                              setActiveDropdown(null);
                            }}
                            className={`flex items-center px-4 py-2 text-xs font-medium text-left transition relative z-50 ${
                              user.isActive ? 'text-rose-600 hover:bg-rose-50' : 'text-emerald-600 hover:bg-emerald-50'
                            }`}
                          >
                            <Power className="w-4 h-4 mr-2" /> {user.isActive ? 'Nonaktifkan' : 'Aktifkan'}
                          </button>
                        </div>
                      </>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {modalConfig.isOpen && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-md border border-slate-200 overflow-hidden">
            
            <div className="flex justify-between items-center px-6 py-4 border-b border-slate-100 bg-slate-50">
              <h2 className="text-lg font-bold text-slate-800">
                {modalConfig.mode === 'ADD' ? 'Tambah User Baru' : modalConfig.mode === 'EDIT' ? 'Edit Data User' : 'Detail Profile User'}
              </h2>
              <button onClick={closeModal} className="text-slate-400 hover:text-slate-600 transition">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nama Lengkap</label>
                <input 
                  type="text" name="fullName" required={!isViewMode} readOnly={isViewMode}
                  value={formData.fullName} onChange={handleInputChange}
                  className={`w-full border border-slate-300 rounded-lg px-4 py-2 text-sm text-slate-800 transition ${isViewMode ? 'bg-slate-50 outline-none' : 'bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500'}`}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Username</label>
                <input 
                  type="text" name="username" required={!isViewMode} readOnly={modalConfig.mode !== 'ADD'}
                  value={formData.username} onChange={handleInputChange}
                  className={`w-full border border-slate-300 rounded-lg px-4 py-2 text-sm text-slate-800 transition ${modalConfig.mode !== 'ADD' ? 'bg-slate-100 text-slate-500 cursor-not-allowed outline-none' : 'bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500'}`}
                />
                {modalConfig.mode === 'EDIT' && <p className="text-xs text-slate-400 mt-1">Username tidak dapat diubah.</p>}
              </div>
              {modalConfig.mode === 'ADD' && (
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">Password</label>
                  <input 
                    type="password" name="password" required minLength="6"
                    value={formData.password} onChange={handleInputChange}
                    className="w-full bg-white border border-slate-300 rounded-lg px-4 py-2 text-sm text-slate-800 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
                  />
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Nomor Telepon</label>
                <input 
                  type="text" name="phoneNumber" readOnly={isViewMode}
                  value={formData.phoneNumber} onChange={handleInputChange}
                  className={`w-full border border-slate-300 rounded-lg px-4 py-2 text-sm text-slate-800 transition ${isViewMode ? 'bg-slate-50 outline-none' : 'bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500'}`}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Posisi</label>
                <input 
                  type="text" disabled value={formData.role}
                  className="w-full bg-slate-100 border border-slate-200 rounded-lg px-4 py-2 text-sm text-slate-500 cursor-not-allowed font-medium"
                />
                {modalConfig.mode === 'ADD' && <p className="text-xs text-slate-400 mt-1">Posisi otomatis diatur sebagai Kasir.</p>}
              </div>

              <div className="pt-4 mt-2 border-t border-slate-100 flex justify-end space-x-3">
                <button 
                  type="button" onClick={closeModal}
                  className="px-4 py-2 text-sm font-medium text-slate-600 hover:text-slate-800 bg-white border border-slate-300 hover:bg-slate-50 rounded-lg transition"
                >
                  {isViewMode ? 'Tutup' : 'Batal'}
                </button>
                
                {!isViewMode && (
                  <button 
                    type="submit" disabled={isSubmitting}
                    className="px-4 py-2 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition disabled:opacity-50 flex items-center shadow-sm"
                  >
                    {isSubmitting ? 'Menyimpan...' : 'Simpan Data'}
                  </button>
                )}
              </div>
            </form>
            
          </div>
        </div>
      )}

    </div>
  );
}