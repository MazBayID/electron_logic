# Electron Logic ⚡

**Simulator elektronika Android untuk merakit rangkaian dan memahami cara kerjanya.**

Electron Logic sedang dikembangkan. Saat ini repository berisi prototipe web interaktif dan aplikasi Android sederhana yang membukanya melalui WebView. Ini belum merupakan simulator rangkaian umum.

## Coba prototipe

1. Unduh repository, atau buka [`index.html`](./index.html).
2. Jalankan file tersebut di browser modern.
3. Coba demo DC + resistor + LED, RC, dan NE555 konseptual. Ubah parameter dan amati hasil yang ditampilkan.

Tidak perlu memasang package manager atau server untuk mencoba prototipe lokal.

## Unduh APK Android

1. Buka [Build Android debug APK di GitHub Actions](https://github.com/MazBayID/electron_logic/actions/workflows/android-debug.yml).
2. Pilih run terbaru yang berstatus **Success**.
3. Di bagian **Artifacts**, unduh **electron-logic-debug-apk**.
4. Ekstrak ZIP dan instal `app-debug.apk` di perangkat Android. Jika diminta, izinkan instalasi dari sumber yang digunakan.

APK ini adalah **build debug untuk pengujian**, bukan rilis produksi. ID paketnya `com.mazbayid.electronlogic.debug`. Artifact hanya tersedia jika workflow selesai dengan sukses.

## Status saat ini

- Prototipe web: demo rangkaian, kontrol parameter, grafik, dan visualisasi gangguan/fault.
- Android: pembungkus WebView untuk menjalankan prototipe secara offline.
- Belum tersedia: editor rangkaian bebas, solver rangkaian umum, dan model fisika lengkap untuk semua komponen.

**Catatan:** efek noise pada demo adalah ilustrasi deterministik, bukan simulasi thermal noise atau shot noise yang tervalidasi. Gangguan (noise) dan hubung singkat (short circuit) merupakan hal berbeda.

## Arah pengembangan

1. Editor untuk menempatkan komponen dan menghubungkannya dengan kabel.
2. Solver DC dan transient dengan hasil yang dapat diuji.
3. Pengukuran tegangan/arus, grafik, dan visualisasi yang mengikuti hasil solver.
4. Model komponen yang lebih lengkap, termasuk transistor dan IC.

Baca [catatan model fisika](./docs/physics-model.md) untuk asumsi dan batasan yang lebih rinci. Laporan bug, saran, dan hasil pengujian di perangkat Android sangat diterima.
