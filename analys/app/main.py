from decimal import Decimal

from fastapi import FastAPI
from pydantic import BaseModel

from app.nyckeltal import beräkna

app = FastAPI(title="Bokföringsanalys – nyckeltal")


class Saldon(BaseModel):
    resultatkonton: dict[str, Decimal]
    balanskonton: dict[str, Decimal]


@app.get("/halsa")
def halsa():
    return {"status": "uppe"}


@app.post("/nyckeltal")
def nyckeltal(saldon: Saldon):
    return beräkna(saldon.resultatkonton, saldon.balanskonton)