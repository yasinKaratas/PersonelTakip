# Personel Takip
Bu program sayesinde anlık olarak personellerinizin nerede olduğunu, yolculuk hızının saatte kaç km. olduğunu harita üzerinde görebilirsiniz.

## Nasıl Çalışır?
Tek uygulama kullanarak hem şirket yetkilisi hem de diğer personeller, kullanıcı adı ve şifresine tanımlanmış olan yetkilere göre başka bir uygulamaya ihtiyaç olmaksızın işlemlerini gerçekleştirebilirler.\ 
Program tüm veritabanı işlemlerini http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx adresindeki C# ve ASP.NET ile yazdığım webservice aracılığıyla yapar.\ 
Firma kodu, ilk 2 hanesi yılı gösterecek şekilde sırayla verilir. Örneğin 2020 yılının 15. müşterisinin firma kodu 200015 olacaktır.\ 
Firma kodunu, firmaya bu yazılımı satan yazılım firması belirler ve "müşteri numarası" olarak muhatabına bildirir. Böylece her personelin hangi şirkette, hatta hangi şubede çalıştığı belirlenmiş olur, filtrelemelerde kolaylık sağlar.\ 
Deneme yapmak için dileyen kullanıcılar şifre tümünde geçerli olmak üzere: Firma Kodu: 200003 Şifre: 1111aaaa Admin için TC Kimlik: 12345678901; standart kullanıcı için 12345678902 veya 12345678903 girip uygulamayı deneyebilirler.\
Şifresini unutan personel için koymuş olduğumuz Şifremi Unuttum alanı sadece bilgilendirme amaçlıdır. Şifresini unutan personel yetkililerle iletişim kurarak sıfırlama talep edebilir.\

## Nasıl Kullanılır?
- Kullanıcılar şirket personeli olduğundan, işe giriş yapıldığında sisteme girilen TC kimlik numaralarıyla ve uygulamaya ilk girişte belirleyecekleri şifreyle login olurlar.
- Programa ilk girişte şirket kodu, TC kimlik no ve şifre girilip giriş yapılır. Bu bilgiler -şifre hariç- işveren tarafından daha önce sisteme girilmiş olur.
- Çalışan, veritabanında kaydının bulunması ve fakat şifresinin bulunamaması halinde girişte kendi şifresini -cihaza verdiği onay sonrasında- belirler.
- Giriş sırasında TC kimlik numarasının, kendine has standartlara uygun olup olmadığı kontrol edilir. Değilse uyarı verir.
- Girilen şifre veritabanına MD5 ile kripto hale getirilerek kaydedilir. Böylece kısmen veri güvenliği sağlanmış olur.
- Çalışan, şifresini unuttuğunda, giriş ekranındaki "Şifremi unuttum" yazısını tıklatarak, şifre sıfırlaması talep edebilir.
- Uygulama, zaman bilgisini GPS uydusundan aldığından, cihazın tarih veya saatiyle oynanması, veri doğruluğunu etkilemez.
