from gtts import gTTS
import io

'''
It takes a string and converts to it audio by Google Text to Speech, 
it requires internet. Instead of saving file I am Writting it in In-Memory Byte Stream
It means RAM. the storage is temporary 

'''
def convert_to_audio(str):
    tts = gTTS(text=str, lang='en')
    file=io.BytesIO()
    tts.write_to_fp(file)
    file.seek(0)
    audio=file.read()
    return audio

