# MeteoPi
![meteopi](img/materials/logo.png?raw=true "MeteoPi")<br />
MeteoPi jest niskobudżetowym systemem pomiaru temperatury, wilgotności powietrza, oraz prędkości i kierunku wiatru opartym na popularnych minikomputerach Raspberry Pi 3 i języka Java.

## Technologia
  - [Raspberry Pi 3](https://botland.com.pl/pl/moduly-i-zestawy-raspberry-pi-3/5576-raspberry-pi-3-model-b-wifi-bluetooth-1gb-ram-12ghz-640522710850.html)
  - [Putty](https://www.putty.org/)
  - [Etcher.io](https://www.balena.io/etcher/)
  - [Total Commander](http://totalcmd.pl/)
  - [Raspbian](https://www.raspberrypi.org/downloads/raspbian/)
  - [PostgreSQL](https://www.postgresql.org/)
  - [JDBC](https://www.oracle.com/technetwork/java/javase/jdbc/index.html)
  - [JavaFX](https://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html)
  - [SceneBuilder](https://gluonhq.com/products/scene-builder/)
  - [PgAdmin 4](https://www.pgadmin.org/)
  - [Pi4J](http://pi4j.com/)
  - [clever-cloud](https://console.clever-cloud.com/)
## Elektronika
  - [Ultradźwiękowy czujnik odległości HC-SR04](https://botland.com.pl/pl/ultradzwiekowe-czujniki-odleglosci/1420-ultradzwiekowy-czujnik-odleglosci-hc-sr04-2-200cm.html) x 2szt
  - [Czujnik temperatury i wilgotności DHT22 (AM2023)](https://botland.com.pl/pl/czujniki-temperatury/2637-czujnik-temperatury-i-wilgotnosci-dht22-am2023-modul-przewody.html?search_query=dht22&results=9)
  - ~~Barometr cyfrowy MPL115A2~~
  - [Karta pamięci SANDISK ULTRA microSDHC 16 GB](https://allegro.pl/oferta/sandisk-ultra-microsdhc-16-gb-class-10-adapter-hi-7440973242)
## Pozostałe elementy
  - [Kolanko NYPLOWE 20 mm KSZTAŁTKI PPR](https://www.leroymerlin.pl/hydraulika/instalacje-wodne-i-gazowe/instalacje-polipropylenowe-zgrzewane/kolanko-nyplowe-20-mm-ksztaltki-ppr,p12447,l548.html)
  - [Płyta HIPS czarna 3mm](https://sklep.akces-plexi.pl/plyty/578-plyta-hips-biala-1mm-100x200cm.html)
  - [Statyw mikrofonowy On Stage Stands MS-7701B](https://riff.net.pl/stojaki-i-statywy/25235-on-stage-stands-ms-7701b.html) (pozostawiony jedynie trzon)

# Urządzenie pomiarowe
### Konfiguracja systemu
Instalujemy powyżej zamieszone programy: Putty (do zdalnego połączenia z urządzeniem Raspberry Pi poprzez SSH), Etcher.io (do wgrania obrazu systemu dla urządzenia na kartę SD) oraz pobieramy [obraz](https://www.raspberrypi.org/downloads/raspbian/) w wersji systemu `Raspbian Stretch with desktop` z oficjalnej strony Raspberry Pi oraz wgraniu obrazu na kartę SD z wykorzystaniem programu Etcher.io umieszczamy kartę wewnątrz urządzenia Raspberry Pi. Po podłączeniu internetu, monitora, klawiatury oraz zasilania uruchamiamy konsolę z wykorzystaniem klawiszy Ctrl+Alt+T i wpisujemy poniższą komendę:
```sh
sudo apt-get update && sudo apt-get upgrade
```
Spowoduje to pobranie oraz instalację aktualizacji dla naszego systemu. Następnie wpisujemy komendę:
```sh
sudo raspi-config
```
Wyświetli to okno `Raspberry Pi Software Configuration Tool`, który pozwoli na uruchomienie SSH dla zdalnego konsolowego połączenia z naszym urządzeniem. Wybieramy opcję `Interfacing Options` > `SSH` i zatwierdzamy nasze zmiany. Po uruchomieniu Putty w polu `Host Name (or IP adress)` wpisujemy adres naszego urządzenia, w tym przypadku jest to `192.168.1.105`<br />
![putty_desktop](img/materials/putty.png?raw=true "Putty")<br />
Po zatwierdzeniu loginu oraz hasła wpisujemy komendę:
```sh
sudo nano start.sh
sudo chmod +x start.sh
sudo chmod 775 start.sh
```
Przepisujemy poniższy:
```sh
while true; do
  if [ ! $(pgrep -f meteomobi.jar)]; then
    java -jar meteomobi.jar &
  fi
  sleep 5
done
```
Utworzy to nam plik o nazwie `start.sh` chmod powoduje, że będzie mógł być potem uruchomiony przy restarcie systemu. Kod wewnątrz pliku będzie miał za zadanie uruchamiać program w przypadku, kiedy wystąpi nieprzewidziany wyjątek, zostanie omyłkowo wyłączony, lub wystąpi nieoczekiwana przerwa w działaniu programu. Dodajemy plik do autostartu:
```sh
sudo nano .config/lxsession/LXDE-pi/autostart
```
na końcu dopisujemy linijkę:
```sh
@/home/pi/start.sh
```
Po restarcie system będzie próbował uruchomić plik `meteomobi.jar`. Po zakończeniu wszystkiego wpisujemy komendę
```sh
sudo reboot
```
### Elektronika
Podpinamy wszystkie elementy do Raspberry Pi według poniższego schematu `GPIO` zostały podane według standardu przyjętego przez bibliotekę [Pi4J](http://pi4j.com/pins/model-b-plus.html):
![connect_scheme](img/materials/connect_scheme.png?raw=true "Connection")

| Czujnik | Wyjście | GPIO | Pin Num |
| ------ | ------ | ------ | ------ |
| HR-SC04 N/S | VCC | 5.0 VDC | 4
| HR-SC04 N/S | GND | Ground | 6 |
| HR-SC04 N/S | TRIGGER N | GPIO 25 | 37 |
| HR-SC04 N/S | ECHO S | GPIO 24 | 35 |
| HR-SC04 W/E | VCC | 5.0 VDC | 2 |
| HR-SC04 W/E | GND | Ground | 14 |
| HR-SC04 W/E | TRIGGER E | GPIO 23 | 33 |
| HR-SC04 W/E | ECHO W | GPIO 22 | 31 |
| DHT22 | + | 3.3V VDC | 17 |
| DHT22 | - | Ground | 25 |
| DHT22 | out | GPIO 12 | 19 |

Przelutowujemy czujniki HR-SC04 z przewodami połączeniowymi tak, aby piny czujników były połaczone wraz z płytką, będziemy chcieli umieszczać czujnik z triggera na przeciwko czujnika echo.

### Obudowa
Wycinamy obudowę z hipsu, kolorami zaznaczone są linie frezu wykonanymi frezem 3mm. 
![connect_scheme](img/materials/cutting.png?raw=true "Cięcie hipsu")<br />

Efekt końcowy:
![stacja_meteorologiczna1](img/materials/andon1.png?raw=true "MeteoPi")<br />
![stacja_meteorologiczna1](img/materials/andon2.png?raw=true "MeteoPi")<br />

# PostgreSQL
Baza PostgreSQL została postawiona na domenie [clever-cloud](https://console.clever-cloud.com/) z automatycznym generowaniem hostu dostępu, nazwy użytkownika, nazwą bazy oraz hasłem dostępu do bazy. Wewnątrz bazy o wygenerowanej automatycznie nazwie `b8lff3k8prhevtsdimsi`z pomocą PgAdmin 4 tworzymy tabele:
  - `andons` w której znajdować się będą urządzeń, oraz ich lokalizacje
  - `measures` gdzie znajdują się dane pomiarowe.

![table1_create](img/postgresql/crt_tab_andons.png?raw=true "tabela andons")
![table2_create](img/postgresql/crt_tab_measures.png?raw=true "tabela measures")<br />
kolumna `measures.andon_id` jest kluczem obcym w związku z tym zostaje utworzona relacja z tabelą `andons`.
![table2_create](img/postgresql/crt_foreign_key.png?raw=true "foreign key")<br />
Dodajemy nasze urządzenie wraz z lokalizacją:
```sql
INSERT INTO andons values(1, 'MMRZ01', 50.100711, 22.035988)
```

# Aplikacja na urządzenie Raspberry Pi 3
### Tworzenie klas
Aplikacja wykorzystuje bibliotekę Pi4J do obsługi elektroniki znajdującej się w urządzeniu oraz JDBC do przesyłania danych do bazy. Tworzymy `Java Application` o nazwie `MeteoMobi`, wewnątrz którego umieszczamy pakiet `io.raspi.meteo`. Tworzymy 3 klasy: 
  - `MainClass` - klasa główna wraz z metodą startową oraz metodami do analizy danych
  - `DHT22` - klasa w której znajdują się metody do obsługi czujnika DHT22*
  - `Connector` - klasa posiadająca metodę do połączenia z bazą danych PostgreSQL

Wewnątrz `MainClass` tworzymy instancję dla `GpioFactory` oraz ustawiamy stan niski dla wyjść cyfrowych
```java
private final GpioController gpio = GpioFactory.getInstance();
private final GpioPinDigitalOutput trigger1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
private final GpioPinDigitalOutput trigger2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW);
```
Tworzymy kolekcje dla pomiarów temperatury, wilgotności powietrza, prędkości wiatru względem czujników północ-południe, prędkość wiatru względem czujników wschód-zachód. Pozwoli to późniejsze na wyciąganie mediany danych pomiarów. Powodem dla którego nie została wykorzystana średnia jest fakt wystąpienia dużej rozbieżności danych.

### Pomiar prędkości i kierunku wiatru
Pomiar odbywa się poprzez wykorzystanie wzoru:
![equation](http://latex.codecogs.com/gif.latex?V%20%3D%20%5Cfrac%7BL%7D%7B2cos%5Calpha%20%7D*%5Cfrac%7BT1-T2%7D%7BT1*T2%7D)
gdzie 
  - ![equation](http://latex.codecogs.com/gif.latex?cos%5Calpha) - kąt pomiędzy czujnikami a kierunkiem w którym porusza się wiatr
  - L - odległość w metrach pomiędzy czujnikami (w naszym przypadku to 0.115m)
  - T1 - czas w jakim sygnał przechodzi pomiędzy triggerem a czujnikiem echo w sytuacji, kiedy nie ma przepływu wiatru
  - T2 - czas zmierzony od wysłania impulsu z Triggera trwającego 10us do drugiego czujnika Echo
 
Ponieważ kąt pomiędzy czujnikami a mierzonym przepływem wynosi ![equation](http://latex.codecogs.com/gif.latex?0%5E%7B%5Ccirc%7D) przyjmujemy, że wartość ![equation](http://latex.codecogs.com/gif.latex?cos%5Calpha) wynosi 1.
Pomiar rozpoczyna się poprzez ustawieniem na 10us stanu wysokiego na triggerze. Następnie odczytujemy i mierzymy czas w jakim czujnik echo pozostaje w stanie wysokim.
```java
trigger.high();
Thread.sleep(0, 10000);
trigger.low();
while (Gpio.digitalRead(pins) == 0) {
	StartTime = System.nanoTime();
}
while (Gpio.digitalRead(pins) == 1) {
	StopTime = System.nanoTime();
}
double diff = (StopTime-StartTime)/Math.pow(10,  9);
```
Czas Zależny jest od urządzenia, w przypadku urządzenia o symbolu "MMRZ01" wynosi odpowiednio: 0.000366s dla czujnika NS(północ-południe) oraz 000372s dla czujnika EW(wschód-zachód), podczas gdy dla "MMRZ02" te wartości wynoszą odpowiednio: 0.000321s dla NS oraz 0.000345s dla EW. Jeżeli dla NS różnica czasu zmierzonego w stosunku do czasu odniesienia przyjmuje wartość dodatnią wtedy przyjmujemy, że wiatr jest z kierunku północnego, jeżeli ujemny przyjmujemy, że wiatr wieje z południa. Analogicznie odbywa się to w przypadku czujnika EW gdzie jeżeli różnica jest dodatnia, wtedy wiatr wieje ze wschodu jeżeli ujemna z zachodu.
Aby ustalić kierunek wiatru przyjmujemy w osi XY przyjmujemy, że stosunek jeżeli wartości czujników ultradźwiękowych NS oraz EW jest większy od 2 wtedy wiatr może być południowy, bądź północny. Jeżeli jednak stosunek EW/NS jest większy niż 2 wtedy mamy do czynienia z wiatrem wiejącym albo na wschód, albo na zachód. W pozostałym przypadku wiatr może być południowo-wschodni, południowo-zachodni, północno-wschodni, północno-zachodni. Prędkość wyznaczamy przy wykorzystaniu wzoru na wyznaczanie długości przeciwprostokątnej trójkąta. ![equation](http://latex.codecogs.com/gif.latex?V%20%3D%20%5Csqrt%7BV1%5E%7B2%7D&plus;V2%5E%7B2%7D%7D) (ponieważ pary czujników są ustawione wobec siebie pod kątem ![equation](http://latex.codecogs.com/gif.latex?45%5E%7B%5Ccirc%7D))

![wind_circle](https://www.researchgate.net/profile/Joost_Pluijms/publication/274387825/figure/fig2/AS:294655649435658@1447262789681/Top-view-of-the-wind-simulator-including-the-16-nominal-wind-directions-steps-of-225.png "Wind directions")<br />

### Pomiar temperatury i wilgotności
Niestety przy odczytywaniu temperatury i wilgotności z czujnika `DHT22` pojawił się problem związany z brakiem rozwiązań dla języka Java związanych z odczytem danych z czujnika. Klasa `DHT22` zawiera jednak rozwiązanie wyjętej ze wspieranego dla Arduino, języków C, C++ oraz Python biblioteki [Adafruit](https://github.com/adafruit/Adafruit_Python_DHT). Odczyt danych odbywa się poprzez wykorzystanie modułu [pi_2_dht_read.c](https://github.com/adafruit/Adafruit_Python_DHT/blob/master/source/Raspberry_Pi_2/pi_2_dht_read.c), który przetwarza otrzymywany przez czujnik sygnał. W Javie aby otrzymać informacje o temperaturze i wilgotności należy ustawić pin GPIO 12 na stan wysoki na 0,5s następnie ustawić stan tego pinu na niski na 20ms.Po upłynięciu tego czasu należy ustawić pin jako input do odczytu danych. 
```java
Gpio.pinMode(pin, Gpio.OUTPUT);
Gpio.digitalWrite(pin, Gpio.HIGH);
Gpio.delay(500);
Gpio.digitalWrite(pin, Gpio.LOW);
Gpio.delay(20);
Gpio.pinMode(pin, Gpio.INPUT);
```
Wewnątrz modułu `pi_2_dht_read.c` znajduje się krótka pętla for poprzedzająca rozpoczęcie odczytu, jest ona również dodana do części programu
```c
// Set pin at input.
pi_2_mmio_set_input(pin);
// Need a very short delay before reading pins or else value is sometimes still low.
for (volatile int i = 0; i < 50; ++i) {
}
```
Po zdekodowaniu wartość temperatury oraz wilgotności dodawany jest do dwóch kolekcji z których wyciągnięte zostają mediany.

### JDBC - wysyłanie wyników
Klasa `Connector` zawiera konstruktor oraz gettery które uniemożliwiają wprowadzenie niewłaściwych danych do bazy. Znajduje się także przypisany dla każdego z urządzeń unikatowy symbol. Wywołanie connectora odbywa się w klasie `MainClass`, gdzie w ramach zabezpieczenia przed niepowodzeniem zapytania wykorzystana została obsługa wyjątków try catch. Zapytanie jest cykliczne i odbywa się co 15s.
```java
try {
	Connector conn = new Connector((float)windspeed, direct, temp, hum);
	conn.connector();
}
catch (Exception e) {
	e.printStackTrace();
}
```
Wysłanie danych odbywa się poprzez załadowanie Driver'a do bazy PostgreSQL `org.postgresql.Driver`.
```java
Class.forName("org.postgresql.Driver");
```
tworzone są 3 zmienne typu String 
  - `url` - zawiera adres hosta oraz nazwę bazę danych
  - `user` - zawiera nazwę użytkownika do bazy danych
  - `password` - zawiera hasło do bazy

tworzymy połączenie z bazą danych oraz wykorzystujemy przeznaczonej do przygotowania zapytania typu INSERT przyjmującą jako parametry symbol urządzenia, temperaturę, wilgotność, prędkość wiatru oraz kierunek wiatru
```java
Connection conn = DriverManager.getConnection(url, user, password);
String sql = "INSERT INTO measures(andon_id, temp, humidity, wind_speed, wind_direct, evt_time) VALUES ((SELECT id FROM andons WHERE symbol=?), ?, ?, ?, ?, NOW())";
PreparedStatement prpstmt = conn.prepareStatement(sql);
prpstmt.setString(1,  this.symbol);
prpstmt.setDouble(2,  getTemperature());
prpstmt.setDouble(3,  getHumidity());
prpstmt.setDouble(4,  getWind());
prpstmt.setString(5,  getDirection());
```
po przygotowaniu zapytania wykonujemy wpis do bazy
```java
prpstmt.executeUpdate();
```
po pomyślnym dokonaniu wpisu do bazy zamykamy połączenie
```java
conn.close();
```
Po skompilowaniu całości do pliku `meteomobi.jar`. Następnie przesyłamy go z wykorzystaniem programu Total Commander na urządzenie Raspberry Pi do lokalizacji `/home/pi/` i restartujemy urządzenie. Dodawanie danych do bazy rozpocznie się niedługo po uruchomieniu urządzenia.

# JavaFX
### Wygląd interfejsu
Projekt posiada aplikację desktopową, dzięki której można przeglądać zebrane w bazie dane z wybranej lokalizacji.
![temperature](img/javafx/temperatura.png?raw=true "Temperatura")<br /><br />
![humidity](img/javafx/wilgotnosc.png?raw=true "Wilgotność")<br /><br />
![wind_speed](img/javafx/predkosc_wiatru.png?raw=true "Prędkość wiatru")<br /><br />
Tworzymy Projekt o nazwie MeteoApp, w której umieszczamy klasę MainClass inicjalizującą projekt. Aby odświeżać dane stworzymy w tym celu klasę Connector, która podobnie jak w przypadku Raspberry Pi służy za klasę umożliwiającą łączenie się z bazą. Dzięki niemu będziemy pobierali dane do analizy. Aby uzyskać interesujące nas dane wykorzystamy w tym celu poniższe zapytanie:
```sql
SELECT AVG(temp) as avg, evt_time::date + (EXTRACT(hour FROM evt_time)::int) * '1h'::interval + (EXTRACT(minute FROM evt_time)::int) * '5m'::interval as time FROM measures WHERE andon_id=(SELECT id FROM andons WHERE symbol="MMRZ01") AND evt_time<NOW()-INTERVAL '0 hours' AND evt_time>NOW()-INTERVAL '6' HOUR GROUP BY 2 ORDER BY 2
```
Dzięki temu zapytaniu otrzymamy średnie wartości temperatur w wybranym przedziale czasowym. `SELECY AVG(temp) as avg` wyświetla nam średnią temperaturę z grupy wyników,  `evt_time::date + (EXTRACT(hour FROM evt_time)::int) * '1h'::interval + (EXTRACT(minute FROM evt_time)::int) * '1m'::interval as time`. Wyświetla wyniki w przedziałach czasowych interwał czasowy pomiędzy wierszami wynosi 1 minutę. `WHERE andon_id=(SELECT id FROM andons WHERE symbol="MMRZ01") AND evt_time<NOW()-INTERVAL '0 hours' AND evt_time>NOW()-INTERVAL '6'` wyświetla dane zebrane przez urządzenie o symbolu `MMRZ01` z przedziału czasowego od `NOW()` do wyników otrzymanych najpóźniej 6 godzin temu. `GROUP BY 2` grupuje wyniki w stosunku do kolumny drugiej czyli przedziałów czasowych,  `ORDER BY 2` sortuje wyniki rosnąco. Zmieniając wartości `temp` na inną nazwę kolumny otrzymamy wyniki pomiarów wilgotności oraz prędkości wiatru. Zmieniając symbol `MMRZ01` na inny otrzymamy wyniki z innego urządzenia. zmieniając interwały `INTERVAL '0 hours'` i `INTERVAL '6 hours'` na przykładowo `INTERVAL '5 hours'` oraz `INTERVAL '12 hours'` otrzymamy wyniki od 5 do 12 godzin wstecz. dzięki tej jednej strukturze zapytania możemy aktualizować wykres typu AreaCharts. Aby zaktualizować AreaCharts o dane typu double oraz datę potrzebne będzie utworzenie nowej klasy DateDoubleFormat, która umożliwia zapisywanie tego typu danych w ArrayList. 
Aktualizacja pozycji tekstowych odbywa się poprzez wywołanie innego zapytania
```sql
SELECT temp, humidity, wind_speed, wind_direct, location, longtitude, latitude FROM measures, andons WHERE measures.andon_id=(SELECT id FROM andons WHERE symbol=?) AND andons.symbol=? ORDER BY measures.id DESC LIMIT 1"
```
gdzie w miejscach pytajników umieszczamy symbol interesującego nas urządzenia.
W pozycji Lokalizacja znajdują się 2 urządzenia, wybór odpowiedniego spowoduje, automatyczne pobranie najświeższych zarejestrowanych danych przez urządzenie. Informacja o ostatniej aktualizacji znajduje się w prawym dolnym rogu panelu, gdzie zapisana jest data i godzina ostatniej aktualizacji.
Przycisk `Odśwież` służy do pobrania najnowszych danych oraz aktualizację tabeli. Przyciski `<<` oraz `>>` służą do poruszania się do przodu i do tyłu względem osi czasu. Przycisk `reset` służy do pobrania z powrotem najnowszych danych.

# Dodatkowe linki
[MeteoPi - odczyty danych pogodowych](http://meteopi.vot.pl/) - we współpracy z [@Fyrrj](https://github.com/Fyrrj)









