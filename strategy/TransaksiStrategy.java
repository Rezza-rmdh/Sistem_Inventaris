package strategy;

import entity.Barang;

public interface TransaksiStrategy {
    void prosesTransaksi();
    boolean validasiTransaksi();
    double hitungTotal();
    String getJenisTransaksi();
    Barang getBarang();
    int getJumlah();
}