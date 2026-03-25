# AGENTS.md

## Project Context

- This repository is a Kotlin Spring Boot backend project.
- Follow existing Gradle Kotlin DSL and Spring Boot conventions in this codebase.

## Git Workflow Rules

- When completing a user task, make git commits proactively without asking for permission.
- Push commits proactively without asking for permission.
- Always commit and push after finishing a task.
- If a single task changes multiple files, split the work into separate commits by file.
- Default rule: one changed file per commit.
- Do not combine unrelated file changes into a single commit, even if they were requested in one prompt.
- Use clear, specific commit messages that describe the change in that one file.

## Permission Rules

- Do not ask for permission before normal development operations such as editing files, creating commits, or pushing commits.
- Ask for permission before dangerous or destructive git operations.
- Dangerous operations include `git reset`, `git reset --hard`, `git revert`, force-push, history rewriting, or anything else that can discard or rewrite work.

## Change Safety

- This is a team project and the worktree may contain teammate changes that are not yet tracked by git.
- Never revert or overwrite user changes unless the user explicitly requests it.
- Preserve unrelated tracked and untracked changes and limit edits, staging, commits, and pushes to the files required for the current task.

## Code Conventions To Remember

- Follow the existing `controller -> facade -> service -> repository` flow for backend features.
- Controllers should wrap facade results in response objects.
- Facade and service layers should return DTOs or simple values, not JPA entities.
- JPA entities should stay internal to persistence-facing logic.
- Keep sensitive data such as passwords out of outward-facing DTOs and responses.
- For schema changes, prefer Liquibase changelogs over JPA auto-DDL and keep table names explicit.
- Use custom exceptions only; prefer reusable base exception types that carry HTTP status and reason metadata.
- Put `@Transactional` on service-layer write operations that mutate database state.
- For partial update APIs, support three states per field: omitted means no change, explicit null means set null, and a concrete value means update to that value.
- Prefer Liquibase `loadData` with versioned seed files for stable reference data such as countries and cities.
- When adding, changing, or removing entity columns, always inspect and update the related APIs, request/response DTOs, service/facade logic, mappings, and Liquibase scripts together.
- Name Liquibase changelog files with `yyyyMMddHHmmss` timestamps so migrations stay ordered and team changes are less likely to collide.
- When adding a new entity, create the related CRUD layers, exceptions, mappings, and schema changes together unless the task explicitly says otherwise.
- When authentication rules change, update the security filter chain, login flow, related exceptions, and required seed data together.

## Save Workflow

- When the user says `save`, create a Markdown handoff note that summarizes what was completed and what should be done next.
- Save notes must be stored under `save-notes/`, not in the repository root.
- The save note filename must use Korea Standard Time in `YYYYMMDDHHmm.md` format.
- Save notes should be concise and practical for teammates continuing the work.
