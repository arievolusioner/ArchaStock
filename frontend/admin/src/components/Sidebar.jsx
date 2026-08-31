import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Users, ShoppingCart, BarChart2 } from 'lucide-react';
import logoApp from '../assets/logo.webp';

export default function Sidebar() {
  const navLinkClass = ({ isActive }) => 
    `flex items-center px-4 py-2.5 rounded-md text-sm font-medium transition-colors ${
      isActive 
        ? 'bg-blue-50 text-blue-600' 
        : 'text-slate-500 hover:bg-slate-100 hover:text-slate-900'
    }`;

  return (
    <aside className="w-64 bg-white border-r border-slate-200 min-h-screen hidden md:flex flex-col">
      {/* Logo Area */}
      <div className="h-20 flex items-center px-8 border-b border-transparent">
        <div className="flex items-center gap-2">
          <img 
            src={logoApp} 
            alt="Logo ArchaStock" 
            className="w-9 h-9 object-contain"
          />
          <span className="text-xl font-bold text-slate-800 tracking-tight">ArchaStock®</span>
        </div>
      </div>

      {/* Menu Area */}
      <nav className="flex-1 px-4 py-6 space-y-8 overflow-y-auto">
        <div>
          <p className="px-4 text-xs font-semibold text-slate-400 tracking-wider mb-4 uppercase">Menu</p>
          <div className="space-y-1">
            <NavLink to="/dashboard" className={navLinkClass}>
              <LayoutDashboard className="w-5 h-5 mr-3" />
              Dashboard
            </NavLink>
            <NavLink to="/users" className={navLinkClass}>
              <Users className="w-5 h-5 mr-3" />
              Manajemen User
            </NavLink>
            <NavLink to="/ecommerce" className={navLinkClass}>
              <ShoppingCart className="w-5 h-5 mr-3" />
              E-Commerce
            </NavLink>
            <NavLink to="/analytics" className={navLinkClass}>
              <BarChart2 className="w-5 h-5 mr-3" />
              Analytics
            </NavLink>
          </div>
        </div>
      </nav>
    </aside>
  );
}