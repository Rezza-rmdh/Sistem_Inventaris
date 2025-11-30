package factory;

import strategy.TransaksiContext;
import service.InventarisService;

import java.util.*;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

public class LaporanKeuangan implements ILaporan {
    private InventarisService inventarisService;
    private Map<String, Object> parameter;
    private List<TransaksiContext> dataTransaksi;
    private Date periodeAwal;
    private Date periodeAkhir;
    private boolean isKeuanganGenerated = false;
    private boolean isStokGenerated = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public LaporanKeuangan(InventarisService inventarisService, Map<String, Object> parameter) {
        this.inventarisService = inventarisService;
        this.parameter = parameter;
        
        // Validasi OCL: Periode awal harus lebih awal atau sama dengan periode akhir
        Date awal = (Date) parameter.getOrDefault("periodeAwal", new Date(0));
        Date akhir = (Date) parameter.getOrDefault("periodeAkhir", new Date());
        
        if (awal.after(akhir)) {
            throw new IllegalArgumentException("Periode awal harus lebih awal atau sama dengan periode akhir");
        }
        
        this.periodeAwal = awal;
        this.periodeAkhir = akhir;
        
        setupData();
    }
    
    private void setupData() {
        this.dataTransaksi = inventarisService.getSemuaTransaksi();
        
        // Filter by periode
        dataTransaksi = dataTransaksi.stream()
            .filter(t -> !t.getTanggal().before(periodeAwal) && !t.getTanggal().after(periodeAkhir))
            .collect(Collectors.toList());
    }
    
    @Override
    public String generate() {
        return generateKeuangan();
    }
    
    // Method: generateKeuangan() dengan validasi OCL
    public String generateKeuangan() {
        // Pre-condition: periodeAwal <= periodeAkhir
        if (periodeAwal.after(periodeAkhir)) {
            throw new IllegalArgumentException("Periode awal harus lebih awal atau sama dengan periode akhir");
        }
        
        if (!validation()) {
            return "Data tidak valid untuk generate laporan keuangan";
        }
        
        double totalPemasukan = hitungTotalPemasukan();
        double totalPengeluaran = hitungTotalPengeluaran();
        double labaBersih = totalPemasukan - totalPengeluaran;
        
        StringBuilder sb = new StringBuilder();
        sb.append("================================================================================\n");
        sb.append("                            LAPORAN KEUANGAN                                   \n");
        sb.append("================================================================================\n");
        sb.append(String.format("Periode: %s sampai %s\n", dateFormat.format(periodeAwal), dateFormat.format(periodeAkhir)));
        sb.append(String.format("Total Transaksi: %d\n", dataTransaksi.size()));
        sb.append(String.format("Total Pemasukan: Rp %,.2f\n", totalPemasukan));
        sb.append(String.format("Total Pengeluaran: Rp %,.2f\n", totalPengeluaran));
        sb.append(String.format("Laba/Rugi: Rp %,.2f\n", labaBersih));
        sb.append("--------------------------------------------------------------------------------\n");
        
        // Header tabel transaksi
        sb.append("+------------+----------------+--------------+\n");
        sb.append("|   Tanggal  | Jenis Transaksi|    Total     |\n");
        sb.append("+------------+----------------+--------------+\n");
        
        // Data transaksi
        for (TransaksiContext transaksi : dataTransaksi) {
            sb.append(String.format("| %-10s | %-14s | %12.2f |\n",
                dateFormat.format(transaksi.getTanggal()),
                transaksi.getJenisTransaksi(),
                transaksi.hitungTotal()));
        }
        
        sb.append("+------------+----------------+--------------+\n");
        
        this.isKeuanganGenerated = true;
        // Post-condition: self.isKeuanganGenerated = true
        if (!this.isKeuanganGenerated) {
            throw new IllegalStateException("Post-condition generateKeuangan gagal");
        }
        
        return sb.toString();
    }
    
    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("periodeAwal", periodeAwal);
        data.put("periodeAkhir", periodeAkhir);
        data.put("totalTransaksi", dataTransaksi.size());
        data.put("totalPemasukan", hitungTotalPemasukan());
        data.put("totalPengeluaran", hitungTotalPengeluaran());
        data.put("labaBersih", hitungTotalPemasukan() - hitungTotalPengeluaran());
        data.put("transaksi", dataTransaksi);
        data.put("isKeuanganGenerated", isKeuanganGenerated);
        data.put("isStokGenerated", isStokGenerated);
        return data;
    }
    
    @Override
    public boolean validation() {
        // Validasi OCL: Periode awal harus lebih awal atau sama dengan periode akhir
        if (periodeAwal.after(periodeAkhir)) {
            return false;
        }
        return dataTransaksi != null && !dataTransaksi.isEmpty();
    }
    
    private double hitungTotalPemasukan() {
        return dataTransaksi.stream()
            .filter(t -> t.getJenisTransaksi().equals("PENJUALAN"))
            .mapToDouble(TransaksiContext::hitungTotal)
            .sum();
    }
    
    private double hitungTotalPengeluaran() {
        return dataTransaksi.stream()
            .filter(t -> t.getJenisTransaksi().equals("PEMBELIAN"))
            .mapToDouble(TransaksiContext::hitungTotal)
            .sum();
    }
}