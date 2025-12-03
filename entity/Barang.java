package entity;

import java.io.Serializable;

public class Barang implements Serializable {
    private String id;
    private String nama;
    private String kategori;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    
    public Barang(String id, String nama, String kategori, double hargaBeli, double hargaJual, int stok) {
        // Validasi OCL: Harga jual tidak boleh lebih rendah dari harga beli
        if (hargaJual < hargaBeli) {
            throw new IllegalArgumentException("Harga jual tidak boleh lebih rendah dari harga beli");
        }
        
        // Validasi OCL: Stok barang tidak boleh negatif
        if (stok < 0) {
            throw new IllegalArgumentException("Stok barang tidak boleh negatif");
        }
        
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
    }
    
    // Getter dan Setter dengan validasi
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    
    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }
    
    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { 
        // Validasi OCL: Harga jual tidak boleh lebih rendah dari harga beli
        if (hargaJual < this.hargaBeli) {
            throw new IllegalArgumentException("Harga jual tidak boleh lebih rendah dari harga beli");
        }
        this.hargaJual = hargaJual; 
    }
    
    public int getStok() { return stok; }
    public void setStok(int stok) { 
        // Validasi OCL: Stok barang tidak boleh negatif
        if (stok < 0) {
            throw new IllegalArgumentException("Stok barang tidak boleh negatif");
        }
        this.stok = stok; 
    }
    
    // Method: updateStok(jumlah) dengan validasi OCL
    public void updateStok(int jumlah) {
        // Pre-condition: jumlah <> 0
        if (jumlah == 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh nol");
        }
        
        int stokSebelum = this.stok; // @pre value
        this.stok += jumlah;
        
        // Post-condition: self.stok = self.stok@pre + jumlah
        if (this.stok != stokSebelum + jumlah) {
            this.stok = stokSebelum; // Rollback jika post-condition gagal
            throw new IllegalStateException("Post-condition updateStok gagal");
        }
        
        // Validasi OCL: Stok barang tidak boleh negatif
        if (this.stok < 0) {
            this.stok = stokSebelum; // Rollback
            throw new IllegalArgumentException("Stok barang tidak boleh negatif setelah update");
        }
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s | Nama: %s | Kategori: %s | Harga Beli: Rp %,.2f | Harga Jual: Rp %,.2f | Stok: %d",
                id, nama, kategori, hargaBeli, hargaJual, stok);
    }
}