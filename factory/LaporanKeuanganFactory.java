package factory;

import service.InventarisService;
import java.util.Map;

public class LaporanKeuanganFactory implements LaporanFactory {
    private InventarisService inventarisService;
    
    public LaporanKeuanganFactory(InventarisService inventarisService) {
        this.inventarisService = inventarisService;
    }
    
    @Override
    public ILaporan createLaporan(String jenis, Map<String, Object> parameter) {
        if ("KEUANGAN".equalsIgnoreCase(jenis)) {
            return new LaporanKeuangan(inventarisService, parameter);
        }
        throw new IllegalArgumentException("Jenis laporan tidak didukung: " + jenis);
    }
}