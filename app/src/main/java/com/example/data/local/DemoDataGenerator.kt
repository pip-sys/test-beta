package com.example.data.local

import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType

object DemoDataGenerator {
    fun buildSixMonthDemoTransactions(): List<FinancialTransaction> {
        val baseTime = 1_760_000_000_000L
        var offset = 0L
        fun tx(
            title: String,
            amount: Long,
            type: TransactionType,
            category: String,
            dateIso: String
        ): FinancialTransaction {
            offset += 1000L
            return FinancialTransaction(
                title = title,
                amount = amount,
                type = type,
                category = category,
                dateIso = dateIso,
                yearMonth = dateIso.substring(0, 7),
                createdAt = baseTime + offset
            )
        }

        return listOf(
            // Juni 2026
            tx("Uang Saku Bulanan Juni", 1_500_000, TransactionType.INCOME, "Uang Saku", "2026-06-01"),
            tx("Jual Desain Poster OSIS", 250_000, TransactionType.INCOME, "Freelance/Part-time", "2026-06-14"),
            tx("Kantin & Bekal Sekolah", 380_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-06-05"),
            tx("Nonton & Kopi Bareng Teman", 285_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-06-12"),
            tx("Bensin Motor & Parkir", 145_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-06-08"),
            tx("Paket Data Bulanan 35GB", 85_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-06-02"),
            tx("Fotokopi Modul Ujian", 110_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-06-10"),
            tx("Case HP & Aksesoris Meja", 160_000, TransactionType.EXPENSE, "Belanja Online", "2026-06-20"),

            // Juli 2026
            tx("Uang Saku Bulanan Juli", 1_550_000, TransactionType.INCOME, "Uang Saku", "2026-07-01"),
            tx("Bonus Lomba Karya Tulis", 400_000, TransactionType.INCOME, "Beasiswa/Prestasi", "2026-07-18"),
            tx("Jajan Harian & Minuman Boba", 410_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-07-06"),
            tx("Main Futsal & Kafe Weekend", 340_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-07-11"),
            tx("Isi Pertamax & Ojek Online", 170_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-07-09"),
            tx("Kuota Internet & Spotify", 115_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-07-03"),
            tx("Buku Referensi & Alat Tulis", 190_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-07-15"),
            tx("Checkout Flash Sale Sepatu", 320_000, TransactionType.EXPENSE, "Belanja Online", "2026-07-22"),

            // Agustus 2026
            tx("Uang Saku Bulanan Agustus", 1_600_000, TransactionType.INCOME, "Uang Saku", "2026-08-01"),
            tx("Editing Video Acara Sekolah", 350_000, TransactionType.INCOME, "Freelance/Part-time", "2026-08-19"),
            tx("Makan Siang & Camilan Sore", 435_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-08-07"),
            tx("Nongkrong Malam Minggu & Bioskop", 395_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-08-15"),
            tx("Bensin & Servis Ringan Motor", 210_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-08-10"),
            tx("Paket Data & Canva Pro Pelajar", 125_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-08-02"),
            tx("Print Proposal Tugas Akhir", 145_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-08-21"),
            tx("Jaket Hoodie Marketplace", 265_000, TransactionType.EXPENSE, "Belanja Online", "2026-08-25"),

            // September 2026
            tx("Uang Saku Bulanan September", 1_600_000, TransactionType.INCOME, "Uang Saku", "2026-09-01"),
            tx("Beasiswa Prestasi Semester", 650_000, TransactionType.INCOME, "Beasiswa/Prestasi", "2026-09-10"),
            tx("Warteg, Mie Ayam & Es Kopi", 395_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-09-04"),
            tx("Hangout Kafe & Game Top-Up", 310_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-09-13"),
            tx("Bensin Mingguan", 150_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-09-08"),
            tx("Paket Internet Unlimited", 105_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-09-02"),
            tx("Jilid Laporan & Kertas A4", 165_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-09-17"),
            tx("Mouse Wireless Tugas Akhir", 185_000, TransactionType.EXPENSE, "Belanja Online", "2026-09-23"),
            tx("Iuran Kas Kelas & Lainnya", 60_000, TransactionType.EXPENSE, "Lainnya", "2026-09-27"),

            // Oktober 2026
            tx("Uang Saku Bulanan Oktober", 1_650_000, TransactionType.INCOME, "Uang Saku", "2026-10-01"),
            tx("Les Privat Adik Kelas", 450_000, TransactionType.INCOME, "Freelance/Part-time", "2026-10-16"),
            tx("Kantin Sekolah & GoFood Malam", 460_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-10-06"),
            tx("Nongkrong Coffee Shop & Karaoke", 480_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-10-18"),
            tx("Transportasi Sekolah & Bensin", 165_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-10-09"),
            tx("Paket Kuota & Cloud Storage", 120_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-10-03"),
            tx("Cetak Revisi Bab 1-3 & Map", 130_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-10-20"),
            tx("Belanja Tas & Organizer", 290_000, TransactionType.EXPENSE, "Belanja Online", "2026-10-24"),
            tx("Donasi Bakti Sosial", 50_000, TransactionType.EXPENSE, "Lainnya", "2026-10-28"),

            // November 2026 (Bulan Berjalan)
            tx("Uang Saku Bulanan November", 1_750_000, TransactionType.INCOME, "Uang Saku", "2026-11-01"),
            tx("Jasa Buat Slide Presentasi", 350_000, TransactionType.INCOME, "Freelance/Part-time", "2026-11-08"),
            tx("Hadiah Ulang Tahun dari Kakak", 250_000, TransactionType.INCOME, "Hadiah/Lainnya", "2026-11-11"),
            tx("Kopi Susu & Nongkrong Bareng Tim", 420_000, TransactionType.EXPENSE, "Nongkrong & Hiburan", "2026-11-09"),
            tx("Makan Siang Kantin & Jajan Sore", 365_000, TransactionType.EXPENSE, "Makan & Jajan", "2026-11-05"),
            tx("Checkout Promo 11.11 Marketplace", 285_000, TransactionType.EXPENSE, "Belanja Online", "2026-11-11"),
            tx("Print & Jilid Hardcover Tugas Akhir", 175_000, TransactionType.EXPENSE, "Kebutuhan Sekolah/Tugas", "2026-11-12"),
            tx("Isi Bensin Pertalite & Parkir", 140_000, TransactionType.EXPENSE, "Transportasi & Bensin", "2026-11-04"),
            tx("Paket Kuota Belajar + Musik", 115_000, TransactionType.EXPENSE, "Kuota & Langganan", "2026-11-02"),
            tx("Potong Rambut & Keperluan Harian", 65_000, TransactionType.EXPENSE, "Lainnya", "2026-11-13")
        )
    }
}
