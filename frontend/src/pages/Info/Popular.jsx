import React from 'react';
import { MapPin } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const destinations = [
  { city: 'Київ', desc: 'Столиця з безліччю варіантів для відпочинку' },
  { city: 'Львів', desc: 'Історія, кава та неймовірна атмосфера' },
  { city: 'Одеса', desc: 'Море, пляжі та культурні події' },
  { city: 'Карпати', desc: 'Гори, природа та активний відпочинок' }
];

const Popular = () => (
  <section className="info-page container">
    <div className="info-header">
      <MapPin className="info-icon" size={28} />
      <h1 className="page-title">Популярні напрямки</h1>
    </div>

    <p className="lead-text">
      Оберіть напрямок для своєї наступної подорожі з найпопулярніших міст та регіонів.
    </p>

    <div className="about-highlights">
      {destinations.map((d) => (
        <div key={d.city} className="highlight-card">
          <MapPin className="highlight-icon" size={32} />
          <h3>{d.city}</h3>
          <p>{d.desc}</p>
        </div>
      ))}
    </div>
  </section>
);

export default Popular;
