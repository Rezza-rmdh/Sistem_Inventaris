package factory;

import java.util.Map;

public interface LaporanFactory {
    ILaporan createLaporan(String jenis, Map<String, Object> parameter);
}