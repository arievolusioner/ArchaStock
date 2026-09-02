import { useState, useEffect } from "react";
import axiosClient from "../api/axiosClient";
import { User, Key, CheckCircle } from "lucide-react";

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: ''
  });
  const [passwordStatus, setPasswordStatus] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axiosClient.get('/api/auth/me');
        setProfile(response.data);
      } catch (err) {
        console.error("Gagal mengambil profil", err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setPasswordStatus({ type: '', message: '' });

    try {
      await axiosClient.patch('/api/users/change-password', passwordForm);
      setPasswordStatus({ type: 'success', message: 'Password berhasil diperbarui!' });
      setPasswordForm({ oldPassword: '', newPassword: '' });
    } catch (err) {
      setPasswordStatus({ 
        type: 'error', 
        message: err.response?.data?.message || 'Gagal memperbarui password.' 
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <div className="text-slate-500 animate-pulse">Memuat data profil...</div>;

  return (
    <div className="space-y-6 max-w-4xl">
      <h1 className="text-2xl font-bold text-slate-800">Pengaturan Profil</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

        <div className="md:col-span-1 space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 flex flex-col items-center text-center">
            <div className="w-24 h-24 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center mb-4 border-4 border-white shadow-md">
              <User className="w-10 h-10" />
            </div>
            <h2 className="text-lg font-bold text-slate-800">{profile?.fullName || '-'}</h2>
            <p className="text-sm text-slate-500">@{profile?.username}</p>
            <span className="mt-3 px-3 py-1 bg-slate-100 text-slate-600 rounded-full text-xs font-semibold border border-slate-200">
              {profile?.role}
            </span>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
            <h3 className="text-sm font-semibold text-slate-800 mb-4 uppercase tracking-wider">Kontak Sistem</h3>
            <div className="space-y-3 text-sm">
              <div>
                <p className="text-slate-400 text-xs mb-0.5">Nomor Telepon</p>
                <p className="font-medium text-slate-700">{profile?.phoneNumber || 'Belum diatur'}</p>
              </div>
              <div>
                <p className="text-slate-400 text-xs mb-0.5">Status Akun</p>
                <p className="font-medium text-emerald-600">Aktif</p>
              </div>
            </div>
          </div>
        </div>

        {/* Kolom Kanan: Form Ganti Password */}
        <div className="md:col-span-2">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="px-6 py-4 border-b border-slate-100 bg-slate-50 flex items-center">
              <Key className="w-5 h-5 text-slate-400 mr-2" />
              <h3 className="font-semibold text-slate-800">Ubah Password Keamanan</h3>
            </div>
            
            <form onSubmit={handlePasswordChange} className="p-6 space-y-5">
              
              {passwordStatus.message && (
                <div className={`p-4 rounded-lg text-sm flex items-center ${passwordStatus.type === 'success' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200'}`}>
                  {passwordStatus.type === 'success' && <CheckCircle className="w-4 h-4 mr-2" />}
                  {passwordStatus.message}
                </div>
              )}

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Password Lama</label>
                <input 
                  type="password" required 
                  value={passwordForm.oldPassword}
                  onChange={(e) => setPasswordForm({...passwordForm, oldPassword: e.target.value})}
                  className="w-full bg-white border border-slate-300 rounded-lg px-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
                  placeholder="Masukkan password saat ini"
                />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Password Baru</label>
                <input 
                  type="password" required minLength="6"
                  value={passwordForm.newPassword}
                  onChange={(e) => setPasswordForm({...passwordForm, newPassword: e.target.value})}
                  className="w-full bg-white border border-slate-300 rounded-lg px-4 py-2.5 text-sm text-slate-800 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
                  placeholder="Minimal 6 karakter"
                />
              </div>

              <div className="pt-2 flex justify-end">
                <button 
                  type="submit" disabled={isSubmitting}
                  className="px-6 py-2.5 text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition disabled:opacity-50 shadow-sm"
                >
                  {isSubmitting ? 'Memproses...' : 'Perbarui Password'}
                </button>
              </div>
            </form>
          </div>
        </div>

      </div>
    </div>
  );
}