# Aplikacija za Igranje Kviza

Aplikacija za Igranje Kviza je web-platforma koja omogućava korisnicima da kreiraju i igraju kvizove u realnom vremenu. Pruža korisnički interfejs za kreiranje kvizova kreatorima kvizova i učešće igračima u kvizovima.

## Sadržaj

- [Funkcionalnosti](#funkcionalnosti)
- [Korištene Tehnologije](#korištene-tehnologije)
- [Instalacija](#instalacija)
- [Korištenje](#korištenje)
- [Nedovršene Funkcionalnosti](#nedovršene-funkcionalnosti)
- [Licenca](#licenca)

## Funkcionalnosti

- Kviz kreatori mogu dizajnirati kvizove i upravljati pitanjima i odgovorima.
- Igrači mogu se pridružiti aktivnim kvizovima koristeći jedinstveni PIN.
- Komunikacija u realnom vremenu između kviz admina i igrača putem websocket tehnologije.
- Tajmer za svako pitanje s vizuelnim odbrojavanjem.
- Praćenje rezultata i prikaz rang liste.
- Čuvanje rezultata kviza kao XLS datoteke.

## Korištene Tehnologije

- Backend: Java, Jetty Server, JAX-RS, JPA (Java Persistence API)
- Frontend: HTML, CSS, JavaScript
- Websockets za komunikaciju u realnom vremenu
- Baza podataka: MySQL
- Build i Dependency Menadžment: Maven
- Ostale biblioteke i frameworkovi: Hibernate, Jersey, jQuery, Bootstrap

## Instalacija

1. Klonirajte repozitorij: `git clone https://github.com/blasted-tiara/quiz-app`
2. Postavite bazu podataka i konfigurišite detalje konekcije u `src/main/resources/hibernate.cfg.xml`.
3. Izgradite projekat koristeći Maven: `mvn clean install`
4. Popunite bazu podataka pokrećući `main` funkciju klase `src/main/java/ba/fet/rwa/scripts/UserDataLoader.java`.
4. Pokrenite Jetty server: `mvn jetty:run`
5. Pristupite aplikaciji u vašem web pregledaču na adresi `http://localhost:8080`. (Admin stranica: `http://localhost:8080/admin`, Igrač stranica: `http://localhost:8080/player/index.html`)

## Korištenje

1. Kreirajte Kviz:
    - Prijavite se kao kviz admin.
    - Dizajnirajte i konfigurišite kviz dodavanjem pitanja i odgovora.
    - Generišite jedinstveni PIN koji ćete podijeliti sa igračima.

2. Igrajte Kviz:
    - Igrači pristupaju stranici kviza i unose dobiveni PIN.
    - Sačekajte da kviz admin pokrene kviz.
    - Odgovorite na pitanja unutar zadatog vremenskog limita.
    - Pogledajte rezultate i rang listu nakon svakog pitanja.
    - Konačni rezultati se prikazuju na kraju kviza.

## Nedovršene Funkcionalnosti

Sljedeće funkcionalnosti nisu implementirane u trenutnoj verziji aplikacije:

- Mogućnost mijenjanja redoslijeda pitanja
- Postojanje različitih rola za admina i moderatora

## Bugovi

Sljedeći bugovi su prisutni u trenutnoj verziji aplikacije:
   - Username administrator/moderator računa nije jedinstven
   - Prilikom brisanja kviza, ili prilikom promjene slike, uploadana slika se ne brise sa servera