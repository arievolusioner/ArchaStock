import { Users, Package } from 'lucide-react';

export default function Dashboard() {
  return (
    <div className="space-y-6">
      
      {/* Baris Pertama: Kartu Statistik Kecil */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        
        {/* Card 1: Customers */}
        <div className="bg-white p-6 rounded-xl border border-slate-100 shadow-sm flex flex-col justify-between h-40">
          <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center mb-4">
            <Users className="w-5 h-5 text-slate-600" />
          </div>
          <div>
            <p className="text-sm font-medium text-slate-500 mb-1">Customers</p>
            <div className="flex items-end justify-between">
              <h3 className="text-2xl font-bold text-slate-800">3,782</h3>
              <span className="text-xs font-medium text-emerald-500 bg-emerald-50 px-2 py-1 rounded-md">↑ 11.01%</span>
            </div>
          </div>
        </div>

        {/* Card 2: Orders */}
        <div className="bg-white p-6 rounded-xl border border-slate-100 shadow-sm flex flex-col justify-between h-40">
          <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center mb-4">
            <Package className="w-5 h-5 text-slate-600" />
          </div>
          <div>
            <p className="text-sm font-medium text-slate-500 mb-1">Orders</p>
            <div className="flex items-end justify-between">
              <h3 className="text-2xl font-bold text-slate-800">5,359</h3>
              <span className="text-xs font-medium text-rose-500 bg-rose-50 px-2 py-1 rounded-md">↓ 9.05%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Baris Kedua: Tempat Grafik (Placeholder sementara) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-xl border border-slate-100 shadow-sm lg:col-span-2 min-h-[400px]">
          <div className="flex justify-between items-center mb-6">
            <h3 className="font-bold text-slate-800">Monthly Sales</h3>
            <button className="text-slate-400 hover:text-slate-600">⋮</button>
          </div>
          <div className="w-full h-full flex items-center justify-center border-2 border-dashed border-slate-200 rounded-lg text-slate-400 text-sm">
            [Area Grafik Batang Nanti Di Sini]
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl border border-slate-100 shadow-sm min-h-[400px]">
          <div className="flex justify-between items-center mb-6">
            <h3 className="font-bold text-slate-800">Monthly Target</h3>
            <button className="text-slate-400 hover:text-slate-600">⋮</button>
          </div>
          <div className="w-full h-full flex items-center justify-center border-2 border-dashed border-slate-200 rounded-lg text-slate-400 text-sm">
            [Area Grafik Lingkaran Nanti Di Sini]
          </div>
        </div>
      </div>

    </div>
  );
} 