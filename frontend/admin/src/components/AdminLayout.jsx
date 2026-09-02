import { useState, useEffect } from 'react';
import { Outlet, useNavigate, Link } from 'react-router-dom';
import Sidebar from './Sidebar';
import { Search, Bell, Moon, UserCircle, ChevronDown, User, LogOut } from 'lucide-react';
import axiosClient from '../api/axiosClient';

export default function AdminLayout({ setIsAuthenticated }) {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);

  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  useEffect(() => {
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
        <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-8 sticky top-0 z-20">
          
          <div className="flex items-center bg-slate-100 px-4 py-2 rounded-lg w-96 border border-slate-200 focus-within:border-blue-500 focus-within:ring-1 focus-within:ring-blue-500 transition-all">
            <Search className="w-5 h-5 text-slate-400 mr-3" />
            <input 
              type="text" 
              placeholder="Search or type command..." 
              className="bg-transparent border-none outline-none text-sm w-full text-slate-700 placeholder-slate-400"
            />
          </div>

          <div className="flex items-center space-x-4">
            {/* <button className="relative text-slate-400 hover:text-slate-600 transition p-2">
              <Bell className="w-5 h-5" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border border-white"></span>
            </button> */}
            <div className="h-8 w-px bg-slate-200 mx-2"></div>
            
            <div className="relative">
              <button 
                onClick={() => setIsProfileMenuOpen(!isProfileMenuOpen)}
                className="flex items-center space-x-3 hover:bg-slate-50 p-2 rounded-lg transition"
              >
                <div className="text-right hidden md:block">
                  <p className="text-sm font-semibold text-slate-700">
                    {profile?.fullName || profile?.username || 'Memuat...'}
                  </p>
                  <p className="text-xs text-slate-500 uppercase tracking-wide">
                    {profile?.role || 'Administrator'}
                  </p>
                </div>
                <UserCircle className="w-9 h-9 text-slate-400" />
                <ChevronDown className="w-4 h-4 text-slate-400" />
              </button>

              {isProfileMenuOpen && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setIsProfileMenuOpen(false)}></div>
                  <div className="absolute right-0 top-full mt-1 w-48 bg-white border border-slate-200 rounded-lg shadow-xl py-1.5 z-50 flex flex-col overflow-hidden">
                    
                    <Link 
                      to="/profile" 
                      onClick={() => setIsProfileMenuOpen(false)}
                      className="flex items-center px-4 py-2.5 text-sm font-medium text-slate-600 hover:bg-slate-50 hover:text-blue-600 transition relative z-50"
                    >
                      <User className="w-4 h-4 mr-3" /> Edit Profil
                    </Link>
                    
                    <div className="border-t border-slate-100 my-1"></div>
                    
                    <button 
                      onClick={handleLogout} 
                      className="flex items-center px-4 py-2.5 text-sm font-medium text-rose-600 hover:bg-rose-50 transition w-full text-left relative z-50"
                    >
                      <LogOut className="w-4 h-4 mr-3" /> Logout
                    </button>
                    
                  </div>
                </>
              )}
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