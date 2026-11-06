#!/usr/bin/env bash
# Remove o que o PIPELINE criou fora do docker-compose (Aula 26B):
#   - os containers de staging e producao (label pes26bg.ambiente)
#   - as imagens pes26bg/boletim-java e pes26bg/boletim-python, de todas as versoes
# O Gitea, o runner e o Traefik continuam; para eles, use docker compose down.
set -euo pipefail

containers=$(docker ps -aq --filter label=pes26bg.ambiente)
if [ -n "$containers" ]; then
  docker rm -f $containers >/dev/null
  echo "Containers de staging/producao removidos."
fi

imagens=$(docker images -q --filter reference='pes26bg/*' | sort -u)
if [ -n "$imagens" ]; then
  docker rmi -f $imagens >/dev/null
  echo "Imagens pes26bg/* removidas."
fi
echo "Pronto."
