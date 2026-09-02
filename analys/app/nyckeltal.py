from decimal import Decimal


def _summa(saldon, prefix):
    """Summerar saldon för konton som börjar med något av prefixen."""
    total = Decimal("0")
    for konto, belopp in saldon.items():
        if konto.startswith(tuple(prefix)):
            total += belopp
    return total


def beräkna(resultatkonton, balanskonton):
    # Intäkter bokförs med minustecken — vänd för läsbarhet
    nettoomsattning = -_summa(resultatkonton, ["3"])

    # Kostnadsklasser 4-7 (8 är finansiella poster, utanför rörelsen)
    rorelsekostnader = _summa(resultatkonton, ["4", "5", "6", "7"])

    rorelseresultat = nettoomsattning - rorelsekostnader

    tillgangar = _summa(balanskonton, ["1"])
    eget_kapital = -_summa(balanskonton, ["20"])

    nyckeltal = {
        "nettoomsattning": nettoomsattning,
        "rorelsekostnader": rorelsekostnader,
        "rorelseresultat": rorelseresultat,
        "tillgangar": tillgangar,
        "eget_kapital": eget_kapital,
    }

    if nettoomsattning != 0:
        nyckeltal["rorelsemarginal"] = rorelseresultat / nettoomsattning

    if tillgangar != 0:
        nyckeltal["soliditet"] = eget_kapital / tillgangar

    return nyckeltal