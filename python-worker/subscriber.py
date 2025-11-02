import redis
import json
from app.transcription import transcribe_audio
from app.summarizer import summarize_text
from app.tts import convert_to_audio
import base64
REDIS_HOST = 'localhost'
REDIS_PORT = 6379
JAVA_TO_PYTHON_CHANNEL = 'java2python'
PYTHON_TO_JAVA_CHANNEL = 'python2java'


def main():
    print("Starting Python subscriber...")

    r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)

    pubsub = r.pubsub()
    pubsub.subscribe(JAVA_TO_PYTHON_CHANNEL)

    print(f"Subscribed to '{JAVA_TO_PYTHON_CHANNEL}'. Waiting for messages...")
    for message in pubsub.listen():
        if  message['type'] == 'message':
                try:
                    print(f"Message received: {message}")
                    data = json.loads(message['data'])
                    jobId=data.get('jobId')
                    video_path = data.get('filePath')
                    action = data.get('action')
                    print(video_path+" "+action)
                    text = transcribe_audio(video_path)

                    if text is None:
                        message = json.dumps(
                            {
                             "jobId": jobId,
                             "status": "ERROR",
                             "data": "Transcription failed"})
                        r.publish(PYTHON_TO_JAVA_CHANNEL, message)
                        raise RuntimeError("Could not transcribe audio")


                    summary = summarize_text(text)

                    if action.lower() == 'text':
                            response = {
                                        "jobId": jobId,
                                        "status": "SUCCESS",
                                        "type": "summary",
                                        "data": summary
                            }
                            r.publish(PYTHON_TO_JAVA_CHANNEL, json.dumps(response))

                    elif action.lower() == 'audio':
                            audio_bytes=convert_to_audio(summary)
                            if audio_bytes:
                                encoded_bytes = base64.b64encode(audio_bytes).decode('utf-8')
                                response={
                                          'jobId': jobId,
                                          'status': 'SUCCESS',
                                          'type': 'audio',
                                          'data': encoded_bytes
                                }
                                r.publish(PYTHON_TO_JAVA_CHANNEL, json.dumps(response))
                                print("published")
                            else:
                                raise RuntimeError("Audio conversion failed")

                    else:
                          raise ValueError("Unknown action")


                except redis.exceptions.ConnectionError as e:
                    print(f" Redis connection error: {e}")
                    print("Attempting to reconnect in 5 seconds...")

                except Exception as e:
                    print(f"An unexpected error occurred: {e}")



if __name__ == "__main__":
    main()