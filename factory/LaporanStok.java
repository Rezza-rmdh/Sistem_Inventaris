package factory;

import entity.Barang;
import service.InventarisService;

import java.util.*;

public class LaporanStok implements ILaporan {
    private InventarisService inventarisService;
    private Map<String, Object> parameter;
    private List<Barang> dataStok;
    
    public LaporanStok(InventarisService inventarisService, Map<String, Object> parameter) {
        this.inventarisService = inventarisService;
        this.parameter = parameter;
        setupData();
    }
    
    private void setupData() {
        this.dataStok = inventarisService.getSemuaBarang();
    }
    
    @Override
    public String generate() {
        if (!validation()) {
            return "Data tidak valid untuk generate laporan stok";
        }
        
        Map<String, Integer> perubahanStok = hitungPerubahanStok();
        
        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("                     LAPORAN STOK BARANG                      \n");
        sb.append("===============================================================\n");
        sb.append(String.format("Total Barang: %d\n", dataStok.size()));
        sb.append(String.format("Total Nilai Stok: Rp %,.2f\n", hitungTotalNilaiStok()));
        sb.append("---------------------------------------------------------------\n");
        
        // Header tabel
        sb.append("+------+----------------------+--------------+------------+------------+----------+\n");
        sb.append("|  ID  |      Nama Barang     |   Kategori   | Harga Beli | Harga Jual |   Stok   |\n");
        sb.append("+------+----------------------+--------------+------------+------------+----------+\n");
        
        // Data barang
        for (Barang barang : dataStok) {
            String status = barang.getStok() == 0 ? "HABIS" : 
                           barang.getStok() < 10 ? "RENDAH" : "NORMAL";
            
            sb.append(String.format("| %-4s | %-20s | %-12s | %10.2f | %10.2f | %-8s |\n",
                barang.getId(),
                truncateString(barang.getNama(), 20),
                truncateString(barang.getKategori(), 12),
                barang.getHargaBeli(),
                barang.getHargaJual(),
                barang.getStok() + " (" + status + ")"));
        }
        
        sb.append("+------+----------------------+--------------+------------+------------+----------+\n");
        
        return sb.toString();
    }
    
    private String truncateString(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalBarang", dataStok.size());
        data.put("totalNilaiStok", hitungTotalNilaiStok());
        data.put("barang", dataStok);
        data.put("perubahanStok", hitungPerubahanStok());
        return data;
    }
    
    @Override
    public boolean validation() {
        return dataStok != null && !dataStok.isEmpty();
    }
    
    private Map<String, Integer> hitungPerubahanStok() {
        // Implementasi tracking perubahan stok
        Map<String, Integer> perubahan = new HashMap<>();
        for (Barang barang : dataStok) {
            perubahan.put(barang.getId(), barang.getStok());
        }
        return perubahan;
    }
    
    private double hitungTotalNilaiStok() {
        return dataStok.stream()
            .mapToDouble(b -> b.getHargaBeli() * b.getStok())
            .sum();
    }
}