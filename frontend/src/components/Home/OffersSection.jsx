import React from 'react';
import Offers from '../Offers';

const OffersSection = () => (
  <section className="offers-section">
    <div className="container">
      <h2 className="section-heading">Пропозиції</h2>
      <p className="section-subheading">Акції, знижки та спеціальні пропозиції для вас</p>
      <Offers />
    </div>
  </section>
);

export default React.memo(OffersSection);
