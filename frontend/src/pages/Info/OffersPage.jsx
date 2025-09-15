import React from 'react';
import { Gift, Percent, Clock, Sparkles } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const offers = [
  {
    id: 1,
    icon: <Percent size={28} />,
    title: 'Літня знижка до 20%',
    description: 'Діє на всі апартаменти до кінця серпня.'
  },
  {
    id: 2,
    icon: <Clock size={28} />,
    title: 'Спеціальні ціни для довготривалих оренд',
    description: 'Орендуй житло від 14 днів та отримай кращу ціну.'
  },
  {
    id: 3,
    icon: <Gift size={28} />,
    title: 'Промокоди для нових користувачів',
    description: 'Отримай -10% на перше бронювання.'
  },
  {
    id: 4,
    icon: <Sparkles size={28} />,
    title: 'Ексклюзивні пропозиції',
    description: 'Доступні лише зареєстрованим користувачам.'
  }
];

const OffersPage = () => (
  <section className="info-page container">
    <div className="info-header">
      <Percent className="info-icon" size={32} />
      <h1 className="page-title">Акції та знижки</h1>
    </div>

    <p className="lead-text">
      Ознайомтеся з актуальними пропозиціями, щоб зробити ваші подорожі вигіднішими.
    </p>

    <div className="offers-grid">
      {offers.map(({ id, icon, title, description }) => (
        <div key={id} className="offer-card">
          <div className="offer-icon">{icon}</div>
          <h3>{title}</h3>
          <p>{description}</p>
        </div>
      ))}
    </div>
  </section>
);

export default OffersPage;
