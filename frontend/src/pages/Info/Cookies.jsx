import React from 'react';
import { Cookie } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const Cookies = () => (
  <section className="info-page container">
    <div className="info-header">
      <Cookie className="info-icon" size={28} />
      <h1 className="page-title">Файли Cookie</h1>
    </div>

    <div className="info-block">
      <p>Ми використовуємо cookie для покращення вашого досвіду користування сайтом.</p>
      <ul className="styled-list cookies">
        <li>Cookie допомагають нам аналізувати трафік.</li>
        <li>Запам’ятовують ваші уподобання...</li>
        <li>Ви можете вимкнути cookie...</li>
      </ul>
    </div>
  </section>
);

export default Cookies;
