package service;

import entity.Barang;
import entity.Pengguna;
import factory.ILaporan;
import strategy.TransaksiContext;

import java.util.*;

public class InventarisService {
    private List<Barang> daftarBarang;
    private List<Pengguna> daftarPengguna;
    private List<TransaksiContext> daftarTransaksi;
    private Pengguna penggunaAktif;
    
    public InventarisService() {
        this.daftarBarang = new ArrayList<>();
        this.daftarPengguna = new ArrayList<>();
        this.daftarTransaksi = new ArrayList<>();
        initializeData();
    }
    
    private void initializeData() {
        // Data sample pengguna
        daftarPengguna.add(new Pengguna("U001", "Admin", "admin@system.com", "admin123", "ADMIN"));
        daftarPengguna.add(new Pengguna("U002", "Manager", "manager@system.com", "manager123", "MANAGER"));
        daftarPengguna.add(new Pengguna("U003", "Kasir", "kasir@system.com", "kasir123", "KASIR"));
        
        // Data sample barang
        daftarBarang.add(new Barang("B001", "Laptop ASUS", "ELEKTRONIK", 8000000, 10000000, 10));
        daftarBarang.add(new Barang("B002", "Mouse Wireless", "ELEKTRONIK", 150000, 250000, 25));
        daftarBarang.add(new Barang("B003", "Buku Tulis", "ATK", 5000, 8000, 100));
    }
    
    // Validasi ID unik untuk Barang
    public boolean isIdBarangExist(String id) {
        return daftarBarang.stream().anyMatch(b -> b.getId().equals(id));
    }
    
    // Validasi ID unik untuk Pengguna
    public boolean isIdPenggunaExist(String id) {
        return daftarPengguna.stream().anyMatch(p -> p.getId().equals(id));
    }
    
    // Validasi Email unik untuk Pengguna
    public boolean isEmailExist(String email) {
        return daftarPengguna.stream().anyMatch(p -> p.getEmail().equals(email));
    }
    
    // Authentication methods
    public boolean login(String email, String password) {
        for (Pengguna pengguna : daftarPengguna) {
            if (pengguna.getEmail().equals(email) && pengguna.login(password)) {
                penggunaAktif = pengguna;
                return true;
            }
        }
        return false;
    }
    
    public void logout() {
        penggunaAktif = null;
    }
    
    public Pengguna getPenggunaAktif() {
        return penggunaAktif;
    }
    
    // Barang methods dengan validasi ID unik
    public List<Barang> getSemuaBarang() {
        return new ArrayList<>(daftarBarang);
    }
    
    public Barang findBarangById(String id) {
        return daftarBarang.stream()
            .filter(b -> b.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public void tambahBarang(Barang barang) {
        // Validasi ID unik
        if (isIdBarangExist(barang.getId())) {
            throw new IllegalArgumentException("ID Barang sudah digunakan: " + barang.getId());
        }
        daftarBarang.add(barang);
    }
    
    public void updateBarang(Barang barang) {
        for (int i = 0; i < daftarBarang.size(); i++) {
            if (daftarBarang.get(i).getId().equals(barang.getId())) {
                daftarBarang.set(i, barang);
                break;
            }
        }
    }
    
    public void hapusBarang(String id) {
        daftarBarang.removeIf(b -> b.getId().equals(id));
    }
    
    // Transaksi methods
    public void tambahTransaksi(TransaksiContext transaksi) {
        daftarTransaksi.add(transaksi);
    }
    
    public List<TransaksiContext> getSemuaTransaksi() {
        return new ArrayList<>(daftarTransaksi);
    }
    
    // User management dengan validasi ID dan Email unik
    public List<Pengguna> getSemuaPengguna() {
        return new ArrayList<>(daftarPengguna);
    }
    
    public Pengguna findPenggunaById(String id) {
        return daftarPengguna.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public Pengguna findPenggunaByEmail(String email) {
        return daftarPengguna.stream()
            .filter(p -> p.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }
    
    public void tambahPengguna(Pengguna pengguna) {
        // Validasi ID unik
        if (isIdPenggunaExist(pengguna.getId())) {
            throw new IllegalArgumentException("ID Pengguna sudah digunakan: " + pengguna.getId());
        }
        
        // Validasi Email unik
        if (isEmailExist(pengguna.getEmail())) {
            throw new IllegalArgumentException("Email sudah digunakan: " + pengguna.getEmail());
        }
        
        daftarPengguna.add(pengguna);
    }
    
    public void updatePengguna(Pengguna pengguna) {
        for (int i = 0; i < daftarPengguna.size(); i++) {
            if (daftarPengguna.get(i).getId().equals(pengguna.getId())) {
                // Validasi email unik (kecuali untuk user yang sama)
                Pengguna existingUser = daftarPengguna.get(i);
                if (!existingUser.getEmail().equals(pengguna.getEmail()) && isEmailExist(pengguna.getEmail())) {
                    throw new IllegalArgumentException("Email sudah digunakan: " + pengguna.getEmail());
                }
                daftarPengguna.set(i, pengguna);
                break;
            }
        }
    }
    
    public void hapusPengguna(String id) {
        // Tidak boleh menghapus user yang sedang login
        if (penggunaAktif != null && penggunaAktif.getId().equals(id)) {
            throw new IllegalStateException("Tidak dapat menghapus user yang sedang login");
        }
        daftarPengguna.removeIf(p -> p.getId().equals(id));
    }
    
    // Method tambahan untuk validasi OCL pada Pengguna
    public void inputBarang(Barang barang, Pengguna pengguna) {
        // Pre-condition: user harus login
        if (!pengguna.isLoggedIn()) {
            throw new IllegalStateException("User harus login untuk input barang");
        }
        
        daftarBarang.add(barang);
        
        // Post-condition: barang harus ada dalam daftar
        if (!daftarBarang.contains(barang)) {
            daftarBarang.remove(barang);
            throw new IllegalStateException("Post-condition inputBarang gagal");
        }
    }
    
    public void inputTransaksi(TransaksiContext transaksi, Pengguna pengguna) {
        // Pre-condition: user harus login
        if (!pengguna.isLoggedIn()) {
            throw new IllegalStateException("User harus login untuk input transaksi");
        }
        
        daftarTransaksi.add(transaksi);
        
        // Post-condition: transaksi harus ada dalam daftar
        if (!daftarTransaksi.contains(transaksi)) {
            daftarTransaksi.remove(transaksi);
            throw new IllegalStateException("Post-condition inputTransaksi gagal");
        }
    }
    
    public void lihatLaporan(ILaporan laporan, Pengguna pengguna) {
        // Pre-condition: user harus login
        if (!pengguna.isLoggedIn()) {
            throw new IllegalStateException("User harus login untuk melihat laporan");
        }
        
        // Post-condition: validasi periode laporan
        Map<String, Object> data = laporan.getData();
        Date periodeAwal = (Date) data.get("periodeAwal");
        Date periodeAkhir = (Date) data.get("periodeAkhir");
        
        if (periodeAwal.after(periodeAkhir)) {
            throw new IllegalStateException("Post-condition lihatLaporan gagal: Periode tidak valid");
        }
        
        System.out.println(laporan.generate());
    }
}