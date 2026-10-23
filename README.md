# Electron Logic ⚡

**Laboratorium elektronika Android native (Kotlin + Jetpack Compose).**

Electron Logic membantu mempelajari respons rangkaian sederhana melalui parameter yang bisa diubah dan grafik tegangan terhadap waktu. Antarmuka native menyediakan tiga contoh model: LED dengan sumber DC, pengisian kapasitor RC, dan timer NE555 astable.

## Unduh APK

1. Buka [Android Kotlin CI di GitHub Actions](https://github.com/MazBayID/electron_logic/actions/workflows/android-debug.yml).
2. Buka run terbaru yang berstatus **Success**.
3. Di bagian **Artifacts**, unduh `electron-logic-debug-apk`.
4. Ekstrak ZIP dan instal `app-debug.apk`.

APK CI adalah build debug untuk pengujian, bukan rilis produksi. ID paket debug: `com.mazbayid.electronlogic.debug`. Artifact tersedia selama 14 hari.

## Fitur yang tersedia

- **Lab:** pilih contoh rangkaian, ubah tegangan sumber dan resistansi, aktifkan injeksi noise atau demo fault, jeda/jalankan animasi, dan reset parameter.
- **LED/DC:** pendekatan arus DC dengan asumsi tegangan maju LED tetap 2 V.
- **RC:** kurva pengisian eksponensial untuk kapasitor ideal 1000 µF dengan konstanta waktu R×C.
- **NE555 astable:** keluaran kotak konseptual dan estimasi frekuensi dengan RA=RB=R dan C=10 µF.
- **Oscilloscope:** grafik CH1 yang merekam riwayat tegangan output model saat simulasi berjalan.
- **Circuit editor:** tambah/hapus node dan resistor, atur nilai resistansi, pilih node A/B untuk setiap resistor, dan hitung tegangan node, arus cabang, serta daya dengan solver DC nodal.
- **Circuit view contoh:** simbol sumber DC, resistor, LED, kapasitor, dan IC NE555 dengan label.
- **Measurement:** ringkasan nilai tegangan, arus, waktu, status, dan estimasi frekuensi NE555.
- **Fault/noise indicators:** warna dipakai untuk membedakan demo fault dan sinyal gangguan yang sengaja diinjeksi.

## Batasan ilmiah yang penting

Versi ini adalah **alat edukasi berbasis model sederhana, bukan simulator SPICE dan bukan solver rangkaian umum**.

- Model LED menganggap tegangan maju 2 V; tidak memodelkan kurva datasheet, suhu, atau toleransi.
- Model RC menganggap kapasitor ideal dan kondisi awal sederhana; ESR, leakage, dan jaringan bebas belum dihitung.
- Model NE555 memakai estimasi astable. Ia belum memodelkan transistor internal, beban output, jitter, atau respons listrik pin 5.
- Noise adalah sinyal matematis yang sengaja ditambahkan ke output, bukan thermal noise atau shot noise fisik.
- Fault demo hanya menandai kondisi yang disisipkan; nilai arusnya bukan prediksi arus hubung-singkat nyata.
- Titik animasi adalah penanda skematis, bukan pelacakan elektron individual.
- Pin 5 NE555 adalah **CONTROL VOLTAGE** yang dapat mengubah ambang internal nominal sekitar 1/3 dan 2/3 VCC; pin tersebut bukan sumber noise.
- Editor grafis drag-and-drop, kabel yang digambar langsung dengan sentuhan, penyimpanan proyek, solver dioda nonlinier, dan solver transient umum belum tersedia. Solver DC saat ini mendukung jaringan resistor linear dengan sumber tegangan ideal.

## Pengembangan dan pemeriksaan

GitHub Actions menjalankan tes unit model LED/RC/NE555, rangkaian seri, serta solver nodal resistor lalu membangun APK debug dan mengunggah artefak. Tes emulator UI dihapus dari jalur wajib karena runner emulator tidak stabil dan tidak sebanding dengan nilai tambahnya untuk iterasi ini. Pengujian akhir interaksi dan tampilan akan dilakukan pada perangkat Android oleh tester.

Sumber prototipe HTML lama tetap ada sebagai referensi historis; APK native tidak memuat halaman HTML tersebut.
