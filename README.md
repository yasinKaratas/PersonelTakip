# Personel Takip
Bu program sayesinde gün içerisinde anlık olarak personellerinizin nerede olduğunu, ne kadar süredir orada olduğunu harita üzerinde görebilir, geriye dönük olarak dilediğiniz zaman aralığındaki rotasını yine harita üzerinde görebilirsiniz.

## Nasıl Çalışır?
Tek uygulama kullanarak hem şirket yetkilisi hem de diğer personeller, kullanıcı adı ve şifresine tanımlanmış olan yetkilere göre başka bir uygulamaya ihtiyaç olmaksızın işlemlerini gerçekleştirebilirler.
İşveren dilerse bir web sayfasında anlık haritayı görebilir, bir monitör veya projeksiyon cihazıyla daha büyük şekilde anlık izleyebilir.
Program tüm veritabanı işlemlerini http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx adresindeki webservice aracılığıyla yapar.

## Nasıl Kullanılır?
- Programa ilk girişte şirket kodu, TC kimlik no ve şifre girilip giriş yapılır. Bu bilgiler -şifre hariç- işveren tarafından daha önce sisteme girilmiş olur.
- Çalışan, veritabanında kaydının bulunması ve fakat şifresinin bulunamaması halinde girişte kendi şifresini -cihaza verdiği onay sonrasında- belirler.
- Giriş sırasında TC kimlik numarasının, kendine has standartlara uygun olup olmadığı kontrol edilir. Değilse giriş yapılamaz.
- Girişen şifre veritabanına MD5 ile kripto hale getirilerek kaydedilir. Böylece kısmen veri güvenliği sağlanmış olur.
- Çalışan, şifresini unuttuğunda, giriş ekranındaki "Şifremi unuttum" yazısını tıklatarak, yeni şifre alabilir.
- Cihazın GPS alıcısı kapalı olduğunda yetkili kişilere uyarı gider.
- Uygulama, zaman bilgisini GPS uydusundan aldığından, cihazın tarih veya saatiyle oynanması, veri doğruluğunu etkilemez.
