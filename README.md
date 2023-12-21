# MİKROSERVİS

 ### Kurulum Adımları;
***
#### 1.1. Boş bir Gradle projesi kurduk.
#### 1.2. Proje içinde dependencies.gradle dosyasını oluşturduk
#### 1.2.1. ext{} bloğu içerisinde 
- version[ ]
- libs[ ]
    #### blokları açtık.  
#### 1.2.2. version{} bloğu içine [***maven repository***](https://mvnrepository.com/) sitesinden kullanaıcağımız teknolojilerin versiyonlarını sabit bir değere atadık.
```
springBoot : "2.7.10",
```
#### 1.2.3. libs{} bloğu içirisine ise kullanılcak teknolojilerin gradle(short) bağımlılıklarını tanımladık.
#### 1.3. Kullanılcak teknoloji tüm mikroservislerde kullanılcaksa onu ana çatının build.gradle dosyasına çağırdık.

```
springBootDataJpa    : "org.springframework.boot:spring-boot-starter-data-jpa:${versions.springBoot}",
```
#### 1.4. Eğer özel ise onuda mikroservisin kendi build.gradle dosyasında dependenceis bloğunda çağırdık. Ama springBoot projesi olduğunu belirtmek için buildscript kod bloğunu tüm modüllere vermek gereklidir.
```
buildscript {
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${versions.springBoot}")
    }
}
dependencies {
    implementation libs.springBootDataJpa
}
```
#### 1.5. Gradle Reflesh yapıp projemize teknolojileri ekledik.
#### 1.6. Resource altında application.yml dosyası oluşturarak port, hibernate, veritabanı bilgilerini belirttik.

###  Modül işlemleri;
***

#### 1.1. Katmanlı mimari uygulayarak katmanlarımızı oluşturduk.
#### 1.2. Entity katmanı ile başlayarak @Entity anatasyonu kullanarak veritabanı sınıfıdır dedik.
#### 1.3. Repository katmanınına interface bir sınıf oluşturup JpaRepository'i extend aldık. Ve kullanıcağımız sorgu metotlarını yazdık.
```
Optional<Auth> findOptionalByEmailAndPassword(String email, String password);
```
#### 1.4. Service ve Controller katmanlarını oluşturduk. Register Metodu yazıldı.

###  OPENFEIGN;
***    
#### 1.1. Mikroservisler arası haberleşmeyi sağlayan önemli bir teknolojidir. Projemizde dahil etmek için [***maven repository***](https://mvnrepository.com/) üzerinden gradle(short) bulduk ve dependencie.gradle dosyamıza ekledik.
- version{ }
```
springCloud         : "3.1.6",
```
- libs{ }
```
 springCloudOpenFeign    : "org.springframework.cloud:spring-cloud-starter-openfeign:${versions.springCloud}",
```
Ana yapı build.gradle --> dependencies{ }
```
 implementation libs.springCloudOpenFeign
```
#### 1.2. Haberleşmeyi başlatıcak olan modül hangisi ise onun Run classına ***@EnableFeignClients*** anatasyonunu eklemeliyiz.
#### 1.3. FeignClient metotları Manager paketi altında oluşturacağımız interface olarak oluşturulan sınıfta yazılır.
#### 1.4. ***@FeignClient()*** anatasyonu yazılarak içine name ve haberleşiceği, bilgiyi aktarıcağı modülün url bilgisi yazılır.
```
 @FeignClient(
        name = "user-profile-service",
        url = "http://localhost:9093/userprofile"
)
```
#### 1.5. Haberleşme ağını tamamladıktan sonra ihtiyacımıza göre metot yazıyoruz PostMapping, GetMapping ile endPoint veriyoruz controller ile aynı isimde.

##  Docker;
***
###  NoSQL ~ MongoDB;
***
#### 1.1. Docker ve mongoDB compass bilgisayarımıza indiriyoruz.
#### 1.2. DockerHup üzerinden mongoDB için indirme kodunu alıyoruz ve herhangi bir terminal açtıktan sonra buraya yapıştırıp Enter tıklıyoruz.
```
 docker run --name dockermongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=BilgeAdmin -e MONGO_INITDB_ROOT_PASSWORD=root -d mongo
```
#### 1.3. MongoDB Compass açıyoruz ve orada docker içinde ki mongoDB ye veridğim username ve şifre bilgileri ile connect diyip bağlanıyorum
#### 1.4. Veritabanı dünyanın en önemli şeyi olduğu için buradaki admin rolu ile açık bir şekilde username password ile işlem yapmamalıyız.
#### 1.5. Bunun yerine admin olarak bizim yetkilendirmesini yaptığımız bir kullanıcı tanımlamalıyız. Bunun için ilk önce bir veritabanı oluşturmam gerekir.
#### 1.6. Sol altta yer alan mongosh terminal üzerinden kurduğum veritabanına geçiyorum
```
 use DB_ADI;
```
#### 1.7. Ardından bir kullanıcı tanımlıyorum. Ne yapmaya yetkisi var username, password tanımlıyorum.
```
db.createUser({user: "Java7User",pwd: "root",roles: ["readWrite", "dbAdmin"]})
```
#### 1.8. Artık ilk Bağlanma işleminde Bu yeni oluşturduğum kişi ile giriş yapmam gereklidir.
#### 1.9. Kurulum işlemleri bittikten sonra artık mongodb projeme dahil etmeliyim. Bunun için yine [***maven repository***](https://mvnrepository.com/) yararlanıyorum.
#### 2.0. dependencies.gradle dosyama geliyorum ve versions yapısını SpringBoot üzerinden alıyor mongoDb o zaman sadece libs kısmına kodu ekliyorum.
```
  springBootDataMongoDB   : "org.springframework.boot:spring-boot-starter-data-mongodb:${versions.springBoot}",
```
#### 2.1. Hangi Mikro serviste kullanıcaksam onun build.gradle dependencies kısmına gidip implement ediyorum.
```
implementation libs.springBootDataMongoDB
```
#### 2.2. Son aşama olarakta veritabanı bağlantı ayarlarını yapmak için kullancağımız mikro servisin ***application.yml*** dosyasına geliyoruz.
- PostgreSQL bağlantısındayken application.yml;
```
server:
  port: 9093

spring:
  datasource:
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/FaceAhmetUserProfileDB
    username: postgres
    password: 1234bilgeADAM
  jpa:
    hibernate:
      ddlAuto: update
    showSql: true
    properties:
      hibernate:
        format_sql: true
```
- MongoDB bağlantısında application.yml;
```
server:
  port: 9093

spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: FaceAhmet
      username: Java7User
      password: root
```

#### 2.3. Başarılı bir şekilde mongoDB bağlantısı yapıldı projede.

###  JWT;
***
#### 1.1. Sistemimizde başarılı şekilde kayıt olduktan sonra login olan kullanıcılara bizim ürettiğimiz bir token ile sistemde güvenli bir şekilde gezinebilmelidir.
#### 1.2. Bu güvenlikli yapı hem sistemimizi hemde kullanıcıyı korur. Kullanıcı için belli bir süreden sonra tekrar oturum açmaya yönlendirilerek güvenlik sağlanır
#### 1.3. Bizim sistemimiz içinse token bizim tarafımızdan üretildği için yabancı bir eylem içeride aktif olamaz. Bu Yöntem için JWT Token Teknolojisini kullanıyoruz.
- depedencies.gradle version{ };
```
javaJWT : "4.4.0",
```
- depedencies.gradle libs{ };
```
 javaJWT : "com.auth0:java-jwt:${versions.javaJWT}",
```
#### 1.4. Bu sistemi tüm yapıda kullanıcağımız için ana çatının build.gradle dependencies kısmına eklemeliyim.
```
implementation libs.javaJWT
```
#### 1.5. JWT Token da kullandığımız şifreleme anahtarı vb. önemli bilgileri environment variable olarak ya intellij veya windows sisteminde tutmamız daha güvenlidir.
###  ConfigServer;
***

#### 1.1. ConfigServer bizim için tüm yapılandırma dosyalarımızı tek bir yerden yönetmeye yarayan bir teknolojidir. Starter, server,client olmak üzere 3 adet bağımlılık eklemek gereklidir. Versiyonunu springCloud'tan alıyor.
```
springCloudStarterConfig: "org.springframework.cloud:spring-cloud-starter-config:${versions.springCloud}",
springCloudConfigServer : "org.springframework.cloud:spring-cloud-config-server:${versions.springCloud}",
springCloudConfigClient : "org.springframework.cloud:spring-cloud-config-client:${versions.springCloud}",
```
#### 1.2. ConfigServer modülünün build.gradle starter ve server bağımlılıkları eklenir
```
implementation libs.springCloudStarterConfig
implementation libs.springCloudConfigServer
```
#### 1.3. Application.yml dosyası taşınan modüllerde build.gradle dosyalarına Client bağımlılığı eklenir.
```
implementation libs.springCloudConfigClient
```
#### 1.4. ConfigServer aktif olabilmesi için configserver modülünde run sınıfında ***@EnableConfigServer*** anatasyonu eklenmelidir.
#### 1.5. Diğer modüllerin yml dosyalarını configServer modülü altındaki config-repo klasörü altına taşıdıktan sonra configServer modülünün yml dosyasına yayın yapıcağı dosyaların yolunu ve portunu vermek için;
```
server:
  port: 8888

spring:
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config-repo
```
#### 1.6. Diğer modüllerin yml dosyalarında ise içlerini temizledikten sonra yapılandırma dosyalarının yolunu vermek için kod bloğu yazıyorum.
```
spring:
  cloud:
    config:
      uri: http://localhost:8888
  config:
    import: "configserver:"
  application:
    name: auth-service-application
```
#### 1.7. Burada yazdığımız name ile config-repo altında ki yml dosyasının adı birebir aynı olmalıdır. Ve name yazarken sonuna .yml diye belirtmeye gerek yoktur.
#### 1.8. Önemli!!! Yayını alan modüllerde build.gradle dosyasında client bağımlılığını unutmamak gerekir.
###  ConfigServerGit;
***
#### 1.1. ConfigServer ile aynı mantıkta ama localde değil bildiğimiz githup reposundan çekeriz yapılandırma dosyalarını. 
#### 1.2. Burada iki şey lazım bana bir private tanımlanmış bir githup reposu birde bu projeye özel githup password.(githup/settings/new Token'dan alınıyor.) 
#### 1.3. Bunları temin ettikten sonra configServerGit yml dosyası oluşturuyoruz ve yapılandırma kodlarını yazıyoruz.
```
server:
  port: 8888

spring:
  application:
    name: config-server-git
  cloud:
    config:
      server:
        git:
          uri: "https://github.com/AhmetTolay/ConfigServerGitFace.git"
          default-label: main
          skip-ssl-validation: true
          username: AhmetTolay
          password: ${GIT_JAVA7_PASSWORD}
```
#### 1.4. application.yml yanına birde bootstrap.yml dosyası oluşturuyoruz. Bu dosyada hangi configurasyona dahil olacağımızı bildiriyoruz
```
spring:
  application:
    name: config-server-git
  profiles:
    active: development
```
#### 1.5. Artık projemiz yapılandırma dosyaları özel olarak saklanan githup repo'sundan gelmektedir.
###  GATEWAY;
***
#### 1.1. Dışarıdan gelen istekler diretk mikro servislere ulaşırsa her bir mikro servis içinde security işlemi yapman gerekir. Bu aynı işlemi bir sürü kez yapmak anlamına gelir.
#### 1.2. Bunun yerine tüm yapımızı sur ile çevreleyip bir tek kale kapısından giriş yaptırırsan ve orada security yaparsan tek yerden hem güvenlik hem yönlendirme yapabilmiş oluyoruz.
- dependencies.gradle;
```
springCloudGateway : "org.springframework.cloud:spring-cloud-starter-gateway:${versions.springCloud}",
```
- Gateway modülü build.gradle;
```
implementation libs.springCloudGateway
```
#### 1.3. Resource altında applicaiton.yml dosyası oluşturarak yapılandırma kodlarını yazıyoruz.
```
server:
  port: 80

spring:
  application:
    name: api-gateway-service
  main:
    web-application-type: reactive
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://localhost:9090 
          predicates:
            - Path=/auth/**    
        - id: user-service
          uri: http://localhost:9093
          predicates:
            - Path=/userprofile/**    
      default-filters:
        - RewritePath= /(?<segment>.*) , /api/v1/$\{segment}
```
#### 1.4. Bu yapılandırma ile 9090 portuna /auth şeklinde bir istek gelirse bunu auth mikro servisine 9093 /user şeklinde istek gelirse bunuda userprofile mikro servisine yönlendirecek.
#### 1.5. Gateway sayesinde birde url kısmında bizim gerçek yol bilgilerimiz(9090/api/v1/auth/getpage) bulunmaz. Sadece bizim belirlediğimiz öçüde bir yol bilgisi(auth/getpage) görünür. Bu sayede biraz daha güvenli hale getirmiş oluyoruz yapımızı.
#### 1.6. Reel projelerde genellikle ApiGateway kullanılır. Çünkü apigateway bulut üzerinden daha sistematik bir arayüz ile tasarlanmış oluyor. Bizim kodlamayla yazdığımız gateway daha statik bir yapı olduğundan genelde bulut sistemlerde apigateway kullanılır.
###  RABBITMQ;
***
#### 1.1. Openfeign gibi servisler arası haberleşmeye yarar ama farkı vardır. Auth servisinden user servisine bilgi gittiği sırada user kapalı olduğunu düşünelim. Böyle zamanda istek iptali gibi bir durum olmamalı, benzer şekilde alışveriş sitelerinde stok verisi tam çöktü. O an olmayan stoktaki ürünü satmaman lazım. Stok verisi çöktüğü anda istek iptal olmaz, Rabbitmq sayesinde kuyrukta bekler istek. Sistem tekrar açılınca sırayla tüm yapılmayna işlemler yapılır.
#### 1.2. RabbitMQ sayesinde tüm sistemimiz işlem sırasını korur ve sırasıyla işlemlerini yapar.
#### 1.3. Producer, exchange,bindind,queue, Consumer ana yapılarından oluşmaktadır.
#### 1.4. Exchange yapısının 3 tipi vardır;
- Direct ~~ Birebir ilişkilerde kullanılan modeldir.
- Topic ~~ Örneğin türk müşteriye türkçe mesaj yabancı müşteriye ingilizce mesaj atıcaksan bunu topic ile yapabilirsin
- Fanout ~~ Örneğin kullanan tüm kullanıcılara Bayram mesajı atıcaksan, bireçok ilişki varsa fanout kullanılabilir.
#### 1.5. Docker üzerinden rabbitMQ indirmek ve kullanmak için herhangi bir terminale aşağıdaki kodu yazıp enter tıklıyoruz.
```
docker run -d --name my-rabbitmq -e RABBITMQ_DEFAULT_USER=java7 -e RABBITMQ_DEFAULT_PASS=root -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```
#### 1.6. Tarayıcada http://localhost:15672/ şeklinde yazarsak açılan sayfada username ve password girdğimizde rabbitmq arayüz sistemine ulaşabiliriz. Projemize eklemek içinde;
- depedencies.gradle;
```
springBootAmqp          : "org.springframework.boot:spring-boot-starter-amqp:${versions.springBoot}",
```
- Kullanılcak Modüllerin build.gradle;
```
implementation libs.springBootAmqp
```
#### 1.7. RabbitMQ teknolojisinin öncelikle yapılandırma(exchange, kuyuruğu ve yolu) dosyası yazılmalı. Bunun için öncelikle config paketi altında rabbitmqconfig isminde paket açılıp yapılandırılmalıdır. Ardından katmanlı mimarimizde rabbitmq isminde ana yapıya paket açıp içine Producer, Model, Consumer adında paketler açılmalıdır.
#### 1.8. Model paketine açtığımız sınıfı rabbitMQ ya mesaj olarak ileteceğiz. Bu mesajı rabbitmq kuyruğa işleyecek. Fakat işlerken Base64 olarak işler. Okurkende Base64'ten dönüştürek okur. Bu yüzden her iki mikro servisteki model sınıfları Serializable'den implements olmalıdır.
#### 1.9. Auth servisinden user servisine iletileceği için mesaj auth servisinde producer, user servisinde de consumer paketleri olmalıdır.
#### 2.0. Producer paketinde oluşturduğumuz sınıfın içinde; RabbitTemplate sayesinde bir convertAndSend metodunu kullanarak yazdığımız exchange ve binding sayesinde modeli userservisine yolluyoruz. 
#### 2.1. Son olarak configserverda config-repo altındaki her iki yml dosyalarınada bağlantı kodlarını yazmam gerekiyor rabbitmq için(spring taginin içerisinde);
```
rabbitmq:
    host: localhost
    port: 5672
    username: java7
    password: root
```
#### 2.2. İlk olarak producer ile mesaj yolladık sonra karşı tarafta consumer'da listener ile dinleyiciyi açtık ve kuyruğu dinletiyoruz. Bir mesaj geldiği anda hemen yakalayıp içindeki işlemi gerçekleştirecek.
###  Zipkin;
***
#### 1.1. Hangi servise kaç istek geliyor, gidiyor, ne kadar cpu harcıyor, yavaş mı çalışıyor gibi sorularımızın cevabını veren teknolojidir.
- Docker zipkin indirmek;
```
docker run --name zipkinfb -d -p 9411:9411 openzipkin/zipkin
```
- dependencies.gradle;
```
springCloudSleuth  : "org.springframework.cloud:spring-cloud-starter-sleuth:${versions.springCloud}",
springCloudZipkin  : "org.springframework.cloud:spring-cloud-sleuth-zipkin:${versions.springCloud}",
```
- ana çatı build .gradle (tüm sistemi izlemesi gerekiyor);
```
implementation libs.springCloudSleuth
implementation libs.springCloudZipkin
```
#### 1.2. Zipkin default olarak 9411 portunda çalışır. Son olarak kullanacak tüm modüllerin yml dosyalarına yapılandırma dosyası eklenmelidir.
```
spring:
  spring:
    zipkin:
      enabled: true
      base-url: http://localhost:9411
      service:
        name: user-service
```
#### 1.3. Burada service name her modül için benzersiz olmalıdır ki zipkin anlasın ne kadar servis var takip edeceği.
###  Redis;
***
#### 1.1. Redis In-Memory yani ram üzerinde çalışan bir veri deposudur. Redis verileri bellek üzerinde <key,value> çifti olarak tutmaktadır, burada herbir anahtara denk gelen değerler farklı veri yapılarında tutulabilmektedir.
- Docker indirme ve kullanma;
```
docker run --name localredis -d -p 6379:6379 redis
```
- dependencies.gradle;
```
 springBootDataRedis : "org.springframework.boot:spring-boot-starter-data-redis:${versions.springBoot}",
```
- Kullanmak istediğin modülün build.gradle;
```
 implementation libs.springBootDataRedis
```
#### 1.2. Kullanmak istediğin serviste config paketi altına RedisConfig sınıfı oluşturulur. Bu sınıfın ***@Configuration*** kullanarak konfigürasyon sınıfı olduğunu spring'e bildiririz.
#### 1.3. Aynı şekilde redisin cacheleme yapabileceğini söylemek içinde ***@EnableCaching*** ve ***@EnableRedisRepositories*** anatasyonları kullanılır.
#### 1.4. Lettuce metodunu kullanarak redisConfig sınıfın içinde Redisin cacheleme mekanizmasını kullanabiliriz.
#### 1.5. Yani dönüşü aynı olan bir sorgu varsa eğer bunu service katmanında ***@Cacheable*** ile cachleyip bu sorgununu cevabını önbelleğe aldırıp bir sonraki sorgularda çok daha hızlı cevap alabilirsin.
#### 1.6. Önbelleğe alınan sorgu değiştiyse, işin bittiyse temizleyebilirsin. Bunun içinde serivce katmanında yazdığın metoda ***@CacheEvict*** anatasyonunu eklersen bu metodu controllerda çalıştırıp temizleyince önbellek temizlenmiş oluyor.

###  Elasticsearch;
***
#### 1.1. Elasticsearch bir veritabanı diyebiliriz. Ama çalışma alanı daha çok bigdata'dır. Java diliyle yazılmıştır. Json formatında veri saklanır. In-memory bir uygulama olduğu için diğer veritabanlarına göre çok hızlıdır.
#### 1.2. ElasticSearch versiyonu ile springBoot versiyonu aynı olmak zorundadır. Buna çok dikkat etmeliyiz.
#### 1.3. Docker'a indirmeden önce bilmemiz gereken şey bir sınırlama vermeden çalıştırırsan tüm Ram sistemini kullanır. O yüzden docker indirirken indirme koduna bir sınır vermek zorundasın.
- Docker indirme;
```
 docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e ES_JAVA_OPTS="-Xms512m -Xmx2048m" -e "discovery.type=single-node" elasticsearch:7.17.9
```
#### 1.4. Projemize dahil etmek için [***maven repository***](https://mvnrepository.com/) den SpringBoot versiyonumuz (2.7.10) ile aynı elasticsearch gradle(short) kodunu alıyoruz.
- Dependencies.gradle;
```
 springBootDataElasticsearch : "org.springframework.boot:spring-boot-starter-data-elasticsearch:${versions.springBoot}",
```
- Elasticsearch build.gradle;
```
 implementation libs.springBootDataElasticsearch
```
#### 1.5. Son işlemde yapılandırma dosyasını yazmaktır. Resource altında application.yml dosyası kuruyoruz.
```
 server:
  port: 9099

spring:
  elasticsearch:
    uris: http://localhost:9200
```
#### 1.6. ElasticSearch diğer modüller gibi katmanlı mimariye sahiptir. Config,exception, utility vb diğer katmanlara sahiptir.
#### 1.7. Ama elasticSearch ile asıl amacımız entity classlarını buraya yönlendirip veritabanı sorgularına çok hızlı cevap vermek. Zaten elasticsarch bunun için kullanılır.
#### 1.8. Userservisinin entity sınıfını aynı şekilde elasticsearch içinde entity classında oluşturdum. Tek farkla userId diye belirttim çünkü elasticSearch yapısının kendi id'si olmalı.
#### 1.9. Aynı şekilde katmanlı mimaride repository, service controller oluşturdum.
#### 2.0. Yani bundan sonra userservisinde ekstra sorgu yazmayacağım. Elasticsearch modülünde yapıcam tüm sorgularımı böylece hem çok hızlı cevap dönücem hemde userservisine yük bindirmeyeceğim.

