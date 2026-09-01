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
      <main style={{ fontFamily: 'system-ui', padding: '2rem', maxWidth: 900 }}>
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
            <section>
              <h2>{resultat.företagsnamn}</h2>
              <p>
                {Object.keys(resultat.konton).length} konton,{' '}
                {resultat.verifikat.length} verifikat
              </p>

              <table style={{ borderCollapse: 'collapse', width: '100%' }}>
                <thead>
                <tr>
                  <th style={{ textAlign: 'left' }}>Verifikat</th>
                  <th style={{ textAlign: 'left' }}>Datum</th>
                  <th style={{ textAlign: 'left' }}>Text</th>
                  <th style={{ textAlign: 'right' }}>Rader</th>
                </tr>
                </thead>
                <tbody>
                {resultat.verifikat.map((v) => (
                    <tr key={`${v.serie}${v.nummer}`}>
                      <td>{v.serie}{v.nummer}</td>
                      <td>{v.datum}</td>
                      <td>{v.text}</td>
                      <td style={{ textAlign: 'right' }}>{v.transaktioner.length}</td>
                    </tr>
                ))}
                </tbody>
              </table>
            </section>
        )}
      </main>
  )
}

export default App