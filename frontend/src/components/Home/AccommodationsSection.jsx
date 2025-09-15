import React from 'react';
import { Link } from 'react-router-dom';
import AccommodationList from '../../pages/Accommodations/AccommodationList';
import Notification from '../Notification';

const AccommodationsSection = ({ loading, error, accommodations }) => (
  <section className="accommodations-section">
    <div className="container">
      <div className="section-header">
        <h2 className="section-heading">Доступні помешкання</h2>
        <Link to="/accommodations" className="btn btn-secondary view-all-btn">
          Переглянути всі помешкання →
        </Link>
      </div>

      {loading && <p className="text-center">Завантаження...</p>}
      {error && <Notification message={error} type="danger" />}
      {!loading && !error && <AccommodationList accommodations={accommodations} />}
    </div>
  </section>
);

export default React.memo(AccommodationsSection);
