# backend

## Documentation Rule

- If WebSocket/STOMP protocols are changed (destinations, payload shapes, auth headers, or behavior), `docs/websocket.md` must be updated in the same change.

## Current Product Alignment Notes

- Quick Match:
- city-wide targeting in selected city,
- requester excluded from city alert targets,
- accept creates chat room directly,
- no automatic mingle creation on accept.
- Mingle:
- optional place/time/target count are part of the API model.
- Chat:
- unread counts rely on participant last-read tracking.
- City:
- representative image and center coordinates are available for UI.

## I18N Contract Guidance

- Finalized frontend views require complete Korean/English UI parity.
- Backend responses should avoid Korean-only assumptions for user-facing labels.
- Where labels are DB-driven and shown in UI, keep English-capable fields available (example: keyword English label).

## Near-Term Backend Checklist

1. Keep FK-safe migration ordering for cleanup/data backfill changes.
2. Keep websocket docs synchronized with any event/payload changes.
3. Keep contract changes minimal and aligned with Figma-finalized frontend needs.
