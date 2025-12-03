package entity;

import java.io.Serializable;

public class Pengguna implements Serializable {
    private String id;
    private String nama;
    private String email;
    private String password;
    private String role; // ADMIN, KASIR, MANAGER
    private boolean isLoggedIn = false;
    
    public Pengguna(String id, String nama, String email, String password, String role) {
        // Validasi OCL: Email minimal 5 karakter, password minimal 8 karakter
        if (email == null || email.length() < 5) {
            throw new IllegalArgumentException("Email harus minimal 5 karakter");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password harus minimal 8 karakter");
        }
        
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        // Validasi OCL: Email minimal 5 karakter
        if (email == null || email.length() < 5) {
            throw new IllegalArgumentException("Email harus minimal 5 karakter");
        }
        this.email = email; 
    }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public boolean isLoggedIn() { return isLoggedIn; }
    
    // Method: login() dengan validasi OCL
    public boolean login(String inputPassword) {
        // Pre-condition: email dan password tidak kosong
        if (this.email == null || this.email.isEmpty() || inputPassword == null || inputPassword.isEmpty()) {
            throw new IllegalArgumentException("Email dan password tidak boleh kosong");
        }
        
        boolean success = this.password.equals(inputPassword);
        if (success) {
            this.isLoggedIn = true;
            // Post-condition: self.isLoggedIn = true
            if (!this.isLoggedIn) {
                throw new IllegalStateException("Post-condition login gagal");
            }
        }
        return success;
    }
    
    // Method: logout() dengan validasi OCL
    public void logout() {
        // Pre-condition: user harus sudah login
        if (!this.isLoggedIn) {
            throw new IllegalStateException("User harus login sebelum logout");
        }
        
        this.isLoggedIn = false;
        // Post-condition: self.isLoggedIn = false
        if (this.isLoggedIn) {
            throw new IllegalStateException("Post-condition logout gagal");
        }
    }
    
    // Method: ubahPassword(newPassword) dengan validasi OCL
    public void ubahPassword(String newPassword) {
        // Pre-condition: password baru minimal 8 karakter
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password baru harus minimal 8 karakter");
        }
        
        String passwordSebelum = this.password; // @pre value
        this.password = newPassword;
        
        // Post-condition: self.password = newPassword
        if (!this.password.equals(newPassword)) {
            this.password = passwordSebelum; // Rollback
            throw new IllegalStateException("Post-condition ubahPassword gagal");
        }
    }
    
    public boolean hasAccess(String feature) {
        switch (role) {
            case "ADMIN":
                return true;
            case "MANAGER":
                return !feature.equals("KELOLA_PENGGUNA");
            case "KASIR":
                return feature.equals("INPUT_TRANSAKSI") || 
                    feature.equals("LIHAT_LAPORAN");
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("ID: %s | Nama: %s | Email: %s | Role: %s | Status: %s", 
                id, nama, email, role, isLoggedIn ? "Logged In" : "Logged Out");
    }
}