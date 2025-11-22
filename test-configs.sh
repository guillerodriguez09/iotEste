#!/bin/bash

# Script para probar el controlador con diferentes configuraciones del simulador
# Uso: ./test-configs.sh [config_number]
# Ejemplo: ./test-configs.sh 1

set -e

CONFIG_DIR="simulador/blackBox/config"
TARGET_CONFIG="config/simulation_config.json"
DOCKER_COMPOSE_FILE="docker-compose-integracion.yml"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Listar configuraciones disponibles
list_configs() {
    echo_info "Configuraciones disponibles:"
    for config in ${CONFIG_DIR}/config_*.json ${CONFIG_DIR}/config_default.json; do
        if [ -f "$config" ]; then
            basename "$config" | sed 's/\.json$//'
        fi
    done | sort -V
}

# Verificar que existe la configuración
check_config() {
    local config_name=$1
    local config_file="${CONFIG_DIR}/${config_name}.json"
    
    if [ ! -f "$config_file" ]; then
        echo_error "Configuración no encontrada: $config_file"
        return 1
    fi
    return 0
}

# Copiar configuración
copy_config() {
    local config_name=$1
    local config_file="${CONFIG_DIR}/${config_name}.json"
    
    echo_info "Copiando configuración: $config_name"
    cp "$config_file" "$TARGET_CONFIG"
    echo_info "Configuración copiada a $TARGET_CONFIG"
}

# Reiniciar servicios
restart_services() {
    echo_info "Reiniciando servicios..."
    docker compose -f "$DOCKER_COMPOSE_FILE" stop simulator controlador-temperatura 2>/dev/null || true
    docker compose -f "$DOCKER_COMPOSE_FILE" up -d simulator
    echo_info "Esperando a que el simulador esté listo..."
    sleep 15
    # Verificar que el simulador responde
    for i in {1..10}; do
        if curl -s http://localhost:8080/site-config > /dev/null 2>&1; then
            echo_info "Simulador listo"
            break
        fi
        echo_info "Esperando simulador... ($i/10)"
        sleep 2
    done
    docker compose -f "$DOCKER_COMPOSE_FILE" up -d controlador-temperatura
    echo_info "Esperando a que el controlador esté listo..."
    sleep 15
}

# Verificar que funciona
verify_system() {
    echo_info "Verificando sistema..."
    
    # Verificar que el simulador responde (con reintentos)
    local max_retries=5
    local retry=0
    while [ $retry -lt $max_retries ]; do
        if curl -s http://localhost:8080/site-config > /dev/null 2>&1; then
            echo_info "✓ Simulador responde correctamente"
            break
        fi
        retry=$((retry + 1))
        if [ $retry -lt $max_retries ]; then
            echo_info "Esperando simulador... ($retry/$max_retries)"
            sleep 3
        else
            echo_error "El simulador no responde después de $max_retries intentos"
            return 1
        fi
    done
    
    # Verificar que el controlador está recibiendo mediciones
    echo_info "Verificando que el controlador recibe mediciones..."
    sleep 8
    local logs=$(docker logs --tail 30 controlador-temp 2>&1)
    
    if echo "$logs" | grep -q "Temperatura.*°C"; then
        echo_info "✓ Sistema funcionando correctamente - Recibiendo mediciones"
        return 0
    else
        echo_warn "⚠ No se detectaron mediciones en los últimos logs"
        echo_warn "Revisa los logs con: docker logs -f controlador-temp"
        return 1
    fi
}

# Mostrar resumen de la configuración
show_config_summary() {
    local config_file=$1
    echo_info "Resumen de la configuración:"
    echo ""
    
    if command -v jq > /dev/null; then
        echo "  Sitio: $(jq -r '.simulacion.site' "$config_file")"
        echo "  Energía máxima: $(jq -r '.simulacion.maxEnergy' "$config_file")"
        echo "  Habitaciones: $(jq -r '.units | length' "$config_file")"
        echo "  Escenario: $(jq -r '.simulacion.escenario' "$config_file")"
        echo ""
        echo "  Habitaciones:"
        jq -r '.units[] | "    - \(.room.name): temp esperada \(.room.expectedTemp)°C, energía \(.room.energy)"' "$config_file"
    else
        echo "  (Instala 'jq' para ver detalles de la configuración)"
    fi
}

# Función principal
main() {
    if [ "$1" == "--list" ] || [ "$1" == "-l" ]; then
        list_configs
        exit 0
    fi
    
    if [ -z "$1" ]; then
        echo_error "Uso: $0 [config_number|config_name]"
        echo ""
        echo "Ejemplos:"
        echo "  $0 1              # Probar con config_1.json"
        echo "  $0 default        # Probar con config_default.json"
        echo "  $0 --list         # Listar configuraciones disponibles"
        echo ""
        list_configs
        exit 1
    fi
    
    local config_name="config_$1"
    if [ "$1" == "default" ]; then
        config_name="config_default"
    fi
    
    if ! check_config "$config_name"; then
        exit 1
    fi
    
    local config_file="${CONFIG_DIR}/${config_name}.json"
    show_config_summary "$config_file"
    
    copy_config "$config_name"
    restart_services
    
    if verify_system; then
        echo_info "✓ Prueba completada exitosamente"
        echo_info "Ver logs en tiempo real con: docker logs -f controlador-temp"
    else
        echo_error "✗ La prueba falló. Revisa los logs."
        exit 1
    fi
}

main "$@"

