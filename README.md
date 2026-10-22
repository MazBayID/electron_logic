# Electron Logic ⚡

> **Electron Logic** adalah proyek aplikasi Android (APK) untuk membangun, mengubah, dan menjalankan simulasi rangkaian elektronik secara visual.

Bayangkan sebuah meja kerja elektronik virtual di ponsel: kita bisa menempatkan komponen, menghubungkannya dengan kabel, mengubah nilai komponennya, lalu menjalankan simulasi untuk melihat bagaimana rangkaian merespons. Tujuan proyek ini adalah memberi kebebasan untuk bereksperimen tanpa harus selalu memiliki komponen fisik di tangan.

## 🎯 Visi Proyek

Membuat simulator elektronik interaktif yang memungkinkan pengguna merakit rangkaian sesuka hati dan mengamati perilaku listriknya—mulai dari rangkaian sederhana hingga eksperimen yang lebih kompleks.

Simulasi akan dikembangkan secara bertahap. Visualisasi gerak elektron, arus, tegangan, dan respons komponen harus mengikuti model simulasi yang sesuai; animasi yang menarik tidak boleh disalahartikan sebagai pengukuran fisik yang akurat jika modelnya belum mendukung hal tersebut.

## 🧩 Komponen yang Direncanakan

Pustaka komponen akan diperluas secara bertahap, termasuk:

- **Sumber daya:** baterai, catu daya DC, ground, dan sumber sinyal.
- **Komponen pasif:** resistor, kapasitor, kapasitor elektrolit (elco), induktor, potensiometer, dan sakelar.
- **Komponen optik:** LED, dioda biasa, dan komponen terkait.
- **Semikonduktor:** transistor BJT, MOSFET, serta dioda khusus.
- **Sirkuit terpadu (IC):** IC logika, timer, op-amp, regulator tegangan, dan komponen lain sesuai kemampuan simulator.
- **Koneksi:** kabel, simpul, terminal, serta konektor untuk menyusun rangkaian.

Setiap komponen idealnya memiliki properti yang dapat diedit, seperti nilai resistansi atau kapasitansi, polaritas, tipe komponen, dan parameter lain yang relevan.

## 🛠️ Pengalaman Merakit

Arah pengalaman pengguna yang ingin dicapai:

1. Menambahkan komponen ke kanvas dengan memilih atau menyeretnya dari panel komponen.
2. Memindahkan, memutar, menghapus, dan menyusun ulang komponen.
3. Menghubungkan pin komponen menggunakan kabel dan simpul.
4. Membuka panel properti untuk mengubah nilai dan konfigurasi komponen.
5. Menjalankan, menjeda, melanjutkan, mengulang, atau mereset simulasi.
6. Mengamati perubahan tegangan, arus, status LED, dan visualisasi elektron sesuai model simulasi.
7. Menyimpan rangkaian, membuka kembali proyek, serta membuat variasi dari rangkaian yang sama.

Antarmuka dirancang agar nyaman digunakan di layar ponsel, dengan kontrol sentuh, zoom, dan kanvas yang mudah dinavigasi.

## ⚡ Tujuan Simulasi

Electron Logic tidak hanya ingin menggambar komponen. Tujuan akhirnya adalah menghubungkan tampilan visual dengan perilaku rangkaian.

Contoh eksperimen yang dapat dikembangkan:

- Mengamati arus pada rangkaian resistor dan perubahan akibat nilai resistansi.
- Melihat proses pengisian dan pengosongan kapasitor atau elco.
- Mengamati LED menyala atau padam berdasarkan kondisi rangkaian dan polaritas.
- Mempelajari transistor sebagai sakelar atau penguat melalui model yang sesuai.
- Merangkai beberapa komponen menjadi rangkaian yang lebih besar.
- Memeriksa tegangan dan arus pada titik-titik rangkaian serta menemukan koneksi yang keliru.

**Catatan ilmiah:** arah gerak elektron dan arah arus konvensional berlawanan. Simulator perlu membedakan keduanya dengan jelas. Model sederhana dapat digunakan untuk tahap awal, sedangkan analisis yang lebih realistis—misalnya respons transien kapasitor, karakteristik transistor, dan perilaku IC—ditambahkan secara bertahap.

## 🗺️ Roadmap

### Tahap 1 — Fondasi
- [ ] Menentukan platform Android dan struktur proyek.
- [ ] Membuat kanvas rangkaian yang dapat diperbesar dan digeser.
- [ ] Menambahkan, memilih, memindahkan, memutar, dan menghapus komponen.
- [ ] Membuat sistem pin, kabel, dan koneksi yang valid.

### Tahap 2 — Komponen Dasar
- [ ] Sumber tegangan DC dan ground.
- [ ] Resistor dengan nilai yang dapat diedit.
- [ ] Sakelar dan LED.
- [ ] Pemeriksaan rangkaian terbuka dan hubungan singkat dasar.

### Tahap 3 — Mesin Simulasi
- [ ] Menghitung tegangan dan arus pada rangkaian dasar.
- [ ] Memvisualisasikan aliran arus dan gerak elektron secara konsisten dengan model yang digunakan.
- [ ] Menampilkan status dan nilai simulasi secara langsung.
- [ ] Menambahkan kontrol mulai, jeda, langkah, dan reset.

### Tahap 4 — Komponen Dinamis
- [ ] Kapasitor dan elco, termasuk proses pengisian/pengosongan.
- [ ] Dioda dan karakteristik LED yang lebih baik.
- [ ] Transistor BJT dan MOSFET dengan model yang sesuai.
- [ ] Induktor dan komponen tambahan.

### Tahap 5 — IC dan Rangkaian Kompleks
- [ ] Menambahkan IC berdasarkan model atau fungsi yang terdokumentasi.
- [ ] Mendukung rangkaian gabungan dan subrangkaian.
- [ ] Menambahkan alat ukur virtual seperti voltmeter dan ammeter.
- [ ] Menyediakan contoh rangkaian dan panduan belajar.

### Tahap 6 — Proyek dan Personalisasi
- [ ] Menyimpan dan memuat rangkaian.
- [ ] Duplikasi rangkaian dan komponen buatan pengguna jika memungkinkan.
- [ ] Undo/redo.
- [ ] Tema, pengaturan tampilan, serta peningkatan kenyamanan penggunaan.

Roadmap ini adalah rencana awal, bukan klaim bahwa fitur-fitur tersebut sudah tersedia. Status akan diperbarui seiring implementasi dan pengujian.

## 🧪 Prinsip Pengembangan

- **Eksperimen bebas:** pengguna dapat merangkai dan memodifikasi rangkaian tanpa dibatasi pada contoh tetap.
- **Modular:** komponen baru dapat ditambahkan tanpa harus menulis ulang seluruh aplikasi.
- **Interaktif:** perubahan nilai atau koneksi harus tercermin pada hasil simulasi.
- **Transparan:** jelaskan asumsi model, batasan simulasi, dan kondisi rangkaian yang tidak valid.
- **Mobile-first:** kontrol dan kanvas dioptimalkan untuk Android.
- **Bertahap dan dapat diuji:** setiap fitur diperiksa sebelum dianggap selesai.

## ⚠️ Batasan dan Keselamatan

Electron Logic adalah simulator perangkat lunak. Hasil simulasi tidak menjamin rangkaian fisik akan bekerja identik, terutama jika model komponen masih disederhanakan. Jangan menggunakan hasilnya sebagai satu-satunya dasar untuk desain yang berisiko, dan jangan menghubungkan perangkat fisik ke rangkaian berbahaya berdasarkan simulasi saja.

## 📦 Status Proyek

**Tahap saat ini: perencanaan dan fondasi awal.** Daftar fitur di atas menggambarkan arah pengembangan; fitur baru dianggap selesai setelah implementasi dan pengujian.

## 🤝 Kontribusi

Ide komponen, contoh rangkaian, laporan bug, dan usulan peningkatan antarmuka sangat membantu. Perubahan sebaiknya dibuat secara bertahap agar mesin simulasi, visualisasi, dan antarmuka tetap konsisten.

---

Dibangun dengan rasa ingin tahu—untuk menjelajahi dunia elektronika, satu rangkaian pada satu waktu. ⚡
