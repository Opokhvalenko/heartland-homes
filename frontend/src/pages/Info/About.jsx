import React from 'react';
import { Home } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const About = () => (
  <section className="info-page container">
    <div className="info-header">
      <Home className="info-icon" size={28} />
      <h1 className="page-title">Про нас</h1>
    </div>

    <p className="lead-text">
      <strong>Heartland Homes</strong> — сервіс для бронювання житла, який поєднує людей
      із затишними помешканнями по всій Україні. Ми прагнемо зробити подорожі доступними
      та комфортними для кожного.
    </p>

    <div className="about-highlights">
      <div className="highlight-card">
        <Home className="highlight-icon" size={36} />
        <h3>Затишні будинки</h3>
        <p>Вибір серед сотень перевірених варіантів житла.</p>
      </div>
      <div className="highlight-card">
        <Home className="highlight-icon" size={36} />
        <h3>Доступні ціни</h3>
        <p>Пропонуємо варіанти для будь-якого бюджету.</p>
      </div>
      <div className="highlight-card">
        <Home className="highlight-icon" size={36} />
        <h3>Зручне бронювання</h3>
        <p>Швидкий та простий процес пошуку і бронювання.</p>
      </div>
    </div>
  </section>
);

export default About;
