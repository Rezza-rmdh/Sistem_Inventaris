package strategy;

import java.util.Date;
import java.util.UUID;

public class TransaksiContext {
    private String id;
    private Date tanggal;
    private TransaksiStrategy strategy;
    private double total;
    
    public TransaksiContext(TransaksiStrategy strategy) {
        // Generate ID transaksi unik menggunakan UUID
        this.id = "T" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
        this.tanggal = new Date();
        this.strategy = strategy;
        this.total = 0;
    }
    
    public void setStrategy(TransaksiStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void prosesTransaksi() {
        if (strategy != null) {
            strategy.prosesTransaksi();
        }
    }
    
    // Method: hitungTotal() dengan validasi OCL
    public double hitungTotal() {
        if (strategy != null) {
            double calculatedTotal = strategy.hitungTotal();
            this.total = calculatedTotal;
            
            // Post-condition: total harus sesuai dengan perhitungan strategy
            if (this.total != calculatedTotal) {
                throw new IllegalStateException("Post-condition hitungTotal gagal");
            }
        }
        return this.total;
    }
    
    public boolean validasiTransaksi() {
        return strategy != null ? strategy.validasiTransaksi() : false;
    }
    
    // Getter
    public String getId() { return id; }
    public Date getTanggal() { return tanggal; }
    public TransaksiStrategy getStrategy() { return strategy; }
    public String getJenisTransaksi() { 
        return strategy != null ? strategy.getJenisTransaksi() : "UNKNOWN"; 
    }
    public double getTotal() { return total; }
}