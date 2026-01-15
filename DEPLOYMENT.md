# BookNook Backend - Deployment Guide

## 🚀 Deployment til Gratis Cloud Platforms

Denne guiden viser hvordan du deployer BookNook backend til gratis cloud-tjenester.

---

## 🛡️ VIKTIG: Beskyttelse mot Uventede Kostnader

### ✅ **Render.com - 100% TRYGT** (ANBEFALT)
- ✅ **Ingen kredittkort påkrevd** for gratis tier
- ✅ **Hard limit** - fysisk umulig å få regning
- ✅ **Automatisk stopp** ved gratis kvote brukt opp
- ✅ **Du må aktivt oppgradere** for å få kostnader
- ✅ **750 timer/måned** gratis (mer enn nok)
- ⚠️ **Verste scenario**: Appen stopper å fungere (ingen regning)

### ⚠️ **Railway.app - MODERAT RISIKO**
- ✅ $5 gratis kreditt/måned
- ⚠️ **Krever kredittkort** etter trial
- ⚠️ **Kan** generere kostnader hvis du går over gratis tier
- ✔️ **Løsning**: Sett spending limit til $5 i dashboard
- ✔️ **Email varsler** når du nærmer deg limit

### ⚠️ **Fly.io - MODERAT RISIKO**
- ✅ Gratis for 3 små VMs
- ⚠️ **Krever kredittkort**
- ⚠️ **Kan** fakturere hvis du går over gratis tier
- ✔️ **Løsning**: Sett opp billing alerts
- ✔️ **Hard limits** kan settes i config

### 🔴 **Heroku - IKKE ANBEFALT**
- ❌ Gratis tier fjernet (2022)
- ❌ Minimum $7/måned
- ❌ Ikke gratis lengre

### 🔐 **MongoDB Atlas - TRYGT**
- ✅ **Gratis M0 cluster** for alltid
- ✅ **Ingen kredittkort påkrevd**
- ✅ **Hard limits** (512 MB storage, 100 connections)
- ✅ **Automatisk pause** ved over-bruk
- ⚠️ **Verste scenario**: Database blir read-only (ingen regning)

---

## 💡 **ANBEFALING: Bruk Render.com**

For din use-case (hobby/læringsprosjekt) er **Render.com det tryggeste valget**:

1. **Ingen risiko for kostnader** - du trenger ikke engang kredittkort
2. **Appen sovner** etter 15 min inaktivitet (sparer ressurser)
3. **Cold starts** er greit for hobby-prosjekter (~30 sek)
4. **Ingen begrenset requests** - kan håndtere normal trafikk
5. **Auto-deploy fra GitHub** - kjekt for utvikling

### Hva med scripting/høy trafikk?

**Render gratis tier håndterer**:
- ✅ Normal API-bruk (tusenvis av requests/dag)
- ✅ Testing og utvikling
- ✅ Flere brukere samtidig

**Render vil IKKE koste penger selv om**:
- ❌ Du pinger API-et hvert sekund (vil bare bruke opp gratis timer)
- ❌ Du glemmer å skru av (ingen "av-knapp" - appen sovner selv)
- ❌ Høy trafikk (appen vil bare bli treg/stoppe midlertidig)

---

## ✅ Beste Alternativer (Gratis)

### 1️⃣ **Render.com** (ANBEFALT - TRYGGST)
- ✅ Gratis tier med 750 timer/måned
- ✅ Automatisk HTTPS
- ✅ Enkel setup med GitHub
- ⚠️ Sovner etter inaktivitet (cold start ~30 sek)

### 2️⃣ **Railway.app**
- ✅ $5 gratis kreditt/måned
- ✅ Raskere enn Render
- ⚠️ Begrenset gratis tier

### 3️⃣ **Fly.io**
- ✅ Gratis for 3 små VMs
- ✅ Bedre ytelse
- ⚠️ Mer komplisert setup

---

## 📦 Forhåndskrav

1. **Git repository** - Push prosjektet til GitHub
2. **MongoDB database** - Gratis på [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)

---

## 🎯 METODE 1: Render.com (Raskeste)

### Steg 1: Sett opp MongoDB Atlas (Gratis)

1. Gå til [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register)
2. Lag en gratis konto
3. Opprett et nytt cluster (M0 Sandbox - FREE)
4. Opprett en database bruker:
   - Database Access → Add New Database User
   - Brukernavn: `booknook`
   - Passord: Generer sterkt passord (lagre det!)
5. Whitelist IP:
   - Network Access → Add IP Address → Allow Access from Anywhere (0.0.0.0/0)
6. Hent connection string:
   - Clusters → Connect → Connect your application
   - Kopier connection string (f.eks: `mongodb+srv://booknook:<password>@cluster0.xxxxx.mongodb.net/booknook?retryWrites=true&w=majority`)
   - Erstatt `<password>` med ditt faktiske passord

### Steg 2: Push til GitHub

```bash
cd /Users/haakon.gunleiksrud/personal_projects/BookNook_backend

# Initialiser git (hvis ikke gjort)
git init
git add .
git commit -m "Initial commit - ready for deployment"

# Opprett repo på GitHub og push
git remote add origin https://github.com/DIN-BRUKER/booknook-backend.git
git branch -M main
git push -u origin main
```

### Steg 3: Deploy på Render

1. Gå til [Render.com](https://render.com) og logg inn med GitHub
2. Klikk **"New +"** → **"Web Service"**
3. Koble til ditt GitHub repository
4. Konfigurer:
   - **Name**: `booknook-backend`
   - **Runtime**: **Docker** (Velg dette siden Java ikke listes direkte)
   - **Branch**: `main`
   - **Root Directory**: La stå tom (eller `.`)
   - **Instance Type**: `Free`

5. Legg til Environment Variables:
   - Klikk "Advanced" → "Add Environment Variable"
   
   ```
   MONGODB_URI = mongodb+srv://booknook:<password>@cluster0.xxxxx.mongodb.net/booknook?retryWrites=true&w=majority
   JWT_SECRET = DIN-SUPER-HEMMELIGE-NØKKEL-HER-MINST-32-TEGN
   JWT_EXPIRATION = 86400000
   PORT = 9090
   ```
   
   **Merk**: Du trenger IKKE `JAVA_VERSION` når du bruker Docker.

6. Klikk **"Create Web Service"**
7. Render vil automatisk detecte `Dockerfile` og bygge med Docker
8. Vent 5-10 minutter mens Render bygger og deployer appen
9. Din app vil være tilgjengelig på: `https://booknook-backend.onrender.com`

**Alternativ**: Hvis du ikke vil bruke Docker, kan du velge "Static Site" eller "Web Service" uten å velge runtime, og Render vil prøve å auto-detecte Java fra `pom.xml`. Men Docker er mer pålitelig.

### Steg 4: Test deployment

```bash
# Test health endpoint
curl https://booknook-backend.onrender.com/actuator/health

# Eller besøk i nettleser
open https://booknook-backend.onrender.com/actuator/health
```

---

## 🎯 METODE 2: Railway.app

### Steg 1: Installer Railway CLI

```bash
# macOS
brew install railway

# Eller npm
npm install -g @railway/cli
```

### Steg 2: Login og deploy

```bash
cd /Users/haakon.gunleiksrud/personal_projects/BookNook_backend

# Login
railway login

# Initialiser prosjekt
railway init

# Legg til environment variables
railway variables set MONGODB_URI="mongodb+srv://..."
railway variables set JWT_SECRET="din-hemmelige-nøkkel"
railway variables set JAVA_VERSION="21"

# Deploy
railway up
```

---

## 🎯 METODE 3: Fly.io

### Steg 1: Installer Fly CLI

```bash
# macOS
brew install flyctl
```

### Steg 2: Login og deploy

```bash
cd /Users/haakon.gunleiksrud/personal_projects/BookNook_backend

# Login
flyctl auth login

# Launch app
flyctl launch

# Sett secrets
flyctl secrets set MONGODB_URI="mongodb+srv://..."
flyctl secrets set JWT_SECRET="din-hemmelige-nøkkel"

# Deploy
flyctl deploy
```

---

## 🐳 METODE 4: Docker (Eget hosting)

Hvis du har en egen server (VPS, Raspberry Pi, etc.):

```bash
# Build Docker image
docker build -t booknook-backend .

# Run container
docker run -d \
  -p 9090:9090 \
  -e MONGODB_URI="mongodb+srv://..." \
  -e JWT_SECRET="din-hemmelige-nøkkel" \
  --name booknook \
  booknook-backend
```

---

## 🔒 Sikkerhet

⚠️ **VIKTIG**: Ikke commit secrets til Git!

Legg til i `.gitignore`:
```
.env
application-prod.properties
```

---

## 📊 Monitorering

### Render Dashboard
- Gå til Render dashboard for å se:
  - Logs
  - Metrics
  - Restart service

### Health Check Endpoint
- `https://din-app.onrender.com/actuator/health`

---

## 🐛 Feilsøking

### Problem: App starter ikke

**Løsning**: Sjekk logs i Render dashboard
```bash
# Eller via CLI
render logs
```

### Problem: Database connection feil

**Løsning**: Sjekk at:
1. MongoDB URI er korrekt
2. IP whitelist inkluderer 0.0.0.0/0
3. Database bruker har riktige rettigheter

### Problem: Cold start tar lang tid

**Løsning**: 
- Dette er normalt for gratis tier
- Appen "sovner" etter 15 min inaktivitet
- Første request tar 30-60 sek

---

## 💡 Tips

1. **Auto-deploy**: Render deployer automatisk når du pusher til main branch
2. **Custom domain**: Legg til eget domene i Render dashboard
3. **Staging environment**: Opprett egen branch for testing
4. **Database backup**: MongoDB Atlas tar automatisk backups

---

## 📝 Neste Steg

1. ✅ Deploy backend
2. ✅ Test alle endpoints
3. ⬜ Deploy frontend
4. ⬜ Koble frontend til backend URL

---

## 🆘 Trenger hjelp?

- Render docs: https://render.com/docs
- MongoDB Atlas docs: https://docs.atlas.mongodb.com/
- Spring Boot docs: https://spring.io/guides

**Lykke til med deployment! 🚀**
