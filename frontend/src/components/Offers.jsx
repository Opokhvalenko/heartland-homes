import React, { useState } from 'react';
import '../styles/components/_offers.scss';
import '../styles/components/_buttons.scss';

const offers = [
  {
    id: 1,
    title: 'Коротка поїздка, якісний відпочинок',
    description: 'Економте до 20% із Сезонною пропозицією',
    details:
      'Ця акція діє лише протягом сезону! Знижки на найкращі помешкання у популярних містах.',
    image:
      'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=2070&auto=format&fit=crop'
  },
  {
    id: 2,
    title: 'Життя мрії в будинку для відпочинку',
    description: 'Вибирайте з-поміж будинків, вілл, шале тощо.',
    details:
      'Спеціальна добірка будинків для відпочинку — від затишних шале до розкішних вілл.',
    image:
      'https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?q=80&w=2074&auto=format&fit=crop'
  },
  {
    id: 3,
    title: 'Вирушайте у нову пригоду',
    description: 'Досліджуйте світ та знаходьте незабутні помешкання.',
    details:
      'Подорожуйте з нами та відкривайте нові напрями. Ми зібрали для вас найкращі пропозиції.',
    image:
      'https://plus.unsplash.com/premium_photo-1661964014750-963a28aeddea?q=80&w=2070&auto=format&fit=crop'
  },
  {
    id: 4,
    title: 'Нові враження чекають',
    description: 'Забронюйте житло та отримайте бонуси',
    details:
      'За кожне бронювання, зроблене у цьому місяці, отримуйте бонуси, які можна використати для майбутніх поїздок.',
    image:
      'https://images.unsplash.com/photo-1449844908441-8829872d2607?q=80&w=2070&auto=format&fit=crop'
  }
];

const Offers = () => {
  const [selectedOffer, setSelectedOffer] = useState(null);

  return (
    <>
      {/* тільки сітка карток */}
      <div className="offers-grid">
        {offers.map(({ id, title, description, image, details }) => (
          <article key={id} className="offer-card">
            <img className="offer-image" src={image} alt={title} loading="lazy" />
            <div className="card-body">
              <h5 className="card-title">{title}</h5>
              <p className="card-text">{description}</p>
              <button
                className="btn-primary mt-auto"
                onClick={() => setSelectedOffer({ title, description, details, image })}
              >
                Дізнатися більше
              </button>
            </div>
          </article>
        ))}
      </div>

      {/* Модальне вікно */}
      {selectedOffer && (
        <div
          className="modal-overlay"
          onClick={() => setSelectedOffer(null)}
          role="dialog"
          aria-modal="true"
          aria-labelledby="offer-title"
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button
              className="modal-close"
              aria-label="Закрити"
              onClick={() => setSelectedOffer(null)}
            >
              ✖
            </button>
            <img
              src={selectedOffer.image}
              alt={selectedOffer.title}
              className="modal-image"
            />
            <h3 id="offer-title">{selectedOffer.title}</h3>
            <p>{selectedOffer.details}</p>
          </div>
        </div>
      )}
    </>
  );
};

export default Offers;
