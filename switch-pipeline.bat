@echo off
setlocal

echo ============================================
echo   SWITCH PIPELINE — Azure DevOps / GitHub
echo   MSIRILDEV
echo ============================================
echo.
echo  [1] Activar AZURE DEVOPS  (desactiva GitHub Actions)
echo  [2] Activar GITHUB ACTIONS (desactiva Azure DevOps)
echo  [3] Ver estado actual
echo  [4] Salir
echo.
set /p OPCION="Elige una opcion (1-4): "

if "%OPCION%"=="1" goto activar_azdo
if "%OPCION%"=="2" goto activar_gha
if "%OPCION%"=="3" goto ver_estado
if "%OPCION%"=="4" goto fin

echo Opcion invalida.
goto fin

:activar_azdo
echo.
echo [*] Desactivando GitHub Actions ci-cd.yml...
gh workflow disable ci-cd.yml --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente
echo [*] Activando Azure DevOps pipeline id=1...
az pipelines update --id 1 --status enabled
echo.
echo [OK] Azure DevOps ACTIVO / GitHub Actions DESACTIVADO
goto fin

:activar_gha
echo.
echo [*] Activando GitHub Actions ci-cd.yml...
gh workflow enable ci-cd.yml --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente
echo [*] Desactivando Azure DevOps pipeline id=1...
az pipelines update --id 1 --status disabled
echo.
echo [OK] GitHub Actions ACTIVO / Azure DevOps DESACTIVADO
goto fin

:ver_estado
echo.
echo --- GitHub Actions ---
gh workflow list --repo cairampomaserinmatiisstevejobs-create/app-azure-cliente --all
echo.
echo --- Azure DevOps ---
az pipelines list --output table
goto fin

:fin
echo.
pause
endlocal
