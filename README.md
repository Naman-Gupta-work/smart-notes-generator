# Smart Notes Generator - Backend

Welcome, frontend developer! This is the complete backend for the Smart Notes Generator. This guide will show you how to run the backend locally for development and how to interact with its API.

## 🚀 Overview

This is an **asynchronous** system. This means when a user uploads a video, they will **not** get the result back immediately.

Instead, they will get a **`jobId`** (a tracking number). Your frontend will then use this `jobId` to "poll" (ask every few seconds) a results endpoint until the processed audio/text is ready.

### Backend Architecture

The backend consists of three main parts:
1.  **Java (Spring Boot) API:** Handles the file upload and serves the final results.
2.  **Redis:** Acts as a "post office," using Pub/Sub for messaging and as a database to store results.
3.  **Python Worker:** A background service that listens for jobs, runs the AI models (Whisper, gTTS), and sends the results back.

**The Data Flow:**
`Frontend (Upload) -> Java API -> Redis -> Python Worker (Process) -> Redis (Store) -> Frontend (Fetch Result)`

---

## 🏁 Getting Started (Running the Backend Locally)

You will need to run three separate services in three separate terminals.

### Prerequisites
* [Java (JDK 17+)](https://www.oracle.com/java/technologies/downloads/)
* [Maven](https://maven.apache.org/download.cgi)
* [Python (3.11 recommended)](https://www.python.org/downloads/)
* [Redis](https://redis.io/docs/latest/operate/oss/platforms/install-redis/) (Make sure the Redis server is running)

---
### 1. 🖥️ Terminal 1: Run the Java API
```bash
# Navigate to the Java folder
cd java-backend

# Run the Spring Boot application
mvn spring-boot:run
```
The Java API will now be running on `http://localhost:8080`.

---
### 2. 🐍 Terminal 2: Run the Python Worker
```bash
# Navigate to the Python folder
cd python-worker

# Create and activate the virtual environment
python -m venv venv
.\venv\Scripts\Activate.ps1  
# (Note: You may need to run `Set-ExecutionPolicy -Scope Process RemoteSigned` first)

# Install all required libraries
pip install -r requirements.txt

# Start the listener script
python subscriber.py
```
This terminal will now show "Waiting for messages..."

---

## 📖 API Contract

Your frontend will need to interact with two main endpoints.

### 1. Uploading a File (The "Order")

This endpoint accepts the video file and starts the processing job.

* **URL:** `POST /api/v1/upload`
* **Request Type:** `multipart/form-data`
* **Form Fields:**
    * `file`: The video file (`.mp4`, `.mp3`, etc.)
    * `action`: A string, either `TEXT` or `AUDIO`

#### ✅ Success Response (200 OK)
The server will immediately return this JSON. Your frontend **must save the `jobId`** to use in the next step.

```json
{
    "status": "SUCCESS",
    "message": "File Uploaded Successfully",
    "jobId": "78e15137-3259-4bf8-9cda-528d39ecb100"
}
```

#### ❌ Failure Response (500 Server Error)
```json
{
    "status": "FAILURE",
    "message": "Could Not store file",
    "jobId": null
}
```

---
### 2. Fetching the Result (The "Pickup")

This is the "pickup window." Your frontend should call this endpoint in a loop (e.g., once every 3-5 seconds) using the `jobId` it saved.

* **URL:** `GET /api/v1/results/{jobId}/audio`
* *(Replace `{jobId}` with the actual ID, e.g., `/api/v1/results/78e15137-.../audio`)*

#### ⏳ "Processing" Response (204 No Content)
* If the job is not ready, the server will return an **empty response** with **HTTP Status 204**.
* When your React app gets a 204, it should just wait a few seconds and try calling this endpoint again.

#### ✅ "Success" Response (200 OK)
* Once the job is done, the server will return the **raw audio data** with a `Content-Type: audio/mpeg`.
* You can load this response directly into an HTML `<audio>` tag to play it.

*(Note: You can also build a `GET /api/v1/results/{jobId}/text` endpoint that returns the summary text as JSON, following the same logic.)*
