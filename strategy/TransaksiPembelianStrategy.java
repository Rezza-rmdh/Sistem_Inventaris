package strategy;

import entity.Barang;
import service.InventarisService;

public class TransaksiPembelianStrategy implements TransaksiStrategy {
    private Barang barang;
    private int jumlah;
    private String supplier;
    private InventarisService inventarisService;
    
    public TransaksiPembelianStrategy(Barang barang, int jumlah, String supplier, InventarisService inventarisService) {
        this.barang = barang;
        this.jumlah = jumlah;
        this.supplier = supplier;
        this.inventarisService = inventarisService;
    }
    
    @Override
    public void prosesTransaksi() {
        // Pre-condition: jumlahBarang > 0
        if (!validasiTransaksi()) {
            throw new IllegalStateException("Pre-condition prosesTransaksi tidak terpenuhi");
        }
        
        int stokSebelum = barang.getStok(); // @pre value
        barang.updateStok(jumlah);
        inventarisService.updateBarang(barang);
        
        // Post-condition: stok bertambah sesuai jumlah
        if (barang.getStok() != stokSebelum + jumlah) {
            // Rollback
            barang.setStok(stokSebelum);
            inventarisService.updateBarang(barang);
            throw new IllegalStateException("Post-condition prosesTransaksi gagal");
        }
        
        System.out.println("Transaksi pembelian berhasil diproses");
    }
    
    @Override
    public boolean validasiTransaksi() {
        // Validasi OCL: Jumlah barang harus positif
        if (jumlah <= 0) {
            System.out.println("Jumlah harus lebih dari 0");
            return false;
        }
        if (barang == null) {
            System.out.println("Barang tidak ditemukan");
            return false;
        }
        return true;
    }
    
    @Override
    public double hitungTotal() {
        return barang.getHargaBeli() * jumlah;
    }
    
    @Override
    public String getJenisTransaksi() {
        return "PEMBELIAN";
    }
    
    @Override
    public Barang getBarang() {
        return barang;
    }
    
    @Override
    public int getJumlah() {
        return jumlah;
    }
    
    public String getSupplier() {
        return supplier;
    }
}