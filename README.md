# MyTrainer

> **Aplikacija za trenere i korisnike teretane**

`MyTrainer` je full-stack aplikacija koja omogućava korisnicima da rezervišu i otkažu termine za treninge, a trenerima da upravljaju terminima kroz REST API i web interfejs.

---

## Sadržaj

1. [Funkcionalnosti](#funkcionalnosti)
2. [Tehnologije](#tehnologije)
3. [Struktura projekta](#struktura-projekta)
4. [Instalacija i pokretanje](#instalacija-i-pokretanje)
5. [Docker Compose](#docker-compose)
6. [Korišćenje](#korišćenje)
7. [Contact](#contact)

---

## Funkcionalnosti

* **Rezervacija**: Korisnik može da rezerviše termin (30 ili 60 minuta).
* **Otkazivanje**: Korisnik može da otkaže rezervaciju najkasnije 24h pre termina.
* **Pregled termina**: Trener ima dnevni i nedeljni kalendar svih zakazanih termina sa podacima o korisnicima.
* **Upravljanje terminima**: Trener može da zakazuje i otkazuje termine u ime korisnika.
* **Autentifikacija**: Trener, kako bi otkazao neki termin, to obavlja putem jedinstvenog koda.

## Tehnologije

* **Backend**: Java 17, Spring Boot, Spring Data JPA, JWT, Mail
* **Frontend**: React, React Router, Context API
* **Baza**: PostgreSQL
* **Docker**: Docker, Docker Compose

## Struktura projekta

```
MyTrainer/
├── backend/        # Spring Boot API
│   ├── src/main/java/com/mytrainer/backend
│   │   ├── model/      # JPA entiteti
│   │   ├── repository/ # Spring Data JPA
│   │   ├── services/   # Biznis logika
│   │   ├── controllers/# REST API
│   │   ├── dto
│   │   ├── scheduling
│   │   ├── config
│   │   └── security/   # JWT, CORS, SecurityConfig
│   ├── Dockerfile
│   ├── pom.xml
│   └── .dockerignore
├── frontend/       # React aplikacija
│   ├── src/
│   ├── public/
│   ├── Dockerfile
│   └── package.json
└── docker-compose.yml  # baza, mailhog, backend, frontend
```

## Instalacija i pokretanje

### Lokalno bez Docker-a

1. **Backend**:

   ```bash
   cd backend
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
2. **Frontend**:

   ```bash
   cd frontend
   npm install
   npm start
   ```

### Portovi

* Backend: `http://localhost:8080`
* Frontend: `http://localhost:3000`

## Docker Compose

U korenu projekta:

```bash
docker compose up -d
```

Komponente:

* **postgres** (5432)
* **mailhog** (1025 SMTP, 8025 UI)
* **backend** (8080)
* **frontend** (80)

Zaustavljanje:

```bash
docker compose down
```

## Korišćenje

1. Otvorite frontend (`http://localhost`) ili `http://localhost:3000`.
2. Rezervišite termin preko korisničkog UI.
3. Trener se prijavljuje kodom na `/trainer/login`.
4. U dashboard-u može da vidi, zakazuje i otkazuje termine.

   
## Contact
- **Ime i prezime**: Vukasin Patkovic  
- **Email**: [vukasinpatkovic@gmail.com](mailto:vukasinpatkovic@gmail.com)  
- **LinkedIn**: [linkedin.com/in/vukasin-patkovic-7209a7355](https://www.linkedin.com/in/vukasin-patkovic-7209a7355/)  
