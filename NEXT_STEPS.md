# NEXT_STEPS.md

## Purpose

- This file defines how to save task progress for later continuation.

## Save Command Behavior

- When the user says `save`, create a new Markdown file in the repository root.
- The filename must use Korea Standard Time in `YYYYMMDDHHmm.md` format.
- The save note must summarize:
  - what was completed in the current work session
  - what should be done next
  - any blockers, assumptions, or follow-up checks worth knowing

## Team Project Rules

- Assume this repository may contain tracked or untracked teammate changes.
- Do not overwrite, stage, commit, or remove unrelated teammate work.
- Save notes should describe only the work relevant to the current task unless the user asks for a broader project summary.

## Git Rules

- After finishing a task, commit and push without asking for permission.
- If multiple files were changed for one task, create separate commits per file.
