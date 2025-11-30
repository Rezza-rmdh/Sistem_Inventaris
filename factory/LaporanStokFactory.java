package factory;

import service.InventarisService;
import java.util.Map;

public class LaporanStokFactory implements LaporanFactory {
    private InventarisService inventarisService;
    
    public LaporanStokFactory(InventarisService inventarisService) {
        this.inventarisService = inventarisService;
    }
    
    @Override
    public ILaporan createLaporan(String jenis, Map<String, Object> parameter) {
        if ("STOK".equalsIgnoreCase(jenis)) {
            return new LaporanStok(inventarisService, parameter);
        }
        throw new IllegalArgumentException("Jenis laporan tidak didukung: " + jenis);
    }
}