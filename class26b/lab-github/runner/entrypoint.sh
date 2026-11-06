#!/usr/bin/env bash
# Registra o runner no repositorio (so' na primeira vez) e fica esperando jobs.
set -euo pipefail
cd /home/runner

if [ ! -f .runner ]; then
  : "${REPO_URL:?defina REPO_URL no .env (https://github.com/SEU-USUARIO/SEU-REPOSITORIO)}"
  : "${RUNNER_TOKEN:?defina RUNNER_TOKEN no .env (Settings > Actions > Runners > New self-hosted runner)}"
  ./config.sh --unattended --replace \
    --url "$REPO_URL" --token "$RUNNER_TOKEN" \
    --name "${RUNNER_NAME:-pes26b-runner}" --labels "${RUNNER_LABELS:-pes26b}" \
    --work _work
fi

# O token de registro nao e' mais necessario: tira do ambiente dos jobs.
unset RUNNER_TOKEN
exec ./run.sh
