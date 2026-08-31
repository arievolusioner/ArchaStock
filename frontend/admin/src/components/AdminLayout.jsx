import { useState, useEffect } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import Sidebar from './Sidebar';
import { Search, Bell, Moon, UserCircle } from 'lucide-react';
import axiosClient from '../api/axiosClient';

export default function AdminLayout({ setIsAuthenticated }) {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    // Mengambil data profil user yang sedang login untuk ditampilkan di header
    const fetchProfile = async () => {
      try {
        const response = await axiosClient.get('/api/auth/me');
        setProfile(response.data);
      } catch (err) {
        console.error("Gagal memuat profil header:", err);
      }
    };

    fetchProfile();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsAuthenticated(false);
    navigate('/');
  };

  return (
    <div className="flex h-screen bg-slate-50 text-slate-800 overflow-hidden font-sans">
      
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-y-auto">
        
        <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-8 sticky top-0 z-10">
          
          <div className="flex items-center bg-slate-100 px-4 py-2 rounded-lg w-96 border border-slate-200 focus-within:border-blue-500 focus-within:ring-1 focus-within:ring-blue-500 transition-all">
            <Search className="w-5 h-5 text-slate-400 mr-3" />
            <input 
              type="text" 
              placeholder="Search or type command..." 
              className="bg-transparent border-none outline-none text-sm w-full text-slate-700 placeholder-slate-400"
            />
          </div>

          <div className="flex items-center space-x-6">
            <button className="text-slate-400 hover:text-slate-600 transition">
              <Moon className="w-5 h-5" />
            </button>
            <button className="relative text-slate-400 hover:text-slate-600 transition">
              <Bell className="w-5 h-5" />
              <span className="absolute top-0 right-0 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
            </button>
            
            {/* Bagian Profil Dinamis */}
            <div className="flex items-center space-x-3 pl-4 border-l border-slate-200">
              <div className="text-right hidden md:block">
                <p className="text-sm font-semibold text-slate-700">
                  {profile?.fullName || profile?.username || 'Memuat...'}
                </p>
                <p className="text-xs text-slate-500 uppercase">
                  {profile?.role || 'Administrator'}
                </p>
              </div>
              <button onClick={handleLogout} className="text-slate-400 hover:text-slate-600" title="Logout">
                <UserCircle className="w-10 h-10 text-slate-300" />
              </button>
            </div>
          </div>
        </header>

        <main className="p-8 max-w-7xl mx-auto w-full">
          <Outlet />
        </main>
      </div>
    </div>
  );
}