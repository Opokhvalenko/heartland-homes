import React from 'react';
import SearchForm from '../SearchForm';

const HeroSection = ({ onSearch }) => (
  <section className="hero-section">
    <div className="container">
      <h1 className="hero-heading">Знайдіть помешкання для наступної подорожі</h1>
      <p className="hero-subheading">
        Знаходьте пропозиції готелів, приватних помешкань та багато іншого...
      </p>
      <SearchForm onSearch={onSearch} />
    </div>
  </section>
);

export default React.memo(HeroSection);
