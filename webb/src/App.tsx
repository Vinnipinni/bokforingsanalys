import { useState } from 'react'

type Transaktion = {
  konto: string
  belopp: number
}

type Verifikat = {
  serie: string
  nummer: string
  datum: string
  text: string
  transaktioner: Transaktion[]
}

type ImportResultat = {
  företagsnamn: string
  konton: Record<string, string>
  verifikat: Verifikat[]
  resultatkonton: Record<string, number>
  balanskonton: Record<string, number>
}

const kr = new Intl.NumberFormat('sv-SE', {
  style: 'currency',
  currency: 'SEK',
  maximumFractionDigits: 0,
})

function Saldotabell({
                       rubrik,
                       saldon,
                       konton,
                     }: {
  rubrik: string
  saldon: Record<string, number>
  konton: Record<string, string>
}) {
  const rader = Object.entries(saldon)
      .filter(([, belopp]) => belopp !== 0)
      .sort(([a], [b]) => a.localeCompare(b))

  const summa = rader.reduce((s, [, belopp]) => s + belopp, 0)

  return (
      <section style={{ marginTop: '2rem' }}>
        <h3>{rubrik}</h3>
        <table>
          <thead>
          <tr>
            <th>Konto</th>
            <th>Benämning</th>
            <th style={{ textAlign: 'right' }}>Saldo</th>
          </tr>
          </thead>
          <tbody>
          {rader.map(([konto, belopp]) => (
              <tr key={konto}>
                <td>{konto}</td>
                <td>{konton[konto] ?? ''}</td>
                <td style={{ textAlign: 'right' }}>{kr.format(belopp)}</td>
              </tr>
          ))}
          <tr>
            <td colSpan={2}><strong>Summa</strong></td>
            <td style={{ textAlign: 'right' }}>
              <strong>{kr.format(summa)}</strong>
            </td>
          </tr>
          </tbody>
        </table>
      </section>
  )
}

function App() {
  const [resultat, setResultat] = useState<ImportResultat | null>(null)
  const [laddar, setLaddar] = useState(false)
  const [fel, setFel] = useState<string | null>(null)

  async function laddaUpp(fil: File) {
    setLaddar(true)
    setFel(null)

    const data = new FormData()
    data.append('fil', fil)

    try {
      const svar = await fetch('/api/import', { method: 'POST', body: data })
      if (!svar.ok) {
        throw new Error(`Servern svarade ${svar.status}`)
      }
      setResultat(await svar.json())
    } catch (e) {
      setFel(e instanceof Error ? e.message : 'Något gick fel')
    } finally {
      setLaddar(false)
    }
  }

  return (
      <main style={{ padding: '2rem', maxWidth: 900 }}>
        <h1>Bokföringsanalys</h1>

        <input
            type="file"
            accept=".se,.si"
            onChange={(e) => {
              const fil = e.target.files?.[0]
              if (fil) laddaUpp(fil)
            }}
        />

        {laddar && <p>Läser filen…</p>}
        {fel && <p style={{ color: 'crimson' }}>{fel}</p>}

        {resultat && (
            <>
              <h2>{resultat.företagsnamn}</h2>
              <p>
                {Object.keys(resultat.konton).length} konton,{' '}
                {resultat.verifikat.length} verifikat
              </p>

              <Saldotabell
                  rubrik="Resultaträkning"
                  saldon={resultat.resultatkonton}
                  konton={resultat.konton}
              />

              <Saldotabell
                  rubrik="Balansräkning"
                  saldon={resultat.balanskonton}
                  konton={resultat.konton}
              />
            </>
        )}
      </main>
  )
}

export default App