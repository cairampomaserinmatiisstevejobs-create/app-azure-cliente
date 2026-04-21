# Azure Configuration — app-azure-cliente

## Recursos Azure

### Resource Group
| Campo | Valor |
|---|---|
| Nombre | `rg-app-azure-cliente` |
| Región | West US 2 |
| Suscripción | Azure subscription 1 |
| Subscription ID | `60aa0ad8-fd7e-4219-b0e5-c3e45798d4b8` |

---

### Azure Container Registry (ACR)
| Campo | Valor |
|---|---|
| Nombre | `acrappcliente` |
| Login Server | `acrappcliente.azurecr.io` |
| Plan | Basic |
| Imagen publicada | `acrappcliente.azurecr.io/app-azure-cliente:1.0.0` |

```bash
# Login
az acr login --name acrappcliente

# Push imagen
docker tag app-azure-cliente:1.0.0 acrappcliente.azurecr.io/app-azure-cliente:1.0.0
docker push acrappcliente.azurecr.io/app-azure-cliente:1.0.0
```

---

### Azure Kubernetes Service (AKS)
| Campo | Valor |
|---|---|
| Nombre | `aks-app-cliente` |
| Región | West US |
| Nodos | 2 nodos (D2as_v6) |
| Plan | Gratuito |
| IP Pública | `20.245.152.31` |
| Puerto | 80 → 8084 |

```bash
# Conectar kubectl
az aks get-credentials --resource-group rg-app-azure-cliente --name aks-app-cliente

# Ver pods
kubectl get pods

# Ver servicios
kubectl get svc

# Deploy
kubectl apply -f k8s/deployment.yml
```

---

### Azure Key Vault
| Campo | Valor |
|---|---|
| Nombre | `kv-app-cliente` |
| Región | West US 2 |
| Plan | Standard |
| URL | `https://kv-app-cliente.vault.azure.net/` |

#### Secretos
| Nombre | Descripción |
|---|---|
| `apim-subscription-key` | Key de suscripción APIM para Clientes API |
| `db-password` | Password base de datos PostgreSQL (Fase 4) |

```bash
# Crear secreto
az keyvault secret set --vault-name kv-app-cliente --name "nombre" --value "valor"

# Leer secreto
az keyvault secret show --vault-name kv-app-cliente --name "nombre" --query value -o tsv
```

---

### Azure App Configuration
| Campo | Valor |
|---|---|
| Nombre | `appconfig-app-cliente` |
| Endpoint | `https://appconfig-app-cliente.azconfig.io` |
| Plan | Free |
| Región | West US 2 |

#### Configuraciones
| Clave | Valor | Descripción |
|---|---|---|
| `app.ambiente` | `dev` | Entorno actual |
| `app.clientes.max-page-size` | `50` | Límite paginación |
| `guinea.apim.url` | `https://guinea.azure-api.net` | URL proveedor externo |

```bash
# Listar configs
az appconfig kv list --name appconfig-app-cliente --auth-mode login -o table

# Agregar config
az appconfig kv set --name appconfig-app-cliente --key "clave" --value "valor" --yes --auth-mode login
```

---

### Azure API Management (APIM)
| Campo | Valor |
|---|---|
| Nombre | `apim-app-cliente` |
| Gateway URL | `https://apim-app-cliente.azure-api.net` |
| Plan | Consumption (1M calls/mes gratis) |
| Región | West US |

#### API configurada
| Campo | Valor |
|---|---|
| API ID | `clientes-api` |
| Display Name | Clientes API |
| Path | `clientes` |
| Backend | `http://20.245.152.31` |
| Subscription Required | `true` |

#### Operaciones
| Método | URL | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/clientes/{id}` | Obtener cliente |
| POST | `/api/clientes` | Crear cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

#### Suscripción
| Campo | Valor |
|---|---|
| Nombre | `sub-clientes` |
| Ámbito | Clientes API |
| Clave Primaria | `dd4e2960a5e440988c0b6314f879513b` |

```bash
# Probar con subscription key
curl -H "Ocp-Apim-Subscription-Key: dd4e2960a5e440988c0b6314f879513b" \
  https://apim-app-cliente.azure-api.net/clientes/api/clientes

# Sin key → 401 Unauthorized
curl https://apim-app-cliente.azure-api.net/clientes/api/clientes
```

---

## Azure DevOps

| Campo | Valor |
|---|---|
| Organización | `cairampomaserinmatiisstevejobs` |
| URL | `https://dev.azure.com/cairampomaserinmatiisstevejobs` |
| Proyecto | `app-azure-cliente` |
| Email | `cairampomaserinmatiisstevejobs@gmail.com` |

#### Service Connections
| Nombre | Tipo | Descripción |
|---|---|---|
| `acr-connection` | Docker Registry | Conecta pipeline con ACR |
| `azure-connection` | Azure Resource Manager | Conecta pipeline con AKS |

#### Pipeline
| Campo | Valor |
|---|---|
| Archivo | `azure-pipelines.yml` |
| Trigger | Push a rama `dev` |
| Stage 1 | Build Maven + Docker + Push ACR |
| Stage 2 | Deploy a AKS |

#### Estado parallelism grant
- Formulario enviado el 21/04/2026
- Aprobación estimada: 2-3 días hábiles

---

## GitHub

| Campo | Valor |
|---|---|
| Organización | `cairampomaserinmatiisstevejobs-create` |
| Repo | `app-azure-cliente` |
| URL | `https://github.com/cairampomaserinmatiisstevejobs-create/app-azure-cliente` |

#### Ramas
| Rama | Propósito |
|---|---|
| `main` | Producción |
| `dev` | Integración — trigger del pipeline |
| `feature/pipeline-cicd` | Feature actual en desarrollo |

#### Flujo de trabajo
```
feature/* → PR → dev → pipeline dispara → AKS actualiza
```

---

## Aplicación Spring Boot

| Campo | Valor |
|---|---|
| Puerto | `8084` |
| Java | 21 (Temurin) |
| Framework | Spring Boot 3.4.4 + WebFlux + R2DBC |
| Base de datos | H2 en memoria (→ PostgreSQL en Fase 4) |
| Imagen Docker | `eclipse-temurin:21-jre-alpine` |

#### Endpoints locales
```bash
curl http://localhost:8084/api/clientes
curl http://localhost:8084/actuator/health
```

#### Endpoints AKS directo (sin key)
```bash
curl http://20.245.152.31/api/clientes
```

#### Endpoints via APIM (con key)
```bash
curl -H "Ocp-Apim-Subscription-Key: dd4e2960a5e440988c0b6314f879513b" \
  https://apim-app-cliente.azure-api.net/clientes/api/clientes
```

---

## Arquitectura actual

```
Internet
    │
    ▼
APIM Consumption — apim-app-cliente.azure-api.net
├── Subscription Key requerida
├── 5 operaciones (GET/POST/PUT/DELETE)
└── Backend: http://20.245.152.31
    │
    ▼
AKS — aks-app-cliente (2 nodos, West US)
├── Pod: app-azure-cliente (1 replica)
├── Spring Boot 3 + WebFlux + R2DBC
└── H2 en memoria
    │
    ▼
ACR — acrappcliente.azurecr.io
└── app-azure-cliente:1.0.0

Servicios de soporte:
├── Key Vault — kv-app-cliente (secretos)
├── App Configuration — appconfig-app-cliente (configs)
└── Azure DevOps — pipeline CI/CD (pendiente grant)
```
