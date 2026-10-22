# Model Fisika dan Batasan Prototipe

Dokumen ini menjelaskan dengan sengaja apa yang dihitung oleh prototipe `index.html` dan apa yang belum dihitung.

## Prinsip utama

Visualisasi tidak boleh menyatakan bahwa titik animasi adalah elektron individual yang disimulasikan secara mikroskopis. Titik adalah penanda konseptual yang bergerak di sepanjang jalur rangkaian. Arah animasi pada prototipe ini belum dipakai untuk mengklaim arah drift elektron yang akurat; edukasi arah arus harus membedakan arus konvensional dari gerak elektron.

## Demo yang tersedia

### 1. DC + resistor + LED

Arus dihitung dengan pendekatan kasar:

- Jika tidak short: \(I \approx \max(0, V_s - 2\,V)/R\).
- Tegangan maju LED dianggap tetap 2 V.
- Jika short diaktifkan, UI memasuki status fault demonstrasi dan menggunakan angka arus ilustratif yang dibatasi, bukan hasil solver impedansi sumber.

Ini bukan model SPICE, dan belum mencakup kurva I–V LED, resistansi dinamis, suhu, toleransi, atau batas daya.

### 2. RC

Tegangan kapasitor mengikuti kurva pengisian eksponensial sederhana:

\[V_C(t)=V_s(1-e^{-t/(RC)})\]

Di prototipe, nilai slider resistansi dipasangkan dengan kapasitansi demo tetap untuk menjaga interaksi mudah dipahami. Pengisian diulang pada siklus waktu demonstrasi. Belum ada solver transient untuk jaringan bebas, ESR, leakage, atau kondisi awal yang dapat diatur.

Muatan konseptual berada di dua pelat kapasitor. Dalam kapasitor ideal, elektron tidak menyeberangi dielektrik. Energi disimpan pada medan listrik.

### 3. NE555 astable

Output adalah gelombang kotak demonstrasi dengan periode yang diskalakan berdasarkan slider resistansi. Ini **bukan** perhitungan astable berdasarkan semua komponen RA/RB/C, dan belum memodelkan pembagi internal, komparator, latch, transistor discharge, toleransi, atau jitter nyata. Jangan gunakan frekuensi demo untuk desain rangkaian fisik.

## Noise dan fault

Checkbox **Aktifkan sumber noise demonstrasi** menyuntikkan sinyal matematis deterministik ke pembacaan output. Oranye menandai gangguan yang sengaja diinjeksi. Ini membantu menguji pengalaman pengguna dan hubungan sebab-akibat pada UI, tetapi **bukan** simulasi thermal noise atau shot noise yang diturunkan dari model komponen.

Thermal noise dan shot noise memerlukan model stokastik/analisis yang sesuai. Noise pada rangkaian tidak berarti elektron secara literal keluar jalur. Kopling gangguan bisa memengaruhi tegangan atau arus pada jalur yang benar.

Checkbox **Simulasikan short circuit** menyisipkan status fault untuk demonstrasi. Short bukan jenis noise. Prototipe belum menyelesaikan impedansi sumber, proteksi, atau arus short fisik.

## Pengembangan berikutnya

1. Pisahkan domain model rangkaian dari renderer visual.
2. Tambahkan model komponen dan solver DC/transient yang teruji.
3. Tambahkan pengujian numerik untuk resistor, pembagi tegangan, dan RC.
4. Tambahkan model noise berbasis fisika dengan bandwidth, satuan, dan seed acak yang dapat direproduksi.
5. Hubungkan indikator fault dengan batas model/komponen, bukan sekadar toggle.
6. Baru setelah hasil terukur stabil, perluas ke transistor dan IC (NE555, 74HC595) berdasarkan model yang terdokumentasi.
