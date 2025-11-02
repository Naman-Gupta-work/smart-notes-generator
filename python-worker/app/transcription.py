import os

import whisper

model=whisper.load_model("base")

def transcribe_audio(file_path: str):
    if not os.path.exists(file_path):
        return None
    try:
        result=model.transcribe(file_path,task='transcribe')
        return result['text']
    except Exception as e:
        return None


# This function takes a file path and convert that Video into audio and returns result;
# The path is being passed from Redis