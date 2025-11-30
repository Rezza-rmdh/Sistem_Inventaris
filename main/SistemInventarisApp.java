package main;

import entity.Barang;
import entity.Pengguna;
import factory.*;
import decorator.*;
import strategy.*;
import service.InventarisService;

import java.text.SimpleDateFormat;
import java.util.*;

public class SistemInventarisApp {
    private InventarisService inventarisService;
    private Scanner scanner;
    private boolean isRunning;

    public SistemInventarisApp() {
        this.inventarisService = new InventarisService();
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    public void run() {
        System.out.println("=== SISTEM INVENTARIS KEUANGAN & BARANG ===");

        while (isRunning) {
            if (inventarisService.getPenggunaAktif() == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }

        scanner.close();
        System.out.println("Terima kasih telah menggunakan sistem inventaris!");
    }

    private void showLoginMenu() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (inventarisService.login(email, password)) {
            Pengguna user = inventarisService.getPenggunaAktif();
            System.out.println("Login berhasil! Selamat datang, " + user.getNama());
        } else {
            System.out.println("Login gagal! Email atau password salah.");
        }
    }

    private void showMainMenu() {
        Pengguna user = inventarisService.getPenggunaAktif();
        System.out.println("\n=== MENU UTAMA ===");
        System.out.println("User: " + user.getNama() + " (" + user.getRole() + ")");
        System.out.println("1. Kelola Barang");
        System.out.println("2. Transaksi");
        System.out.println("3. Laporan");
        System.out.println("4. Manajemen Pengguna");
        System.out.println("5. Logout");
        System.out.println("0. Keluar");

        System.out.print("Pilih menu: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                showKelolaBarangMenu();
                break;
            case 2:
                showTransaksiMenu();
                break;
            case 3:
                showLaporanMenu();
                break;
            case 4:
                showManajemenPenggunaMenu();
                break;
            case 5:
                inventarisService.logout();
                System.out.println("Logout berhasil!");
                break;
            case 0:
                isRunning = false;
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private void showKelolaBarangMenu() {
        if (!inventarisService.getPenggunaAktif().hasAccess("MANAGE_BARANG")) {
            System.out.println("Anda tidak memiliki akses ke menu ini!");
            return;
        }

        System.out.println("\n=== KELOLA BARANG ===");
        System.out.println("1. Lihat Semua Barang");
        System.out.println("2. Tambah Barang");
        System.out.println("3. Edit Barang");
        System.out.println("4. Hapus Barang");
        System.out.println("0. Kembali");

        System.out.print("Pilih menu: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                lihatSemuaBarang();
                break;
            case 2:
                tambahBarang();
                break;
            case 3:
                editBarang();
                break;
            case 4:
                hapusBarang();
                break;
            case 0:
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private void lihatSemuaBarang() {
        List<Barang> barangList = inventarisService.getSemuaBarang();
        if (barangList.isEmpty()) {
            System.out.println("Tidak ada barang yang terdaftar.");
        } else {
            System.out.println("\n=== DAFTAR BARANG ===");

            // Header tabel
            System.out.println("+------+----------------------+--------------+------------+------------+-------+");
            System.out.println("|  ID  |      Nama Barang     |   Kategori   | Harga Beli | Harga Jual | Stok  |");
            System.out.println("+------+----------------------+--------------+------------+------------+-------+");

            // Data barang
            for (Barang barang : barangList) {
                System.out.println(String.format("| %-4s | %-20s | %-12s | %10.2f | %10.2f | %-5d |",
                        barang.getId(),
                        barang.getNama(),
                        barang.getKategori(),
                        barang.getHargaBeli(),
                        barang.getHargaJual(),
                        barang.getStok()));
            }

            System.out.println("+------+----------------------+--------------+------------+------------+-------+");
            System.out.println("Total Barang: " + barangList.size());
        }
    }

    private void tambahBarang() {
        try {
            System.out.println("\n=== TAMBAH BARANG ===");
            System.out.print("ID Barang: ");
            String id = scanner.nextLine();

            // Cek ID sudah ada
            if (inventarisService.findBarangById(id) != null) {
                System.out.println("Error: ID Barang sudah digunakan!");
                return;
            }

            System.out.print("Nama Barang: ");
            String nama = scanner.nextLine();
            System.out.print("Kategori: ");
            String kategori = scanner.nextLine();
            System.out.print("Harga Beli: ");
            double hargaBeli = scanner.nextDouble();
            System.out.print("Harga Jual: ");
            double hargaJual = scanner.nextDouble();
            System.out.print("Stok Awal: ");
            int stok = scanner.nextInt();
            scanner.nextLine();

            Barang barang = new Barang(id, nama, kategori, hargaBeli, hargaJual, stok);
            inventarisService.tambahBarang(barang);
            System.out.println("Barang berhasil ditambahkan!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void editBarang() {
        try {
            System.out.print("Masukkan ID Barang yang akan diedit: ");
            String id = scanner.nextLine();
            Barang barang = inventarisService.findBarangById(id);

            if (barang == null) {
                System.out.println("Barang tidak ditemukan!");
                return;
            }

            System.out.println("Data saat ini: " + barang);
            System.out.print("Nama Barang baru: ");
            String nama = scanner.nextLine();
            System.out.print("Kategori baru: ");
            String kategori = scanner.nextLine();
            System.out.print("Harga Beli baru: ");
            double hargaBeli = scanner.nextDouble();
            System.out.print("Harga Jual baru: ");
            double hargaJual = scanner.nextDouble();
            System.out.print("Stok baru: ");
            int stok = scanner.nextInt();
            scanner.nextLine();

            barang.setNama(nama);
            barang.setKategori(kategori);
            barang.setHargaBeli(hargaBeli);
            barang.setHargaJual(hargaJual);
            barang.setStok(stok);

            inventarisService.updateBarang(barang);
            System.out.println("Barang berhasil diupdate!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusBarang() {
        System.out.print("Masukkan ID Barang yang akan dihapus: ");
        String id = scanner.nextLine();
        inventarisService.hapusBarang(id);
        System.out.println("Barang berhasil dihapus!");
    }

    private void showTransaksiMenu() {
        if (!inventarisService.getPenggunaAktif().hasAccess("INPUT_TRANSAKSI")) {
            System.out.println("Anda tidak memiliki akses ke menu ini!");
            return;
        }

        System.out.println("\n=== TRANSAKSI ===");
        System.out.println("1. Transaksi Penjualan");
        System.out.println("2. Transaksi Pembelian");
        System.out.println("3. Lihat Riwayat Transaksi");
        System.out.println("0. Kembali");

        System.out.print("Pilih menu: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                prosesTransaksiPenjualan();
                break;
            case 2:
                prosesTransaksiPembelian();
                break;
            case 3:
                lihatRiwayatTransaksi();
                break;
            case 0:
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private void prosesTransaksiPenjualan() {
        try {
            System.out.println("\n=== TRANSAKSI PENJUALAN ===");
            System.out.print("Masukkan ID Barang: ");
            String idBarang = scanner.nextLine();
            Barang barang = inventarisService.findBarangById(idBarang);

            if (barang == null) {
                System.out.println("Barang tidak ditemukan!");
                return;
            }

            System.out.println("Barang: " + barang.getNama());
            System.out.println("Stok tersedia: " + barang.getStok());
            System.out.print("Jumlah: ");
            int jumlah = scanner.nextInt();
            scanner.nextLine();

            TransaksiPenjualanStrategy strategy = new TransaksiPenjualanStrategy(
                    barang, jumlah, inventarisService);
            TransaksiContext transaksi = new TransaksiContext(strategy);

            if (transaksi.validasiTransaksi()) {
                System.out.println("Total: Rp " + transaksi.hitungTotal());
                System.out.print("Konfirmasi transaksi? (y/n): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("y")) {
                    transaksi.prosesTransaksi();
                    inventarisService.tambahTransaksi(transaksi);
                    System.out.println("Transaksi penjualan berhasil!");
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error validasi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void prosesTransaksiPembelian() {
        try {
            System.out.println("\n=== TRANSAKSI PEMBELIAN ===");
            System.out.print("Masukkan ID Barang: ");
            String idBarang = scanner.nextLine();
            Barang barang = inventarisService.findBarangById(idBarang);

            if (barang == null) {
                System.out.println("Barang tidak ditemukan!");
                return;
            }

            System.out.println("Barang: " + barang.getNama());
            System.out.print("Jumlah: ");
            int jumlah = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Supplier: ");
            String supplier = scanner.nextLine();

            TransaksiPembelianStrategy strategy = new TransaksiPembelianStrategy(
                    barang, jumlah, supplier, inventarisService);
            TransaksiContext transaksi = new TransaksiContext(strategy);

            if (transaksi.validasiTransaksi()) {
                System.out.println("Total: Rp " + transaksi.hitungTotal());
                System.out.print("Konfirmasi transaksi? (y/n): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("y")) {
                    transaksi.prosesTransaksi();
                    inventarisService.tambahTransaksi(transaksi);
                    System.out.println("Transaksi pembelian berhasil!");
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error validasi: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void lihatRiwayatTransaksi() {
        List<TransaksiContext> transaksiList = inventarisService.getSemuaTransaksi();
        if (transaksiList.isEmpty()) {
            System.out.println("Belum ada transaksi.");
        } else {
            System.out.println("\n=== RIWAYAT TRANSAKSI ===");

            // Header tabel
            System.out.println("+----------+------------+----------------+--------------+");
            System.out.println("|    ID    |   Tanggal  | Jenis Transaksi|    Total     |");
            System.out.println("+----------+------------+----------------+--------------+");

            // Data transaksi
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (TransaksiContext transaksi : transaksiList) {
                System.out.println(String.format("| %-8s | %-10s | %-14s | %-10d | %12.2f |",
                        transaksi.getId(),
                        sdf.format(transaksi.getTanggal()),
                        transaksi.getJenisTransaksi(),
                        transaksi.hitungTotal()));
            }

            System.out.println("+----------+------------+----------------+------------+--------------+");
            System.out.println("Total Transaksi: " + transaksiList.size());
        }
    }

    private void showLaporanMenu() {
        if (!inventarisService.getPenggunaAktif().hasAccess("LIHAT_LAPORAN")) {
            System.out.println("Anda tidak memiliki akses ke menu ini!");
            return;
        }

        System.out.println("\n=== LAPORAN ===");
        System.out.println("1. Laporan Keuangan");
        System.out.println("2. Laporan Stok");
        System.out.println("3. Laporan dengan Filter Periode");
        System.out.println("0. Kembali");

        System.out.print("Pilih menu: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        LaporanFactory factory = new LaporanKeuanganFactory(inventarisService);

        switch (choice) {
            case 1:
                generateLaporanKeuangan(factory);
                break;
            case 2:
                generateLaporanStok(factory);
                break;
            case 3:
                generateLaporanDenganFilter(factory);
                break;
            case 0:
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private void generateLaporanKeuangan(LaporanFactory factory) {
        try {
            Map<String, Object> parameter = new HashMap<>();
            ILaporan laporan = factory.createLaporan("KEUANGAN", parameter);
            System.out.println(laporan.generate());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void generateLaporanStok(LaporanFactory factory) {
        try {
            Map<String, Object> parameter = new HashMap<>();
            ILaporan laporan = factory.createLaporan("STOK", parameter);
            System.out.println(laporan.generate());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void generateLaporanDenganFilter(LaporanFactory factory) {
        try {
            System.out.print("Tanggal awal (dd/MM/yyyy): ");
            String tglAwalStr = scanner.nextLine();
            System.out.print("Tanggal akhir (dd/MM/yyyy): ");
            String tglAkhirStr = scanner.nextLine();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date tglAwal = sdf.parse(tglAwalStr);
            Date tglAkhir = sdf.parse(tglAkhirStr);

            Map<String, Object> parameter = new HashMap<>();
            ILaporan baseLaporan = factory.createLaporan("KEUANGAN", parameter);
            ILaporan filteredLaporan = new FilterPeriodeDecorator(baseLaporan, tglAwal, tglAkhir);

            System.out.println(filteredLaporan.generate());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showManajemenPenggunaMenu() {
        if (!inventarisService.getPenggunaAktif().hasAccess("MANAGE_USERS")) {
            System.out.println("Anda tidak memiliki akses ke menu ini!");
            return;
        }

        System.out.println("\n=== MANAJEMEN PENGGUNA ===");
        System.out.println("1. Lihat Semua Pengguna");
        System.out.println("2. Tambah Pengguna");
        System.out.println("3. Edit Pengguna");
        System.out.println("4. Hapus Pengguna");
        System.out.println("0. Kembali");

        System.out.print("Pilih menu: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                lihatSemuaPengguna();
                break;
            case 2:
                tambahPengguna();
                break;
            case 3:
                editPengguna();
                break;
            case 4:
                hapusPengguna();
                break;
            case 0:
                break;
            default:
                System.out.println("Pilihan tidak valid!");
        }
    }

    private void lihatSemuaPengguna() {
        List<Pengguna> userList = inventarisService.getSemuaPengguna();
        System.out.println("\n=== DAFTAR PENGGUNA ===");
        
        if (userList.isEmpty()) {
            System.out.println("Tidak ada pengguna yang terdaftar.");
        } else {
            // Header tabel
            System.out.println("+------+----------------------+----------------------+----------+---------------+");
            System.out.println("|  ID  |         Nama         |        Email         |   Role   |    Status     |");
            System.out.println("+------+----------------------+----------------------+----------+---------------+");
            
            // Data pengguna
            for (Pengguna user : userList) {
                System.out.println(String.format("| %-4s | %-20s | %-20s | %-8s | %-13s |",
                    user.getId(),
                    user.getNama(),
                    user.getEmail(),
                    user.getRole(),
                    user.isLoggedIn() ? "Logged In" : "Logged Out"));
            }
            
            System.out.println("+------+----------------------+----------------------+----------+---------------+");
            System.out.println("Total Pengguna: " + userList.size());
        }
    }

    private void tambahPengguna() {
        try {
            System.out.println("\n=== TAMBAH PENGGUNA ===");
            System.out.print("ID Pengguna: ");
            String id = scanner.nextLine();

            // Cek ID sudah ada
            if (inventarisService.findPenggunaById(id) != null) {
                System.out.println("Error: ID Pengguna sudah digunakan!");
                return;
            }

            System.out.print("Nama: ");
            String nama = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();

            // Cek Email sudah ada
            if (inventarisService.findPenggunaByEmail(email) != null) {
                System.out.println("Error: Email sudah digunakan!");
                return;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Role (ADMIN/MANAGER/KASIR): ");
            String role = scanner.nextLine().toUpperCase();

            // Validasi role
            if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("KASIR")) {
                System.out.println("Error: Role harus ADMIN, MANAGER, atau KASIR!");
                return;
            }

            Pengguna pengguna = new Pengguna(id, nama, email, password, role);
            inventarisService.tambahPengguna(pengguna);
            System.out.println("Pengguna berhasil ditambahkan!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void editPengguna() {
        try {
            System.out.print("Masukkan ID Pengguna yang akan diedit: ");
            String id = scanner.nextLine();
            Pengguna pengguna = inventarisService.findPenggunaById(id);

            if (pengguna == null) {
                System.out.println("Pengguna tidak ditemukan!");
                return;
            }

            System.out.println("Data saat ini: " + pengguna);
            System.out.print("Nama baru: ");
            String nama = scanner.nextLine();
            System.out.print("Email baru: ");
            String email = scanner.nextLine();

            // Cek email unik (kecuali untuk user yang sama)
            Pengguna existingUserWithEmail = inventarisService.findPenggunaByEmail(email);
            if (existingUserWithEmail != null && !existingUserWithEmail.getId().equals(pengguna.getId())) {
                System.out.println("Error: Email sudah digunakan oleh pengguna lain!");
                return;
            }

            System.out.print("Password baru (kosongkan jika tidak ingin mengubah): ");
            String password = scanner.nextLine();
            System.out.print("Role baru (ADMIN/MANAGER/KASIR): ");
            String role = scanner.nextLine().toUpperCase();

            // Validasi role
            if (!role.equals("ADMIN") && !role.equals("MANAGER") && !role.equals("KASIR")) {
                System.out.println("Error: Role harus ADMIN, MANAGER, atau KASIR!");
                return;
            }

            pengguna.setNama(nama);
            pengguna.setEmail(email);
            if (!password.isEmpty()) {
                pengguna.ubahPassword(password);
            }
            // Note: Role biasanya tidak diubah melalui edit biasa, butuh validasi tambahan

            inventarisService.updatePengguna(pengguna);
            System.out.println("Pengguna berhasil diupdate!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void hapusPengguna() {
        try {
            System.out.print("Masukkan ID Pengguna yang akan dihapus: ");
            String id = scanner.nextLine();

            Pengguna pengguna = inventarisService.findPenggunaById(id);
            if (pengguna == null) {
                System.out.println("Pengguna tidak ditemukan!");
                return;
            }

            // Konfirmasi penghapusan
            System.out.println("Data pengguna yang akan dihapus: " + pengguna);
            System.out.print("Yakin ingin menghapus? (y/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                inventarisService.hapusPengguna(id);
                System.out.println("Pengguna berhasil dihapus!");
            } else {
                System.out.println("Penghapusan dibatalkan.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SistemInventarisApp app = new SistemInventarisApp();
        app.run();
    }
}