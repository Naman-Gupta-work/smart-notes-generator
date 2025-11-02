from transformers import pipeline,AutoTokenizer


summarizer = pipeline("summarization",model="facebook/bart-large-cnn",device="cpu")
tokenizer = AutoTokenizer.from_pretrained("facebook/bart-large-cnn")
max=tokenizer.model_max_length

def summarize_text(text):
    tokens=tokenizer.encode(text)
    summaries=[]
    for i in range(0,len(tokens),max):
        chunk=tokens[i:i+max]
        chunk_text = tokenizer.decode(chunk)
        summary = summarizer(chunk_text,max_length=150,min_length=20,do_sample=False)
        summaries.append(summary[0]['summary_text'])
    return " ".join(summaries)

#This was first developed to check the Working of functionality, we can use it for different purposes
def summarize(text):
    summary=summarizer(text,max_length=700,min_length=20,do_sample=False)
    return summary

