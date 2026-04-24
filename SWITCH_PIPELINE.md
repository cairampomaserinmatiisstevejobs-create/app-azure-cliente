# Switch Pipeline — Azure DevOps ↔ GitHub Actions

## Contexto

El proyecto tiene dos pipelines equivalentes con los mismos 5 stages + Jira auto-move.
Solo uno debe estar activo a la vez para evitar doble ejecución.

| Pipeline | Archivo | ID |
|---|---|---|
| **GitHub Actions** | `.github/workflows/ci-cd.yml` | `264467672` |
| **Azure DevOps** | `azure-pipelines.yml` | `1` |

---

## Activar GitHub Actions / Desactivar Azure DevOps

```bash
gh workflow enable ci-cd.yml --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente
az pipelines update --id 1 --status disabled
```

## Activar Azure DevOps / Desactivar GitHub Actions

```bash
gh workflow disable ci-cd.yml --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente
az pipelines update --id 1 --status enabled
```

## Ver estado actual

```bash
gh workflow list --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente --all
az pipelines list --output table
```

---

## Estado actual (al momento de crear este archivo)

- GitHub Actions `ci-cd.yml` → **DESHABILITADO** (`disabled_manually`)
- Azure DevOps pipeline `id=1` → **HABILITADO** (`enabled`)

---

*MSIRILDEV*
