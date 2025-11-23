# Pusher WebSocket Events Documentation

## Overview
This document describes how to connect [Pusher](https://pusher.com/docs/beams/reference/all-libraries/?ref=docs-index#client-sdks) WebSocket events to receive real-time updates about charging orders in the Powerly platform.

---

## Table of Contents
- [Connection Setup](#connection-setup)  
- [Authentication](#authentication)  
- [Channel Subscription](#channel-subscription)  
- [Events](#events)  
- [Implementation Examples](#implementation-examples)

---

## Connection Setup

### Configuration

| Parameter       | Value                                                             |
|-----------------|-------------------------------------------------------------------|
| Host            | `api.powerly.app`                                            |
| App Key         | `8735473`                                                     |
| WSS Port        | `443`                                                             |
| Auth Endpoint   | `https://api.powerly.app/broadcasting/auth`                 |

---

## Authentication

All private channels require authorization.

Include your bearer token in the header:

```
Authorization: Bearer {USER_TOKEN}
```

---

## Channel Subscription

Subscribe to private order channels:

```
private-orders.{ORDER_ID}
```

Example:

```
private-orders.2149
```

---

## Events

---

### 1. ChargePointConsumption

**Event:** `App\Events\ChargePointConsumption`  
**Trigger:** Fired periodically during an active charging session to report consumption updates.

#### Full Payload

```json
{
  "channel": "private-orders.2149",
  "event": "App\\Events\\ChargePointConsumption",
  "data": {
    "order": {
      "id": 2149,
      ...
    }
  }
}
```

*(Payload truncated for brevity — use your original full object.)*

---

### 2. ChargePointOrderCompleted

**Event:** `App\Events\ChargePointOrderCompleted`  
**Trigger:** Fired when a charging session is completed or stopped.

#### Full Payload

```json
{
  "channel": "private-orders.2149",
  "event": "App\\Events\\ChargePointOrderCompleted",
  "data": {
    "chargePoint": {
      "id": 626,
      ...
    }
  }
}
```

---

## Implementation Examples

---

# Kotlin (Android)

### Required Library

```gradle
dependencies {
    implementation 'com.pusher:pusher-java-client:2.4.4'
}
```

### Step 1: Initialize and Connect

```kotlin
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.util.HttpChannelAuthorizer

val userToken = "your_bearer_token"
val orderId = .....

val channelAuthorizer = HttpChannelAuthorizer("https://api.powerly.app/broadcasting/auth")
channelAuthorizer.setHeaders(mapOf("Authorization" to "Bearer $userToken"))

val options = PusherOptions()
    .setHost("api.powerly.app")
    .setWssPort(443)
    .setEncrypted(true)
    .setChannelAuthorizer(channelAuthorizer)

val pusher = Pusher("8735473", options)
val channel = pusher.subscribePrivate("private-orders.$orderId")

channel.bind("App\\Events\\ChargePointConsumption") { event ->
    println("Event: ${event.eventName}")
    println("Channel: ${event.channelName}")
    println("Raw Payload: ${event.data}")
}

channel.bind("App\\Events\\ChargePointOrderCompleted") { event ->
    println("Event: ${event.eventName}")
    println("Channel: ${event.channelName}")
    println("Raw Payload: ${event.data}")
}

pusher.connect()
```

### Step 2: Cleanup

```kotlin
override fun onDestroy() {
    super.onDestroy()
    pusher.disconnect()
}
```

---
