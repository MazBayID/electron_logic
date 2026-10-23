# Electron Logic ⚡

**Simulator elektronika Android yang sedang dibangun dengan Kotlin native dan Jetpack Compose.**

Proyek sedang beralih dari prototipe HTML/WebView ke antarmuka Android native. Build Kotlin awal menyediakan dashboard lab, kontrol parameter demo, visual rangkaian, dan navigasi dasar. Ini belum menjadi solver rangkaian umum.

## Unduh APK pengujian

1. Buka [Android Kotlin CI di GitHub Actions](https://github.com/MazBayID/electron_logic/actions/workflows/android-debug.yml).
2. Pilih run terbaru berstatus **Success**.
3. Di bagian **Artifacts**, unduh **electron-logic-debug-apk**.
4. Ekstrak ZIP dan instal `app-debug.apk`.

APK ini merupakan debug build untuk pengujian, bukan rilis produksi. ID paket debug: `com.mazbayid.electronlogic.debug`. Artifact tersedia selama 14 hari setelah build.

## Status proyek

- **Sudah ada:** antarmuka native Kotlin/Compose, area aman Android, dashboard lab, kontrol demo tegangan/resistansi, visualisasi konseptual, serta status demo noise/fault.
- **Belum ada:** editor rangkaian bebas, koneksi pin/kabel, penyimpanan proyek, solver DC/transient yang umum, dan model fisika tervalidasi untuk komponen aktif.
- **Catatan model:** nilai yang tampil masih memakai pendekatan demonstrasi sederhana. Visualisasi partikel bukan pelacakan elektron individual; noise demo bukan simulasi thermal/shot noise fisik.

## Rencana berikutnya

1. Pisahkan model rangkaian dan UI agar dapat diuji.
2. Bangun editor komponen, pin, kabel, simpul, dan properti.
3. Tambahkan solver DC yang diuji dengan rangkaian referensi.
4. Kembangkan solver transient, alat ukur, grafik, dan model komponen bertahap.

File `index.html` lama dipertahankan hanya sebagai referensi prototipe web; APK Android tidak lagi memuatnya. Laporan bug dan hasil pengujian di perangkat Android sangat membantu.
