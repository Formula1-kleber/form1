import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";
import type { Session } from "../types";

const TICKER = ["Lights out and away we go", "Pit stop em menos de 3 segundos", "Mais de 300 km/h nas retas", "Interlagos • GP de São Paulo",
  "Fittipaldi • Piquet • Senna", "Bandeira quadriculada"];

const ABOUT = [
  { icon: "🏎️", title: "Os carros", text: "Monopostos de fibra de carbono que usam a aerodinâmica para “colar” no asfalto: quanto mais rápido, mais pressão sobre a pista." },
  { icon: "🧑‍🚀", title: "Os pilotos", text: "Nas curvas, o corpo chega a suportar cerca de 5 vezes o próprio peso (5 G), e o coração passa horas em ritmo de maratona." },
  { icon: "🛞", title: "Os pneus", text: "Três compostos para a corrida: macio (vermelho), médio (amarelo) e duro (branco). Escolher a hora de trocar decide corridas." },
  { icon: "⏱️", title: "O pit stop", text: "Quatro pneus trocados por mais de 20 mecânicos em cerca de 2 a 3 segundos. É trabalho em equipe na velocidade máxima." },
];

const TIMELINE = [
  { year: "1950", text: "Silverstone (Inglaterra) recebe a primeira corrida do Campeonato Mundial de Pilotos." },
  { year: "1958", text: "Nasce o Campeonato de Construtores: as equipes passam a disputar o próprio título." },
  { year: "1972", text: "Emerson Fittipaldi conquista o primeiro título mundial do Brasil." },
  { year: "1990", text: "O GP do Brasil volta a ser disputado em Interlagos, em São Paulo." },
  { year: "Hoje", text: "Você pode levar para casa uma peça de corrida no Leilão Pit Lane." },
];

const BRAZIL = [
  { name: "Emerson Fittipaldi", titles: "1972 • 1974" },
  { name: "Nelson Piquet", titles: "1981 • 1983 • 1987" },
  { name: "Ayrton Senna", titles: "1988 • 1990 • 1991" },
];

const DRIVERS = [
  { slug: "senna", name: "Ayrton Senna", flag: "🇧🇷", country: "Brasil", team: "McLaren • Lotus", titles: 3, color: "#c9a800",
    text: "Capacete amarelo, mestre da chuva e de Mônaco. Tricampeão em 1988, 1990 e 1991." },
  { slug: "schumacher", name: "Michael Schumacher", flag: "🇩🇪", country: "Alemanha", team: "Ferrari • Benetton", titles: 7, color: "#e10600",
    text: "Heptacampeão. Levou cinco títulos seguidos pela Ferrari, de 2000 a 2004." },
  { slug: "hamilton", name: "Lewis Hamilton", flag: "🇬🇧", country: "Reino Unido", team: "Ferrari • Mercedes • McLaren", titles: 7, color: "#00a79d",
    text: "Igualou o recorde de 7 títulos e, em 2025, trocou a Mercedes pela Ferrari." },
  { slug: "verstappen", name: "Max Verstappen", flag: "🇳🇱", country: "Países Baixos", team: "Red Bull", titles: 4, color: "#1e41ff",
    text: "Tetracampeão de 2021 a 2024, conhecido pela pilotagem agressiva." },
  { slug: "leclerc", name: "Charles Leclerc", flag: "🇲🇨", country: "Mônaco", team: "Ferrari", titles: 0, color: "#ff3b30",
    text: "Nascido em Monte Carlo, é piloto da Ferrari desde 2019 e um dos mais rápidos em volta lançada." },
  { slug: "fangio", name: "Juan Manuel Fangio", flag: "🇦🇷", country: "Argentina", team: "Maserati • Mercedes • Ferrari", titles: 5, color: "#6cc4ff",
    text: "O primeiro grande mestre: cinco títulos nos anos 1950, quando tudo começou." },
];

const initials = (n: string) => n.split(" ").map((w) => w[0]).slice(0, 2).join("");

function Driver({ d, delay }: { d: (typeof DRIVERS)[number]; delay: number }) {
  const [hasImg, setHasImg] = useState(true);
  return (
    <article className="driver reveal" style={{ "--team": d.color, transitionDelay: `${delay}ms` } as React.CSSProperties}>
      <span className="wm" aria-hidden="true">{d.name.split(" ").slice(-1)[0]}</span>
      {hasImg
        ? <img src={`/pilotos/${d.slug}.jpg`} alt={d.name} loading="lazy" onError={() => setHasImg(false)} />
        : <span className="initials" aria-hidden="true">{initials(d.name)}</span>}
      <div className="info">
        <span className="badge">{d.titles > 0 ? `🏆 ${d.titles}x campeão` : "Estrela do grid"}</span>
        <h4>{d.flag} {d.name}</h4>
        <small>{d.team}</small>
        <p>{d.text}</p>
      </div>
    </article>
  );
}

const LOTS = ["Capacete de piloto autografado", "Asa dianteira de carbono", "Volante de corrida", "Pneus usados do GP de Interlagos"];

/** Número que sobe até o valor quando aparece na tela. */
function Count({ to, prefix = "", suffix = "" }: { to: number; prefix?: string; suffix?: string }) {
  const ref = useRef<HTMLSpanElement>(null);
  const [v, setV] = useState(0);
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    let raf = 0;
    const io = new IntersectionObserver(([e]) => {
      if (!e.isIntersecting) return;
      io.disconnect();
      const t0 = performance.now();
      const step = (t: number) => {
        const p = Math.min(1, (t - t0) / 1400);
        setV(Math.round(to * (1 - Math.pow(1 - p, 3))));
        if (p < 1) raf = requestAnimationFrame(step);
      };
      raf = requestAnimationFrame(step);
    });
    io.observe(el);
    return () => { io.disconnect(); cancelAnimationFrame(raf); };
  }, [to]);
  return <span ref={ref}>{prefix}{v}{suffix}</span>;
}

export default function Home({ session }: { session: Session | null }) {
  const auctionPath = !session ? "/login" : session.role === "ADMIN" ? "/admin" : "/leilao";
  const auctionLabel = !session ? "Entrar e dar lances" : session.role === "ADMIN" ? "Abrir painel do admin" : "Ir para o leilão";

  // Revela as seções ao rolar a página.
  useEffect(() => {
    const io = new IntersectionObserver((es) => es.forEach((e) => {
      if (e.isIntersecting) { e.target.classList.add("in"); io.unobserve(e.target); }
    }), { threshold: 0.15 });
    document.querySelectorAll(".reveal").forEach((el) => io.observe(el));
    return () => io.disconnect();
  }, []);

  return (
    <div className="home">
      <section className="hero">
        <div className="speed" aria-hidden="true" />
        <div className="start" aria-hidden="true"><i /><i /><i /><i /><i /></div>
        <p className="kicker">Fórmula 1 • Leilão ao vivo</p>
        <h2>LEILÃO <em>PIT LANE</em></h2>
        <p className="lead">Conheça o esporte mais veloz do planeta e dispute peças de corrida em leilões em tempo real.</p>
        <p className="go" aria-hidden="true">KLEBER</p>
        <div className="cta">
          <Link className="btn red big" to={auctionPath}>{auctionLabel}</Link>
          <a className="btn ghost big" href="#pilotos">Ver os pilotos</a>
        </div>
      </section>

      <div className="ticker" aria-hidden="true">
        <div>{[...TICKER, ...TICKER].map((t, i) => <span key={i}>{t}</span>)}</div>
      </div>

      <section className="bigstats reveal">
        <div><b><Count to={1950} /></b><span>ano do 1º campeonato mundial</span></div>
        <div><b><Count to={300} suffix="+" /></b><span>km/h nas retas</span></div>
        <div><b><Count to={3} prefix="< " suffix=" s" /></b><span>para trocar 4 pneus</span></div>
        <div><b><Count to={8} /></b><span>títulos de pilotos brasileiros</span></div>
      </section>

      <section id="sobre" className="block">
        <h3 className="reveal">Como funciona a <em>Fórmula 1</em></h3>
        <div className="cards">
          {ABOUT.map((a, i) => (
            <article key={a.title} className="card reveal" style={{ transitionDelay: `${i * 90}ms` }}>
              <span className="ico">{a.icon}</span>
              <h4>{a.title}</h4>
              <p>{a.text}</p>
            </article>
          ))}
        </div>
      </section>

      <section id="pilotos" className="block">
        <h3 className="reveal">Lendas e <em>estrelas do grid</em></h3>
        <div className="drivers">
          {DRIVERS.map((d, i) => <Driver key={d.slug} d={d} delay={i * 80} />)}
        </div>
      </section>

      <div className="checker" aria-hidden="true" />

      <section id="historia" className="block">
        <h3 className="reveal">Uma história em <em>alta velocidade</em></h3>
        <ol className="timeline">
          {TIMELINE.map((t) => (
            <li key={t.year} className="reveal"><b>{t.year}</b><p>{t.text}</p></li>
          ))}
        </ol>
      </section>

      <section className="block brazil">
        <h3 className="reveal">Orgulho <em>brasileiro</em></h3>
        <div className="cards three">
          {BRAZIL.map((d) => (
            <article key={d.name} className="card flag reveal">
              <h4>{d.name}</h4>
              <p className="years">{d.titles}</p>
              <small>Campeão mundial</small>
            </article>
          ))}
        </div>
      </section>

      <section className="block auctionpromo">
        <h3 className="reveal">Leve a F1 <em>para casa</em></h3>
        <p className="reveal sub">Lances em tempo real, aviso na hora quando alguém cobre o seu e resultado de cada leilão. Exemplos do que costuma ir a leilão:</p>
        <div className="cards">
          {LOTS.map((l, i) => (
            <article key={l} className="card lotcard reveal" style={{ transitionDelay: `${i * 90}ms` }}>
              <span className="tag">Exemplo</span>
              <h4>{l}</h4>
              <p>Dê o seu lance antes que o cronômetro zere.</p>
            </article>
          ))}
        </div>
        <div className="cta center reveal">
          <Link className="btn red big" to={auctionPath}>{auctionLabel}</Link>
        </div>
      </section>

      <footer className="foot">Projeto acadêmico, sem qualquer vínculo com a Formula 1® ou suas equipes.</footer>
    </div>
  );
}
