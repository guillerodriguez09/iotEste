#!/bin/bash

# Script para probar todas las configuraciones del simulador automáticamente
# Uso: ./test-all-configs.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_SCRIPT="${SCRIPT_DIR}/test-configs.sh"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

echo_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
}

# Obtener lista de configuraciones
get_configs() {
    local config_dir="simulador/blackBox/config"
    for config in ${config_dir}/config_*.json ${config_dir}/config_default.json; do
        if [ -f "$config" ]; then
            basename "$config" .json | sed 's/^config_//'
        fi
    done | sort -V | uniq
}

# Probar una configuración
test_config() {
    local config_num=$1
    echo_test "========================================="
    echo_test "Probando configuración: $config_num"
    echo_test "========================================="
    
    if "$TEST_SCRIPT" "$config_num" > /tmp/test_config_${config_num}.log 2>&1; then
        echo_info "✓ Configuración $config_num: EXITOSA"
        return 0
    else
        echo_error "✗ Configuración $config_num: FALLÓ"
        echo_warn "  Ver logs en: /tmp/test_config_${config_num}.log"
        return 1
    fi
}

# Función principal
main() {
    echo_info "Iniciando pruebas con todas las configuraciones..."
    echo ""
    
    local total=0
    local passed=0
    local failed=0
    local failed_configs=()
    
    # Obtener configuraciones
    local configs=($(get_configs))
    
    if [ ${#configs[@]} -eq 0 ]; then
        echo_error "No se encontraron configuraciones"
        exit 1
    fi
    
    echo_info "Configuraciones encontradas: ${#configs[@]}"
    echo ""
    
    # Probar cada configuración
    for config in "${configs[@]}"; do
        total=$((total + 1))
        if test_config "$config"; then
            passed=$((passed + 1))
        else
            failed=$((failed + 1))
            failed_configs+=("$config")
        fi
        echo ""
        sleep 2  # Pequeña pausa entre pruebas
    done
    
    # Resumen
    echo_test "========================================="
    echo_test "RESUMEN DE PRUEBAS"
    echo_test "========================================="
    echo_info "Total: $total"
    echo_info "Exitosas: $passed"
    if [ $failed -gt 0 ]; then
        echo_error "Fallidas: $failed"
        echo_error "Configuraciones fallidas: ${failed_configs[*]}"
    else
        echo_info "Fallidas: 0"
    fi
    echo ""
    
    if [ $failed -eq 0 ]; then
        echo_info "✓ Todas las configuraciones funcionan correctamente"
        exit 0
    else
        echo_error "✗ Algunas configuraciones fallaron"
        exit 1
    fi
}

main "$@"

