import React from 'react';
import { FileText } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const Terms = () => (
  <section className="info-page container">
    <div className="info-header">
      <FileText className="info-icon" size={28} />
      <h1 className="page-title">Умови використання</h1>
    </div>

    <div className="info-block">
      <p>
        Використовуючи наш сервіс, ви погоджуєтесь із правилами користування, бронювання
        та політикою скасування.
      </p>
      <ul className="styled-list terms">
        <li>Ви зобов’язані вводити правдиві дані під час бронювання.</li>
        <li>Забороняється здавати помешкання без підтвердження власника.</li>
        <li>Ми залишаємо за собою право змінювати умови...</li>
      </ul>
    </div>
  </section>
);

export default Terms;
