import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import numpy
from contextlib import asynccontextmanager
import joblib
import re

# Проблема несовместимости версий
# библиотечка с проверкой на матюки просит на sklearn == 1.4.0
# но тогда не грузится моделька с проверкой на спам через ml

# Определяем lifespan-контекст, где инициализируем и закрываем пул
@asynccontextmanager
async def lifespan(app: FastAPI):
    yield

app = FastAPI(lifespan=lifespan)
spam_vs_text_model = joblib.load("NB_spam.joblib")
print(spam_vs_text_model.predict(['hello']))

class TextRequest(BaseModel):
    text: str

class AnswerResponse(BaseModel):
    result: int

@app.post("/check_spam_ml", response_model=AnswerResponse)
async def read_item(request: TextRequest):
    text = re.sub(r'[^a-zA-Zа-яёА-ЯЁ0-9\s]', ' ', request.text.lower())
    prediction = spam_vs_text_model.predict([text])
    return {"result": int(prediction[0])}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "spam_check_ml_hack:app", 
        host="127.0.0.1", 
        port=int(os.getenv("PORT", 8055)), 
        reload=True
    )
