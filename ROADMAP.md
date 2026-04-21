# Roadmap — app-azure-cliente en Azure

## Completado
- [x] Java 21 + Maven 3.9.15
- [x] Spring Boot 3 + WebFlux + R2DBC + H2
- [x] Docker image — `app-azure-cliente:1.0.0`
- [x] ACR — `acrappcliente.azurecr.io`
- [x] AKS — `aks-app-cliente` (West US, Plan Gratis)
- [x] Deploy en Kubernetes (1 pod, 2 nodos)
- [x] LoadBalancer IP pública: `20.245.152.31`
- [x] APIs respondiendo desde internet

---

## Fase 2 — Seguridad y Configuración (HOY)

### Paso 1 — Key Vault
- [ ] Crear `kv-app-cliente` en Azure
- [ ] Guardar secretos:
  - `APIM-SUBSCRIPTION-KEY`
  - `DB-PASSWORD` (para cuando migremos a PostgreSQL)
- [ ] Configurar Managed Identity en AKS para leer Key Vault
- [ ] Inyectar secretos como variables de entorno en los pods

### Paso 2 — Azure App Configuration
- [ ] Crear `appconfig-app-cliente`
- [ ] Guardar configuraciones por entorno:
  - `app.ambiente` = dev | staging | prod
  - `app.clientes.max-page-size` = 50
  - `guinea.apim.url` = https://guinea.azure-api.net
- [ ] Conectar Spring Boot con App Configuration via SDK

### Paso 3 — APIM (Consumption)
- [ ] Crear `apim-app-cliente` tier Consumption (gratis 1M calls/mes)
- [ ] Importar API desde OpenAPI o manualmente
- [ ] Configurar políticas inbound:
  - `validate-jwt` — validar token
  - `check-header` — validar Ocp-Apim-Subscription-Key
  - `ip-filter` — whitelist de IPs
  - `rate-limit` — throttling por suscripción
- [ ] Backend apunta a IP pública del AKS: `http://20.245.152.31`
- [ ] Probar APIs via APIM gateway URL

---

## Fase 3 — CI/CD con Azure DevOps

### Pipeline completo
- [ ] Crear organización Azure DevOps
- [ ] Crear proyecto `app-azure-cliente`
- [ ] Pipeline `azure-pipelines.yml`:
  1. Build Maven → JAR
  2. Docker build → push ACR
  3. kubectl apply → AKS
- [ ] Trigger automático en push a `main`

---

## Fase 4 — Base de datos real

- [ ] Migrar H2 → Azure PostgreSQL Flexible Server
- [ ] R2DBC PostgreSQL driver
- [ ] Flyway migrations
- [ ] Connection string en Key Vault

---

## Arquitectura final

```
Internet
    │
    ▼
APIM (Consumption)
├── validate-jwt
├── ip-filter
├── rate-limit
└── subscription-key
    │
    ▼
AKS — app-azure-cliente (pod)
├── Lee config de App Configuration
├── Lee secretos de Key Vault (Managed Identity)
└── H2 en memoria → PostgreSQL (Fase 4)
    │
    ▼
ACR — acrappcliente.azurecr.io
```
