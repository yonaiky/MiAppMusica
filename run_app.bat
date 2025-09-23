@echo off
echo Compilando MiAppMusica...
call gradlew assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa!
    echo Instalando en dispositivo...
    call gradlew installDebug
    if %ERRORLEVEL% EQU 0 (
        echo App instalada correctamente!
    ) else (
        echo Error al instalar la app
    )
) else (
    echo Error en la compilacion
)
pause
