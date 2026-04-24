# Roadmap — Nuevo Microservicio con Flujo Completo Automatizado

> Tarea para mañana. Objetivo: flujo de desarrollo 100% trazable desde requerimiento hasta deploy.
> MSIRILDEV

---

## Visión

Cada nuevo microservicio nace desde un `.md` de requerimientos y viaja
automáticamente por todas las fases hasta producción, con Jira moviéndose
solo en cada etapa y pruebas validadas antes de cada merge.

---

## Flujo propuesto (9 Stages)

```
PUSH feature/* → [Stage 1] Jira En curso + Requerimiento
                → [Stage 2] Crear BDD (migracion Flyway/Liquibase)
                → [Stage 3] Contrato API (OpenAPI spec validado)
                → [Stage 4] Build Maven
                → [Stage 5] Pruebas automatizadas (WebTestClient)
                → [Stage 6] Crear PR automático
                → [Stage 7] Merge PR → Jira In Review
                → [Stage 8] Docker Build → ACR
                → [Stage 9] Deploy AKS → Jira Listo / Por hacer
```

---

## Detalle de cada Stage nuevo

### Stage 1 — Jira En curso + Validar Requerimiento
- Detectar o crear tarjeta Jira (ya existe)
- **NUEVO:** Verificar que exista `docs/requerimientos/SCRUM-NNN.md` en el repo
- Si no existe → comentar en Jira que falta el `.md` y fallar con mensaje claro
- Comentario en Jira: "Requerimiento validado ✅"

### Stage 2 — Crear / Migrar BDD
- Detectar si hay archivos nuevos en `src/main/resources/db/migration/`
- Ejecutar `mvn flyway:migrate` (o Liquibase) contra la BD de staging
- Si migración OK → comentar en Jira: "Migración BDD aplicada ✅ — V{version}"
- Jira permanece en "En curso"

### Stage 3 — Validar Contrato API (OpenAPI)
- Verificar que exista `docs/contrato/openapi.yml` o `openapi.json`
- Ejecutar `openapi-generator validate` o `swagger-cli validate`
- Si falla → Jira comentario con error, no avanza
- Si OK → comentar en Jira: "Contrato API validado ✅"

### Stage 4 — Build Maven
- `mvn package -DskipTests` (igual que ahora)
- Publicar JAR como artifact

### Stage 5 — Pruebas Automatizadas ← EL MAS IMPORTANTE
- `mvn test` con WebTestClient
- Happy path + unhappy path por cada endpoint del contrato
- Publicar reporte de pruebas como artifact
- Si falla → Jira a "Por hacer" + comentario con resumen de fallos
- Si OK → Jira comentario: "Pruebas ✅ {N} tests pasaron"
- **Mover Jira a "En Revisión"** al terminar este stage

### Stage 6 — Crear PR automático
- Igual que ahora con `gh pr create`

### Stage 7 — Merge PR
- Igual que ahora con `gh pr merge`

### Stage 8 — Docker Build → ACR
- Igual que ahora

### Stage 9 — Deploy AKS + Smoke test
- Deploy igual que ahora
- **NUEVO:** Smoke test post-deploy: curl a `/actuator/health`
- Si smoke test OK → Jira "Listo" + comentario con URL del servicio
- Si falla → rollback + Jira "Por hacer"

---

## Estructura de carpetas del nuevo microservicio

```
nuevo-servicio/
├── docs/
│   ├── requerimientos/
│   │   └── SCRUM-NNN.md          ← requerimiento en markdown
│   └── contrato/
│       └── openapi.yml            ← contrato API
├── src/
│   ├── main/
│   │   ├── java/...
│   │   └── resources/
│   │       └── db/migration/
│   │           └── V1__init.sql   ← migración BDD
│   └── test/
│       └── java/...               ← WebTestClient tests
├── Dockerfile
└── azure-pipelines.yml            ← pipeline completo 9 stages
```

---

## Template `docs/requerimientos/SCRUM-NNN.md`

```markdown
# SCRUM-NNN — Nombre de la Historia

## Descripción
Como [usuario], quiero [funcionalidad] para [beneficio].

## Criterios de aceptación (BDD)
- DADO que...  CUANDO...  ENTONCES...
- DADO que...  CUANDO...  ENTONCES...

## Endpoints
- GET /api/recurso
- POST /api/recurso
- GET /api/recurso/{id}

## Modelo de datos
| Campo | Tipo | Requerido |
|---|---|---|
| id | Long | Si |
| nombre | String | Si |

## Notas técnicas
- ...
```

---

## Movimiento de tarjetas Jira por stage

| Stage | Transición | ID |
|---|---|---|
| 1 — Requerimiento | En curso | 21 |
| 5 — Pruebas OK | In Review | 31 |
| 9 — Deploy OK | Listo | 41 |
| 9 — Deploy falla | Por hacer | 11 |
| 5 — Pruebas fallan | Por hacer | 11 |

---

## Por definir mañana

- [ ] Nombre del nuevo microservicio
- [ ] BDD: PostgreSQL Railway o H2 en tests?
- [ ] Contrato: OpenAPI 3.0 o generar desde anotaciones Spring?
- [ ] Reporte de pruebas: publicar como artifact o comentar resumen en Jira?
- [ ] Smoke test URL: loadBalancer IP o APIM?
- [ ] ¿Crear template de rama automático con SCRUM-NNN ya incluido?

---

## Por qué esto es viable

- El pipeline actual ya tiene la base sólida (9 meses de debug en un día 😄)
- Solo es agregar stages con lógica similar a la que ya funciona
- Jira REST API ya está integrada y testeada
- gh CLI + az CLI ya configurados con tokens correctos
- WebTestClient ya está en el stack (Spring WebFlux)

---

*Indra busca la forma de generar más rápido el desarrollo — nosotros ya la encontramos* 🚀
*MSIRILDEV*
