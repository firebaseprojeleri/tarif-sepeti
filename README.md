#Turkcell Geleceği Yazanlar Tarif Sepeti

Tarif sepeti, Geleceği Yazan Kadınlar Eğitim Etkinlikleri kapsamında oluşturulmuş Firebase veritabanı kullanan bir android uygulamasıdır.

# Kullanımı
* Tarif Sepetini bilgisayarınıza indirip Android Studio ile açın.
* Kendi Firebase konsolunuzda herhangibir isimle yeni bir proje oluşturun.
* Bu firebase projenizin google-services.json dosyasını Tarif sepeti projenize ekleyin.
* Firebase uygulamanızın Authentication sayfasında E-posta/Şifre oturum açma yöntemini etkin hale getirin.
* Firebase uygulamanızın Storage sayfasında "images" adında bir klasör oluşturun.
* Yine Storage sayfasında bulunan bucked url'nizi kopyalayıp android projemizin utils paketindeki FireUtils.java dosyasındaki APP_STORAGE_BUCKED_URL değişkenine yapıştırın.
* Artık uygulamanızı çalıştırabilirsiniz.