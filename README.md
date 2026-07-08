# Cape39 - Spigot/Paper Eklentisi (1.20+)

Banner'lardan pelerin yapan sunucu eklentisi.

## Nasıl çalışıyor?

1. **Cape Frame:** 2 demir külçesi + ortada 1 çubuk (yan yana, tek sıra) craftlanır.
   Görünüm olarak demir külçesi gibi durur ama üzerinde özel isim ve gizli
   bir etiket var ("Pelerin Çerçevesi").
2. Bu çerçeveyi **herhangi bir banner** ile aynı crafting tablosuna koyunca,
   banner'ın ana rengini taşıyan bir **deri göğüslük** (Banner Cape) çıkar.
   - Deri zırhın rengi kod ile banner'ın rengine göre ayarlanıyor
     (resource pack gerekmez, gerçekten o renkte görünür).
   - Banner'daki desenler (patterns) şu an sadece **lore** (item açıklaması)
     içinde "Desen sayısı: X" olarak gösteriliyor; gerçek görsel desenleri
     zırhın üzerinde göstermek resource pack + custom model data gerektirir.

## Neden deri göğüslük, gerçek "pelerin" değil?

Vanilla Spigot/Paper API'sinde:
- Yeni bir giyim slotu (cape slotu) eklenemez — sadece var olan zırh
  slotlarını (kask/göğüslük/pantolon/bot) kullanabiliriz.
- Yeni bir 3D model/texture eklenemez (resource pack olmadan).
- **Ama** deri zırhın rengi kod ile değiştirilebiliyor, bu yüzden en
  gerçekçi "banner rengini yansıtan giyilebilir eşya" için deri göğüslüğü
  seçtim.

İstersen ileride bir **resource pack** hazırlayıp (custom model data ile)
gerçek bir pelerin görünümü ekleyebiliriz — bu ayrı bir iş, istersen ona da
geçebiliriz.

## Telefondan derleme (GitHub Actions ile) — bilgisayar gerekmez!

Bu proje `.github/workflows/build.yml` içeriyor. Bu sayede GitHub, kodu
senin yerine bulutta derliyor. Adımlar:

1. **Repo oluştur / dosyaları yükle** (telefondan, GitHub mobil sitesi veya uygulaması üzerinden):
   - github.com/new ile boş bir repo aç (daha önce anlattığım gibi)
   - Reponun ana sayfasında **"Add file" → "Upload files"** ile bu zip'in
     içindeki TÜM dosya ve klasörleri yükle (klasör yapısını koru:
     `src/...`, `pom.xml`, `.github/workflows/build.yml` vs. aynı yerde
     kalmalı). Mobil tarayıcıdan da bu yükleme ekranı çalışır.
   - "Commit changes" de.

2. **Derlemenin otomatik başlamasını bekle:**
   - Repo sayfasında üstte **"Actions"** sekmesine gir.
   - "Build Spigot Plugin" adında bir workflow çalışıyor/çalışmış olacak
     (push yapınca otomatik tetiklenir).
   - Yeşil tik ✅ olunca derleme başarılı demektir. Kırmızı çarpı ❌ olursa
     üstüne tıklayıp hata logunu bana yapıştır.

3. **Jar dosyasını indir:**
   - Başarılı olan workflow çalıştırmasına (run) tıkla.
   - En altta **"Artifacts"** bölümünde `cape39-spigot-jar` göreceksin.
   - Ona dokunup indir (zip olarak iner, içinde gerçek `.jar` dosyası var).

4. Bu jar'ı sunucunun `plugins/` klasörüne koy (sunucuya nasıl dosya
   atacağın, sunucunu nasıl barındırdığına bağlı — FTP, dosya yöneticisi,
   panel vs.)

Bu şekilde bilgisayar açmadan, tamamen telefondan kod → derleme → jar
akışını tamamlayabilirsin.



```bash
mvn clean package
```
Çıktı: `target/spigotcape-1.0.0.jar`

Bu jar'ı sunucunun `plugins/` klasörüne koyup sunucuyu yeniden başlat.

**Not:** Bu proje bu ortamda derlenmedi/test edilmedi (internet erişimi yok).
Kendi bilgisayarında `mvn clean package` çalıştırınca hata alırsan bana
yapıştır, birlikte düzeltelim. Spigot API'sinin Maven deposuna erişim için
internetin olması lazım (pom.xml içindeki `spigot-repo` deposu kullanılıyor).

## GitHub'a yükleme

```bash
cd cape39-spigot
git init
git remote add origin https://github.com/<kullanici-adin>/<repo-adi>.git
git add .
git commit -m "Cape39 Spigot eklentisi ilk sürüm"
git branch -M main
git push -u origin main
```

## Dosya yapısı

```
src/main/java/com/cape39/spigotcape/
  CapePlugin.java              -> ana plugin sınıfı (onEnable/onDisable)
  util/ItemFactory.java        -> frame ve cape item'larını oluşturur
  util/Recipes.java            -> tarifleri kaydeder
  listeners/CraftListener.java -> banner rengini crafting anında uygular

src/main/resources/
  plugin.yml
```
