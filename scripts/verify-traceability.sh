#!/usr/bin/env bash
set -euo pipefail

mode="${1:---worktree}"
if [[ "$mode" == "--staged" ]]; then
  changed="$(git diff --cached --name-only --diff-filter=ACMR)"
else
  changed="$(git diff --name-only --diff-filter=ACMR; git ls-files --others --exclude-standard)"
fi

if [[ -z "$changed" ]]; then
  echo "traceability: no changed files"
  exit 0
fi

journal_count="$(printf '%s\n' "$changed" | grep -cE '^journal/[^/]+\.md$' || true)"
if [[ "$journal_count" -ne 1 ]]; then
  echo "traceability: expected exactly one changed journal, found $journal_count" >&2
  exit 1
fi

journal_file="$(printf '%s\n' "$changed" | grep -E '^journal/[^/]+\.md$')"
grep -Eq 'Novo ADR criado:|ADR aplicado:|Decisao local sem ADR novo:' "$journal_file" || {
  echo "traceability: $journal_file must declare ADR status" >&2
  exit 1
}

echo "traceability: exactly one journal with ADR status"
