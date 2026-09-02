from decimal import Decimal

from app.nyckeltal import beräkna


def test_nettoomsättning_vänder_tecken():
    resultatkonton = {"3041": Decimal("-1000"), "3051": Decimal("-500")}
    balanskonton = {}

    n = beräkna(resultatkonton, balanskonton)

    assert n["nettoomsattning"] == Decimal("1500")


def test_rörelseresultat():
    resultatkonton = {
        "3041": Decimal("-1000"),
        "4010": Decimal("400"),
        "5010": Decimal("100"),
    }
    balanskonton = {}

    n = beräkna(resultatkonton, balanskonton)

    assert n["rorelseresultat"] == Decimal("500")


def test_soliditet():
    resultatkonton = {}
    balanskonton = {"1910": Decimal("1000"), "2081": Decimal("-400")}

    n = beräkna(resultatkonton, balanskonton)

    assert n["soliditet"] == Decimal("0.4")