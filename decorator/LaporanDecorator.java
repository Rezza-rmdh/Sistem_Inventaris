package decorator;

import factory.ILaporan;

import java.util.Map;

public abstract class LaporanDecorator implements ILaporan {
    protected ILaporan laporanDekorasi;
    
    public LaporanDecorator(ILaporan laporan) {
        this.laporanDekorasi = laporan;
    }
    
    @Override
    public String generate() {
        return laporanDekorasi.generate();
    }
    
    @Override
    public Map<String, Object> getData() {
        return laporanDekorasi.getData();
    }
    
    @Override
    public boolean validation() {
        return laporanDekorasi.validation();
    }
}