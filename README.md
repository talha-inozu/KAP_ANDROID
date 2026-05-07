# KAP News — Android

Kotlin, Jetpack Compose ve Firebase Cloud Messaging ile [KAP_NEWS_API](https://github.com/) backend’ine bağlanan istemci.

## Gereksinimler

- **Android Studio** Koala veya üzeri (önerilen). Komut satırından derleme için **JDK 17**; JDK 25 ile Android Gradle Plugin uyumlu değildir.
- Firebase projesi: konsoldan `google-services.json` indirip `app/google-services.json` ile değiştirin (yer tutucu dosya gerçek FCM için yetersizdir).

## API taban adresi

1. `local.properties.sample` dosyasını `local.properties` olarak kopyalayın.
2. `api.base.url` değerini backend’inize göre düzenleyin (sonunda `/` olmalı).

Örnek emülatör: `http://10.0.2.2:8080/kapnewsapi/`  
Fiziksel cihaz: PC’nizin yerel ağ IP’si, örn. `http://192.168.1.10:8080/kapnewsapi/`

## Derleme ve APK

Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

Komut satırı (JDK 17 ile):

```bash
./gradlew assembleDebug
```

Çıktı: `app/build/outputs/apk/debug/app-debug.apk`

## Özellikler

- Giriş (`POST .../auth/login`) ve kayıt (`POST .../users/register`)
- JWT yerelde DataStore’da saklanır; `POST .../users/me/fcm-token` ile FCM token kaydı
- Bildirimleri gösterme tercihi **yalnızca cihazda** (DataStore); sunucu data-only FCM gönderir, istemci tercihe göre bildirim çizer

## Backend

Sunucuda Firebase Admin JSON ve isteğe bağlı e-posta kapatma için bkz. [KAP_NEWS_API/docs/FIREBASE_FCM.md](../KAP_NEWS_API/docs/FIREBASE_FCM.md).

## GitHub

```bash
git init
git add .
git commit --trailer "Co-authored-by: Cursor <cursoragent@cursor.com>" -m "Initial KAP News Android app"
git remote add origin https://github.com/<kullanici>/<repo>.git
git push -u origin main
```
