# WebSocket (STOMP) Guide

## Endpoint
- WebSocket endpoint: `/ws-chat`
- STOMP application prefix: `/app`
- STOMP broker prefix: `/topic`

## Authentication
- A valid JWT access token is required in the STOMP `CONNECT` frame native header:
  - header name: `accessToken`
- If `accessToken` is missing or invalid, connection/authentication fails.

Example CONNECT headers:
```text
accept-version:1.2
host:localhost
accessToken:<JWT_ACCESS_TOKEN>
```

## Chat Messaging
Publish:
- destination: `/app/chatrooms/{chatRoomId}/messages`
- payload:
```json
{
  "content": "Hello from websocket"
}
```

Subscribe:
- destination: `/topic/chatrooms/{chatRoomId}`
- event payload:
```json
{
  "delivery": {
    "message": {
      "id": 1,
      "chatRoomId": 10,
      "senderUserId": 3,
      "content": "Hello from websocket",
      "createdDateTime": "2026-04-01T17:00:00"
    },
    "translations": [
      {
        "id": 101,
        "chatMessageId": 1,
        "userId": 5,
        "translatedContent": "웹소켓에서 보낸 인사",
        "createdDateTime": "2026-04-01T17:00:00",
        "updatedDateTime": "2026-04-01T17:00:00"
      }
    ]
  }
}
```

Translation behavior for incoming chat messages:
- Server first detects the message language with OpenAI.
- If detected language already matches a recipient's primary language (derived from recipient nationality), translation is skipped for that recipient.
- Otherwise, server requests OpenAI translation, stores it in `chat_message_translations`, and includes it in `delivery.translations` for recipients.

## Quick Match Alerts
Created/accepted quick-match alerts are published by city and by user.

Quick-match actions are available over STOMP publish as well:
- create: destination `/app/quick-matches`
  - body:
  ```json
  {
    "cityId": 1,
    "message": "Coffee tonight?"
  }
  ```
- accept: destination `/app/quick-matches/{quickMatchId}/accept`
- decline: destination `/app/quick-matches/{quickMatchId}/decline`

City subscriptions:
- `/topic/cities/{cityId}/quick-matches` (all target types)
- `/topic/cities/{cityId}/quick-matches/minglers`
- `/topic/cities/{cityId}/quick-matches/locals`
- `/topic/cities/{cityId}/quick-matches/any`

Quick-match targeting behavior:
- Server now treats quick-match create requests as city-wide broadcast in the selected city.
- `targetType` from client create payloads is ignored and persisted as `ANY`.
- Recipients are active travelers + locals in the city, excluding the requester.
- Accepting a quick match now creates a chat room directly (no mingle is auto-created).

User subscription:
- `/topic/users/{userId}/quick-matches`

City event payload:
```json
{
  "eventType": "QUICK_MATCH_CREATED",
  "targetType": "ANY",
  "quickMatch": {
    "id": 20,
    "requesterUserId": 3,
    "cityId": 1,
    "message": "Coffee tonight?",
    "targetType": "ANY",
    "status": "PENDING",
    "acceptedByUserId": null,
    "mingleId": null,
    "createdDateTime": "2026-04-01T17:00:00",
    "updatedDateTime": "2026-04-01T17:00:00"
  },
  "targetUserIds": [5, 8, 12]
}
```

User event payload:
```json
{
  "eventType": "QUICK_MATCH_ACCEPTED",
  "quickMatch": {
    "id": 20,
    "requesterUserId": 3,
    "cityId": 1,
    "message": "Coffee tonight?",
    "targetType": "ANY",
    "status": "ACCEPTED",
    "acceptedByUserId": 8,
    "mingleId": null,
    "createdDateTime": "2026-04-01T17:00:00",
    "updatedDateTime": "2026-04-01T17:02:00"
  },
  "chatRoom": {
    "id": 31,
    "name": null,
    "directChat": true,
    "mingleId": null,
    "participantUserIds": [3, 8],
    "unreadMessageCount": 0,
    "createdDateTime": "2026-04-01T17:02:00",
    "updatedDateTime": "2026-04-01T17:02:00"
  }  
}
```

User error payload (for STOMP quick-match actions):
```json
{
  "eventType": "QUICK_MATCH_ERROR",
  "action": "QUICK_MATCH_ACCEPT",
  "reason": "Quick match not found for id=123"
}
```

## Minimal JavaScript Client Example
```javascript
import { Client } from "@stomp/stompjs";

const client = new Client({
  brokerURL: "ws://localhost:8080/ws-chat",
  connectHeaders: {
    accessToken: "<JWT_ACCESS_TOKEN>",
  },
  reconnectDelay: 5000,
});

client.onConnect = () => {
  client.subscribe("/topic/chatrooms/10", (frame) => {
    console.log("chat:", JSON.parse(frame.body));
  });

  client.subscribe("/topic/cities/1/quick-matches/any", (frame) => {
    console.log("quick-match any:", JSON.parse(frame.body));
  });

  client.publish({
    destination: "/app/chatrooms/10/messages",
    body: JSON.stringify({ content: "Hello from STOMP client" }),
  });
};

client.activate();
```

## Related REST APIs
- Read chat history: `GET /chatrooms/{chatRoomId}/messages`
- Mark chat room as read: `POST /chatrooms/{chatRoomId}/read`
- Create quick-match: `POST /quick-matches`
- Accept quick-match: `POST /quick-matches/{quickMatchId}/accept`
- Decline quick-match: `POST /quick-matches/{quickMatchId}/decline`

Chat history response note:
- `GET /chatrooms/{chatRoomId}/messages` includes each message's optional `translatedContent` for the requesting user when a stored translation exists.
