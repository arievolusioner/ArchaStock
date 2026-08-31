import { useState } from "react";
import axiosClient from "../api/axiosClient";

export default function Login({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMsg('');
        setLoading(true);

        try {
            const response = await axiosClient.post('/api/auth/login', {
                username,
                password,
            });

            const token = response.data.token;
            localStorage.setItem('token', token);

            onLoginSuccess();

        } catch (err) {
            const backendError = typeof err.response?.data === 'string' 
            ? err.response.data 
            : err.response?.data?.message;

            setErrorMsg(backendError || 'Login gagal. Periksa username/password');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
          <div className="bg-slate-800 p-8 rounded-xl shadow-2xl w-full max-w-md border border-slate-700">
            <h2 className="text-2xl font-bold text-white text-center mb-6">Omnichannel Admin</h2>
            
            {errorMsg && (
              <div className="bg-red-500/10 border border-red-500 text-red-400 p-3 rounded-lg text-sm mb-4">
                {errorMsg}
              </div>
            )}
    
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Username</label>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-blue-500 transition"
                  placeholder="Masukkan username"
                />
              </div>
    
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Password</label>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:border-blue-500 transition"
                  placeholder="••••••••"
                />
              </div>
    
              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 rounded-lg transition duration-200 disabled:opacity-50"
              >
                {loading ? 'Memuat...' : 'Masuk Dashboard'}
              </button>
            </form>
          </div>
        </div>
    );
}