# Setup GitHub Actions — app-azure-cliente

## Paso 1 — Crear Service Principal con OIDC (una sola vez)

```bash
SUBSCRIPTION_ID="60aa0ad8-fd7e-4219-b0e5-c3e45798d4b8"
RG="rg-app-azure-cliente"
ACR_NAME="acrappcliente"
AKS_NAME="aks-app-cliente"
APP_NAME="app-github-actions-app-cliente"
GITHUB_USER="<tu-usuario-github>"
GITHUB_REPO="app-azure-cliente"

# 1. Crear App Registration
APP_ID=$(az ad app create --display-name "$APP_NAME" --query appId -o tsv)

# 2. Crear Service Principal
az ad sp create --id "$APP_ID"

# 3. Obtener datos
SP_OBJECT_ID=$(az ad sp show --id "$APP_ID" --query id -o tsv)
TENANT_ID=$(az account show --query tenantId -o tsv)

echo "APP_ID:    $APP_ID"
echo "TENANT_ID: $TENANT_ID"

# 4. Federated credential para rama dev
az ad app federated-credential create --id "$APP_ID" --parameters "{
  \"name\": \"github-dev\",
  \"issuer\": \"https://token.actions.githubusercontent.com\",
  \"subject\": \"repo:${GITHUB_USER}/${GITHUB_REPO}:ref:refs/heads/dev\",
  \"audiences\": [\"api://AzureADTokenExchange\"]
}"

# 5. Roles RBAC
ACR_ID=$(az acr show --name "$ACR_NAME" --resource-group "$RG" --query id -o tsv)
AKS_ID=$(az aks show --name "$AKS_NAME" --resource-group "$RG" --query id -o tsv)

az role assignment create --assignee "$APP_ID" --role "AcrPush" --scope "$ACR_ID"
az role assignment create --assignee "$APP_ID" --role "Azure Kubernetes Service Cluster User Role" --scope "$AKS_ID"
az role assignment create --assignee "$APP_ID" --role "Azure Kubernetes Service RBAC Writer" --scope "$AKS_ID"
```

## Paso 2 — Secrets en GitHub

`Settings → Secrets and variables → Actions → New repository secret`

| Secret | Valor |
|---|---|
| `AZURE_CLIENT_ID` | valor de `$APP_ID` |
| `AZURE_TENANT_ID` | valor de `$TENANT_ID` |
| `AZURE_SUBSCRIPTION_ID` | `60aa0ad8-fd7e-4219-b0e5-c3e45798d4b8` |

## Paso 3 — Environment en GitHub

`Settings → Environments → New environment → nombre: staging`

## Flujo de ramas

```
feature/* → PR → pr-check.yml (build+test, sin deploy)
dev       → push → ci-cd.yml (build + push ACR + deploy AKS)
```

## Diferencias vs azure-pipelines.yml

| Concepto | Azure DevOps | GitHub Actions |
|---|---|---|
| Build.BuildId | `$(Build.BuildId)` | `${{ github.run_number }}` |
| acr-connection | Service Connection | OIDC — sin password |
| azure-connection | Service Connection | OIDC — sin password |
| Parallelism | Grant manual (días) | 2000 min/mes — inmediato |
| Maven cache | Cache@2 manual | `cache: maven` automático |
