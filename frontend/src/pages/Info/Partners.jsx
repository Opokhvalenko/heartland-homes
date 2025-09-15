import React from 'react';
import { Users } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const partners = [
  { name: 'TravelUA', desc: 'Агентство подорожей' },
  { name: 'HotelPro', desc: 'Мережа готелів' },
  { name: 'CityRentals', desc: 'Оренда квартир' }
];

const Partners = () => (
  <section className="info-page container">
    <div className="info-header">
      <Users className="info-icon" size={28} />
      <h1 className="page-title">Наші партнери</h1>
    </div>

    <p className="lead-text">
      Ми співпрацюємо з надійними компаніями, щоб надати вам найкращий досвід.
    </p>

    <div className="about-highlights">
      {partners.map((p) => (
        <div key={p.name} className="highlight-card">
          <Users className="highlight-icon" size={32} />
          <h3>{p.name}</h3>
          <p>{p.desc}</p>
        </div>
      ))}
    </div>
  </section>
);

export default Partners;
