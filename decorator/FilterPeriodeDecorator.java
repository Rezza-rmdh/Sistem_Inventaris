package decorator;

import factory.ILaporan;
import strategy.TransaksiContext;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        String originalReport = laporanDekorasi.generate();
        Map<String, Object> data = laporanDekorasi.getData();
        
        // Filter data transaksi by periode
        if (data.containsKey("transaksi")) {
            List<TransaksiContext> filteredTransaksi = filterByPeriode(
                (List<TransaksiContext>) data.get("transaksi")
            );
            data.put("transaksi", filteredTransaksi);
        }
        
        return "=== FILTER PERIODE: " + periodeAwal + " - " + periodeAkhir + " ===\n" + originalReport;
    }
    
    @Override
    public Map<String, Object> getData() {
        Map<String, Object> data = laporanDekorasi.getData();
        if (data.containsKey("transaksi")) {
            List<TransaksiContext> filteredTransaksi = filterByPeriode(
                (List<TransaksiContext>) data.get("transaksi")
            );
            data.put("transaksi", filteredTransaksi);
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