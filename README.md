# Electron Logic ⚡

> **Build the circuit. See what happens. Understand why.**  
> Simulator elektronika yang berfokus pada perakitan rangkaian, visualisasi perilaku listrik, dan penjelasan fisika yang transparan.

Electron Logic ditujukan sebagai aplikasi Android untuk membangun rangkaian secara bebas, mengubah nilai komponen, menjalankan simulasi, serta memahami arus, tegangan, penyimpanan muatan, gangguan, dan fault. Animasi harus mengikuti hasil model—bukan sekadar terlihat meyakinkan.

## 🚀 Coba prototipe sekarang

Repository ini sekarang menyertakan prototipe web interaktif:

- Buka **[Circuit Lab — index.html](./index.html)**.
- Pilih demo **DC + resistor + LED**, **RC**, atau **NE555 astable konseptual**.
- Ubah tegangan dan resistansi; aktifkan sumber noise demonstrasi atau short circuit untuk melihat perbedaan tampilan normal, gangguan, dan fault.
- Amati visualisasi rangkaian, nilai model, status, dan grafik sinyal.

Untuk menjalankannya, unduh repository lalu buka `index.html` di browser modern. File ini tidak membutuhkan package manager atau server. Agar dapat dibuka sebagai situs, GitHub Pages dapat diaktifkan melalui **Settings → Pages** pada repository.

**Penting:** ini prototipe antarmuka dan model edukasi sederhana, bukan aplikasi Android/APK dan bukan solver rangkaian umum. Noise yang diaktifkan saat ini adalah sinyal demonstrasi deterministik, bukan simulasi thermal noise atau shot noise fisik. Model dan batasannya dijelaskan di **[docs/physics-model.md](./docs/physics-model.md)**.

## 📱 APK Android debug

Workflow Android sudah disiapkan untuk membangun APK debug dari host Android WebView yang memuat prototipe secara offline.

1. Buka tab **[Actions → Build Android debug APK](https://github.com/MazBayID/electron_logic/actions/workflows/android-debug.yml)**.
2. Pilih workflow run terbaru dengan status **Success**.
3. Pada bagian **Artifacts**, unduh `electron-logic-debug-apk`.
4. Ekstrak ZIP artifact, lalu instal `app-debug.apk` di perangkat Android. Jika diminta, izinkan instalasi dari sumber tersebut.

APK ini adalah **debug build** dengan application ID `com.mazbayid.electronlogic.debug`. APK ini dapat dipasang berdampingan dengan build package utama yang mungkin dibuat pada masa depan. Karena merupakan debug build, APK ini bukan rilis produksi.

Setiap build hanya tersedia setelah workflow selesai berhasil. Jika build gagal, buka log langkah **Build debug APK** untuk melihat penyebabnya.

## 🧭 Arah produk

Electron Logic ingin menggabungkan tiga hal yang sering terpisah:

1. **Merakit dengan bebas** — kanvas rangkaian, kabel, simpul, pin, dan parameter yang dapat diedit.
2. **Melihat perilaku listrik** — tegangan, arus, pengisian kapasitor, sinyal IC, dan visualisasi partikel yang konsisten dengan model.
3. **Memahami penyebabnya** — alat ukur, grafik, indikator gangguan/fault, dan penjelasan tentang apa yang terjadi serta mengapa.

Prinsip produk: **visualisasi tidak boleh berpura-pura menjadi pengukuran fisika.** Jika mesin simulasi belum menghitung suatu efek, UI harus menyebutnya sebagai ilustrasi atau fitur yang belum didukung.

## 🎨 Sistem visualisasi

Mode visualisasi direncanakan dapat dinyalakan secara terpisah:

- **Particle / Current View:** penanda aliran pada jalur berdasarkan hasil arus. Bedakan arah arus konvensional dengan arah gerak elektron; jangan mengklaim melacak elektron individual bila model tidak melakukannya.
- **Charge Storage View:** muatan pada kedua pelat kapasitor dan perubahan medan listrik. Untuk kapasitor ideal, jangan menggambar elektron menembus dielektrik.
- **Noise View:** tandai sumber gangguan, jalur kopling, dan dampak pada sinyal. Noise tidak selalu berarti elektron salah jalur.
- **Fault View:** sorot kondisi yang terdeteksi model, misalnya short circuit, arus berlebihan, polaritas salah, atau operasi di luar batas komponen.
- **Signal / Logic View:** tampilkan bentuk gelombang, status pin, blok internal, dan keadaan logika IC sesuai model yang benar-benar tersedia.

### Bahasa warna awal

| Warna | Makna UI yang diusulkan |
|---|---|
| 🟢 Hijau | Aliran/operasi model dalam kondisi normal |
| 🟠 Oranye | Gangguan yang diinjeksi atau terdeteksi |
| 🔴 Merah | Fault atau kondisi di luar batas yang dikenali model |
| 🔵 Biru | Muatan/medan atau keadaan kapasitor konseptual |
| Sian | Sinyal, koneksi terpilih, atau nilai pengukuran |

Warna adalah petunjuk visual, bukan pengganti angka, satuan, atau penjelasan. Noise dan short circuit adalah fenomena berbeda.

## 🔬 Fisika dan noise

Noise bisa berasal dari proses fisik pembawa muatan (misalnya thermal noise dan shot noise), gangguan catu daya, interferensi elektromagnetik, atau kopling antarsinyal. Efek tersebut tidak selalu terlihat sebagai elektron yang berpindah ke jalur yang salah.

- **Resistor:** tunjukkan arus, jatuh tegangan, dan daya; jangan menyiratkan bahwa elektron seperti mobil yang melambat di resistor.
- **Kapasitor:** muatan terakumulasi pada pelat; arus dan tegangan berubah saat pengisian/pengosongan.
- **NE555 pin 5:** pin CONTROL VOLTAGE mengubah ambang internal. Gangguan di sana dapat mengubah timing, tetapi pin 5 bukan generator noise dengan sendirinya.
- **Short circuit:** tampilkan jalur fault dan nilai arus/tegangan dari model. Jangan menyebut short sebagai noise.
- **Noise fisik:** thermal noise, shot noise, dan jitter harus dihitung dengan metode/model yang sesuai. Menggambar gelombang acak saja tidak cukup.

## 🧩 Komponen yang direncanakan

### Fondasi
- Sumber tegangan DC, ground, resistor, LED, sakelar, kabel, simpul, terminal.
- Kapasitor biasa dan elektrolit, potensiometer, dioda, induktor.
- Probe tegangan, ammeter, multimeter, dan osiloskop sederhana.

### Semikonduktor dan IC
- BJT NPN/PNP (contoh BC547/BC557), MOSFET dan dioda dengan model yang sesuai.
- Gerbang logika, 74HC595, NE555, LM358, CD4017, regulator dan driver motor.
- Tampilan pin, status tegangan/logika, diagram blok internal, dan detail semikonduktor hanya ketika model mendukungnya.

Daftar ini adalah sasaran bertahap, bukan klaim bahwa semua komponen sudah tersedia.

## 🗺️ Roadmap

### Tahap 0 — Prototipe interaksi
- [x] Prototipe web awal dengan pilihan demo, kontrol parameter, animasi, metrik, dan grafik.
- [x] Toggle demonstrasi gangguan dan short circuit.
- [x] Dokumentasi asumsi dan batasan model.
- [ ] Validasi prototipe di berbagai ukuran layar dan browser.
- [ ] Tambahkan tes otomatis untuk persamaan demo.

### Tahap 1 — Fondasi simulator
- [ ] Menetapkan struktur proyek Android dan arsitektur engine.
- [ ] Kanvas yang dapat di-zoom/pan dan komponen yang dapat ditempatkan, dipindahkan, diputar, serta dihapus.
- [ ] Model pin, kabel, node, validasi koneksi, undo/redo.
- [ ] Simpan/buka proyek.

### Tahap 2 — Mesin rangkaian
- [ ] Solver DC untuk jaringan resistor/sumber yang didukung.
- [ ] Model diode/LED dan deteksi rangkaian terbuka atau short berdasarkan konektivitas.
- [ ] Solver transient untuk RC, kondisi awal, dan pengukuran.
- [ ] Tes numerik yang dapat direproduksi dan pesan kesalahan yang jelas.

### Tahap 3 — Visualisasi berbasis hasil model
- [ ] Partikel/penanda arus terhubung dengan besar dan arah arus hasil solver.
- [ ] Muatan pelat kapasitor dan grafik pengisian/pengosongan.
- [ ] Probe, grafik tegangan/arus, serta penjelasan sebab-akibat.
- [ ] Mode normal, noise, dan fault yang terpisah dengan jelas.

### Tahap 4 — Noise dan komponen aktif
- [ ] Sumber gangguan eksternal yang dapat dikonfigurasi.
- [ ] Model thermal noise/shot noise jika engine mendukungnya.
- [ ] Transistor dengan model terdokumentasi dan pemeriksaan batas operasi.
- [ ] NE555 dan IC logika melalui model tervalidasi.

### Tahap 5 — Aplikasi Android dan pengalaman belajar
- [ ] UI Android mobile-first.
- [ ] Tutorial eksperimen, contoh rangkaian, dan panel analisis.
- [ ] Kustomisasi tema, kecepatan animasi, warna kabel, dan tampilan.
- [ ] Ekspor/impor dan berbagi proyek.

## 🏗️ Arsitektur yang dituju

Pisahkan empat lapisan agar visualisasi tidak menjadi pengganti mesin simulasi:

1. **Circuit Editor** — komponen, pin, kabel, properti, proyek.
2. **Simulation Engine** — solver DC/transient dan model komponen.
3. **Analysis Engine** — pengukuran, grafik, noise, serta deteksi fault.
4. **Visual Engine** — animasi partikel, medan, warna, dan diagram yang membaca hasil model.

UI Android direncanakan memakai Kotlin/Jetpack Compose. Prototipe web saat ini berdiri sendiri dan berfungsi sebagai percobaan interaksi awal, bukan fondasi solver final yang sudah tervalidasi.

## 🧪 Prinsip pengembangan

- **Eksperimen bebas:** rangkaian dapat disusun dan dimodifikasi, bukan hanya memilih gambar rangkaian jadi.
- **Fisikanya transparan:** asumsi, satuan, dan batas model selalu dapat diketahui.
- **Model lebih dahulu, animasi kemudian:** visualisasi membaca hasil simulasi.
- **Tidak mengarang data:** nilai pengukuran harus berasal dari model; ilustrasi harus diberi label.
- **Bertahap dan dapat diuji:** fitur belum dianggap selesai sebelum diuji.

## ⚠️ Keselamatan dan batasan

Electron Logic adalah perangkat lunak edukasi. Hasil simulasi tidak menjamin rangkaian fisik akan bekerja identik, terutama bila model masih disederhanakan. Jangan gunakan prototipe ini sebagai satu-satunya dasar desain rangkaian berisiko atau untuk memutuskan sambungan perangkat fisik.

## 🤝 Kontribusi

Saran fitur, model komponen, contoh rangkaian, laporan bug, dan hasil uji di ponsel sangat membantu. Sebelum menambah visualisasi baru, tentukan terlebih dahulu fenomena yang dimodelkan dan data yang mendukungnya.

---

Dibangun dengan rasa ingin tahu — untuk memahami elektronika, satu rangkaian pada satu waktu. ⚡
