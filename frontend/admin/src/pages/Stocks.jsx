import { useState, useEffect } from "react";
import axiosClient from "../api/axiosClient";
import { ArrowRightLeft, PackageSearch } from "lucide-react";

export default function Stocks() {
  const [warehouses, setWarehouses] = useState([]);
  const [selectedWarehouse, setSelectedWarehouse] = useState("");
  
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(false);

  // 1. Ambil daftar gudang saat komponen dimuat
  useEffect(() => {
    const fetchWarehouses = async () => {
      try {
        // Asumsi: Kamu punya endpoint ini di WarehouseResource
        const response = await axiosClient.get('/api/warehouses'); 
        setWarehouses(response.data);
        
        if (response.data.length > 0) {
          setSelectedWarehouse(response.data[0].id); // Pilih gudang pertama otomatis
        }
      } catch (err) {
        console.error("Gagal mengambil data gudang", err);
      }
    };
    fetchWarehouses();
  }, []);

  // 2. Ambil data tabel stok setiap kali gudang yang dipilih berubah
  useEffect(() => {
    if (!selectedWarehouse) return;

    const fetchInventory = async () => {
      setLoading(true);
      try {
        const response = await axiosClient.get(`/api/inventory/table?warehouseId=${selectedWarehouse}`);
        setInventory(response.data);
      } catch (err) {
        console.error("Gagal mengambil data stok", err);
      } finally {
        setLoading(false);
      }
    };
    fetchInventory();
  }, [selectedWarehouse]);

  return (
    <div className="space-y-6">
      
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-slate-800">Manajemen Stock</h1>
        
        {/* Tombol Mutasi (Nanti kita buatkan modalnya) */}
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition shadow-sm flex items-center">
          <ArrowRightLeft className="w-4 h-4 mr-2" /> Mutasi Stok
        </button>
      </div>

      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center space-x-4">
        <label className="text-sm font-medium text-slate-700">Lokasi Gudang:</label>
        <select 
          value={selectedWarehouse} 
          onChange={(e) => setSelectedWarehouse(e.target.value)}
          className="bg-slate-50 border border-slate-300 text-slate-800 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-2 outline-none"
        >
          {warehouses.length === 0 && <option value="">Belum ada gudang</option>}
          {warehouses.map(w => (
            <option key={w.id} value={w.id}>{w.name}</option>
          ))}
        </select>
      </div>

      {/* Tabel Data Stok */}
      <div className="bg-white rounded-xl border border-slate-200 shadow-sm">
        <table className="w-full text-left text-sm text-slate-600">
          <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold">
            <tr>
              <th className="px-6 py-4 rounded-tl-xl">SKU</th>
              <th className="px-6 py-4">Nama Produk</th>
              <th className="px-6 py-4 text-right">Fisik (Total)</th>
              <th className="px-6 py-4 text-right">Dipesan (Reserved)</th>
              <th className="px-6 py-4 text-right rounded-tr-xl">Tersedia</th>
            </tr>
          </thead>
          
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr><td colSpan="5" className="px-6 py-8 text-center text-slate-400">Memuat data stok...</td></tr>
            ) : inventory.length === 0 ? (
              <tr>
                <td colSpan="5" className="px-6 py-12 text-center flex flex-col items-center justify-center">
                  <PackageSearch className="w-12 h-12 text-slate-300 mb-3" />
                  <p className="text-slate-500 font-medium">Stok kosong di gudang ini.</p>
                </td>
              </tr>
            ) : (
              inventory.map((item) => (
                <tr key={item.inventoryId} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 font-mono text-xs text-slate-500">{item.sku}</td>
                  <td className="px-6 py-4 font-medium text-slate-800">{item.productName}</td>
                  <td className="px-6 py-4 text-right font-semibold text-slate-700">{item.quantity}</td>
                  <td className="px-6 py-4 text-right text-rose-500">{item.reservedQuantity}</td>
                  <td className="px-6 py-4 text-right font-bold text-emerald-600">{item.availableQuantity}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

    </div>
  );
}