package strategy;

import entity.Barang;
import service.InventarisService;

public class TransaksiPenjualanStrategy implements TransaksiStrategy {
    private Barang barang;
    private int jumlah;
    private InventarisService inventarisService;
    
    public TransaksiPenjualanStrategy(Barang barang, int jumlah, InventarisService inventarisService) {
        this.barang = barang;
        this.jumlah = jumlah;
        this.inventarisService = inventarisService;
    }
    
    @Override
    public void prosesTransaksi() {
        // Pre-condition: jumlahBarang > 0 dan stok cukup
        if (!validasiTransaksi()) {
            throw new IllegalStateException("Pre-condition prosesTransaksi tidak terpenuhi");
        }
        
        int stokSebelum = barang.getStok(); // @pre value
        barang.updateStok(-jumlah);
        inventarisService.updateBarang(barang);
        
        // Post-condition: stok berkurang sesuai jumlah
        if (barang.getStok() != stokSebelum - jumlah) {
            // Rollback
            barang.setStok(stokSebelum);
            inventarisService.updateBarang(barang);
            throw new IllegalStateException("Post-condition prosesTransaksi gagal");
        }
        
        System.out.println("Transaksi penjualan berhasil diproses");
    }
    
    @Override
    public boolean validasiTransaksi() {
        // Validasi OCL: Jumlah barang harus positif
        if (jumlah <= 0) {
            System.out.println("Jumlah harus lebih dari 0");
            return false;
        }
        
        // Validasi OCL: Stok harus cukup untuk penjualan
        if (barang == null) {
            System.out.println("Barang tidak ditemukan");
            return false;
        }
        if (barang.getStok() < jumlah) {
            System.out.println("Stok tidak mencukupi. Stok tersedia: " + barang.getStok());
            return false;
        }
        return true;
    }
    
    @Override
    public double hitungTotal() {
        return barang.getHargaJual() * jumlah;
    }
    
    @Override
    public String getJenisTransaksi() {
        return "PENJUALAN";
    }
    
    @Override
    public Barang getBarang() {
        return barang;
    }
    
    @Override
    public int getJumlah() {
        return jumlah;
    }
}