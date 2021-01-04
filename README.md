# Personel Takip
Bu program sayesinde anlık olarak personellerinizin nerede olduğunu, yolculuk hızının saatte kaç km. olduğunu harita üzerinde görebilirsiniz.

## Nasıl Çalışır?
Tek uygulama kullanarak hem şirket yetkilisi hem de diğer personeller, kullanıcı adı ve şifresine tanımlanmış olan yetkilere göre başka bir uygulamaya ihtiyaç olmaksızın işlemlerini gerçekleştirebilirler.\
Program tüm veritabanı işlemlerini http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx adresindeki C# ve ASP.NET ile yazdığım webservice aracılığıyla yapar.\
Program çalıştırılıp kullanıcı girişi başarılı bir şekilde yapıldığında, arka plan servisi çalışmaya başlar. Programdan çıkılsa da servis GPS verilerini göndermeye devam eder.\
Firma kodunu, firmaya bu yazılımı satan yazılım firması belirler ve "müşteri numarası" olarak muhatabına bildirir. Böylece her personelin hangi şirkette, hatta hangi şubede çalıştığı belirlenmiş olur, filtrelemelerde kolaylık sağlar.\
Firma kodu, ilk 2 hanesi yılı gösterecek şekilde sırayla verilir. Örneğin 2020 yılının 15. müşterisinin firma kodu 200015 olacaktır.\
Dileyen herkes Firma Kodu: 200003 Şifre: 1111aaaa TC Kimlik no admin için: 12345678901 standart kullanıcı için 12345678902 veya 12345678903 girip uygulamayı deneyebilirler.\
Girilen şifre veritabanına MD5 ile kripto hale getirilerek kaydedilir. Böylece kısmen veri güvenliği sağlanmış olur.\
Giriş sırasında TC kimlik numarasının, kendine has standartlara uygun olup olmadığı kontrol edilir. Değilse uyarı verir. Gerekirse giriş yapması kod tarafında engellenebilir.\
Uygulama, zaman bilgisini GPS uydusundan aldığından, cihazın tarih veya saatinin yanlış olması, veri doğruluğunu etkilemez.\
Program personelin anlık hızını da GPS'ten alır, veritabanına kaydeder ve anlık olarak gösterir.\
GPS verileri 3 saniye ve 3 metre hassasiyetle çekilir. İhtiyaca ve kullanılan cihazların kapasitelerine göre süre ve mesafe hassasiyeti kod tarafında artırırlıp azaltılabilir.\
Uygulama dili İngilizce ve Türkçe'dir. Cihazın sistem diline göre otomatik olarak dil seçimi yapar.\
Firma Kodu ve TC Kimlik numarası giriş alanlarında klavye sadece numerik olurken, şifre alanında alfa-nümerik olur.

## Nasıl Kullanılır?
- Kullanıcılar şirket personeli olduğundan, işe girişte verilen evraklara göre sisteme girilmiş olan TC kimlik numaraları ve kendi belirleyecekleri şifreleriyle login olurlar.
- Programa ilk girişte şirket kodu, TC kimlik no ve şifre girilip giriş yapılır. Firma kodu işveren tarafından personele önceden bildirilir.
- Çalışan, veritabanında kaydının bulunması ve fakat şifresinin bulunamaması halinde girişte kendi şifresini belirlemiş olur.
- Şifresini unutan personel için koymuş olduğumuz "Şifremi Unuttum" alanı sadece bilgilendirme amaçlıdır. Şifresini unutan personel, şirket yetkilileriyle iletişim kurarak sıfırlama talep eder.
- Kullanıcı admin yetkisine sahip ise kendi ekranındaki haritada tüm personellerin konumlarını anlık olarak görür.
- Kullanıcı admin değilse sadece kendi konumunu haritada görecektir.
- Admin kullanıcı haritadaki markerlara dokunarak o personelle ilgili detayı görebilir.
