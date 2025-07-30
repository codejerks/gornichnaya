import os
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from check_swear import SwearingCheck
from contextlib import asynccontextmanager

import logging
import warnings
from sklearn.exceptions import InconsistentVersionWarning

# Проблема несовместимости версий
# библиотечка с проверкой на матюки просит на sklearn == 1.4.0
# но тогда не грузится моделька с проверкой на спам через ml
warnings.filterwarnings("ignore", category=InconsistentVersionWarning)

# Configure basic logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)

# Create a logger instance
logger = logging.getLogger(__name__)

# Определяем lifespan-контекст, где инициализируем и закрываем пул
@asynccontextmanager
async def lifespan(app: FastAPI):
    yield

app = FastAPI(lifespan=lifespan)
sch = SwearingCheck()

class TextRequest(BaseModel):
    text: str

class AnswerResponse(BaseModel):
    is_cursed: int

@app.post("/check_curses", response_model=AnswerResponse)
async def read_item(request: TextRequest):
    logger.info('got new request '+str(request))
    proba = sch.predict_proba(request.text)[0]
    logger.info('prob that this is curse: '+str(proba))
    return {"is_cursed": 0 if proba < 0.7 else 1}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "curse_check:app", 
        host="127.0.0.1", 
        port=int(os.getenv("PORT", 8075)), 
        reload=True
    )