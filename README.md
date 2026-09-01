# Bokföringsanalys

Webbapp som läser in svensk bokföring i SIE4-format och gör den analyserbar — kontoplan, verifikat och nyckeltal i ett gränssnitt istället för i ett bokföringsprogram.
![Gränssnitt](docs/skarmdump.png)


## Varför SIE

SIE är den svenska de facto-standarden för att flytta redovisningsdata mellan system. Alla större bokföringsprogram — Fortnox, Visma, Bokio — exporterar det, och Skatteverket accepterar det. Det gör formatet till en bra ingång för verktyg som ska fungera oavsett vilket system företaget använder.

Formatet är taggbaserat och teckenkodat i CP437 (`#FORMAT PC8`), med citerade och ociterade fält om vartannat och dimensioner i måsvingar. Parsern hanterar alla varianterna.

## Arkitektur

| Del | Teknik | Ansvar |
|-----|--------|--------|
| `api/` | Java 17, Spring Boot | Parsar SIE4, exponerar REST-API |
| `analys/` | Python, FastAPI | Nyckeltal och analys |
| `webb/` | React, TypeScript, Vite | Gränssnitt |

## Köra lokalt

Backend:
cd api
./mvnw spring-boot:run

Frontend:
cd webb
npm install
npm run dev


Öppna http://localhost:5173 och ladda upp en SIE4-fil. En exempelfil finns i `api/src/test/resources/`.

## Tester
cd api
./mvnw test


Testsviten validerar bland annat att varje inläst verifikat balanserar till noll — en regel i dubbel bokföring, och därmed ett test av att parsern inte tappar rader.

## Status

Under utveckling. Parsning, REST-API och verifikatlista fungerar. Nästa steg: saldoberäkning och Python-analystjänsten.
