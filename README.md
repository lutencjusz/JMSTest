# JMSTest
System utworzony do przetestwoania mozliwości silnika kolejek JMS Appache-activeMQ 5.15.2 z mozliwością konfiguracji ilosci wątków i obciązenia servera. System składa się z dwóch aplikacji:
- SOAModelESB - w modelu SOA pełni rolę szyny ESB, która przetwarza dane między kolejkami
- SOAJMSTestClient - klient graficzny (ciężki) z możliwością konfiguracji obciążenia zarówno pod względem ciężkości zapytań SQL jak i ilosci wątków, równoległości i długości ich pracy. Całość testu jest pokazywana w formie krótkiego raportu. 
Całość jest dostosowana do pokazywania w eclipse.

Katalog SOAJAR zawiera skompilowane do postaci JAR'ów. Pliki do uruchomienia w środowisku lokalnym Java (Windows) za pomocą SOA.bat.

# Instalacja JMSTest
W celu uruchomienia rozwiązania wystarczy:
1. Zainstalować i uruchomić server JMS Appache-activeMQ (5.15.2 lub nowsza)
2. utworzyć katalog SOAJAR i przekopiwać do niego pliki z katalogu SOAJAR na repozytorium, tworząc następującą strukturę:
.\SOAJAR\SOAModelESB.jar
.\SOAJAR\SOAJMSTestClient.jar
.\SOA.bat - plik uruchomieniowy (w systemach Linux należy na bazie SOA.bat utworzyć plik uruchomieniowy np. SOA.sh)
3. uruchomić batch (w środowiskach Windows SOA.bat)
