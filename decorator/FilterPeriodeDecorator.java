package decorator;

import factory.ILaporan;
import strategy.TransaksiContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class FilterPeriodeDecorator extends LaporanDecorator {
    private Date periodeAwal;
    private Date periodeAkhir;
    
    public FilterPeriodeDecorator(ILaporan laporan, Date periodeAwal, Date periodeAkhir) {
        super(laporan);
        this.periodeAwal = periodeAwal;
        this.periodeAkhir = periodeAkhir;
    }
    
    @Override
    public String generate() {
        Map<String, Object> originalData = laporanDekorasi.getData();
        Map<String, Object> filteredData = new HashMap<>(originalData);
        
        if (filteredData.containsKey("transaksi")) {
            Object transaksiObj = filteredData.get("transaksi");
            if (transaksiObj instanceof List<?>) {
                List<TransaksiContext> transaksiList = (List<TransaksiContext>) transaksiObj;
                List<TransaksiContext> filteredTransaksi = filterByPeriode(transaksiList);
                filteredData.put("transaksi", filteredTransaksi);
            }
        }
                StringBuilder sb = new StringBuilder();
        sb.append("=== FILTER PERIODE: ")
          .append(periodeAwal)
          .append(" - ")
          .append(periodeAkhir)
          .append(" ===\n");
        
        if (filteredData.containsKey("transaksi")) {
            List<TransaksiContext> filtered = (List<TransaksiContext>) filteredData.get("transaksi");
            sb.append("Total Transaksi dalam Periode: ").append(filtered.size()).append("\n");
            
            double totalPemasukan = filtered.stream()
                .filter(t -> "PENJUALAN".equals(t.getJenisTransaksi()))
                .mapToDouble(TransaksiContext::hitungTotal)
                .sum();
                
            double totalPengeluaran = filtered.stream()
                .filter(t -> "PEMBELIAN".equals(t.getJenisTransaksi()))
                .mapToDouble(TransaksiContext::hitungTotal)
                .sum();
                
            sb.append("Total Pemasukan: Rp ").append(String.format("%,.2f", totalPemasukan)).append("\n");
            sb.append("Total Pengeluaran: Rp ").append(String.format("%,.2f", totalPengeluaran)).append("\n");
            sb.append("Laba/Rugi: Rp ").append(String.format("%,.2f", totalPemasukan - totalPengeluaran)).append("\n\n");
        }
        
        sb.append(laporanDekorasi.generate());
        return sb.toString();
    }
    
    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = laporanDekorasi.getData();
        if (data.containsKey("transaksi")) {
            Object transaksiObj = data.get("transaksi");
            if (transaksiObj instanceof List<?>) {
                List<TransaksiContext> transaksiList = (List<TransaksiContext>) transaksiObj;
                List<TransaksiContext> filteredTransaksi = filterByPeriode(transaksiList);
                data.put("transaksi", filteredTransaksi);
            }
        }
        data.put("filterPeriodeAwal", periodeAwal);
        data.put("filterPeriodeAkhir", periodeAkhir);
        return data;
    }
    
    private List<TransaksiContext> filterByPeriode(List<TransaksiContext> transaksiList) {
        return transaksiList.stream()
            .filter(t -> !t.getTanggal().before(periodeAwal) && !t.getTanggal().after(periodeAkhir))
            .collect(Collectors.toList());
    }
}