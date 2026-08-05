#!/usr/bin/env bash
set -euo pipefail

missing=0
while IFS= read -r commit; do
  parents="$(git rev-list --parents -n 1 "$commit" | wc -w | tr -d ' ')"
  [[ "$parents" -gt 2 ]] && continue
  journals="$(git diff-tree --no-commit-id --name-only -r --root "$commit" | grep -cE '^journal/[^/]+\.md$' || true)"
  if [[ "$journals" -ne 1 ]]; then
    echo "traceability: commit $commit has $journals changed journals" >&2
    missing=1
  fi
done < <(git rev-list --reverse HEAD)

[[ "$missing" -eq 0 ]]
echo "traceability: all substantive commits have exactly one journal"
