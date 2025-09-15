import React from 'react';
import { Mail, Phone } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const Support = () => (
  <section className="info-page container">
    <div className="info-header">
      <Mail className="info-icon" size={28} />
      <h1 className="page-title">Зв&apos;язатися з нами</h1>
    </div>

    <p className="lead-text">Маєте питання чи потребуєте допомоги? Ми завжди поруч!</p>

    <div className="support-box">
      <p>
        <Phone size={20} /> <a href="tel:+380671234567">+380 67 123 45 67</a>
      </p>
      <p>
        <Mail size={20} />{' '}
        <a href="mailto:support@heartlandhomes.com">support@heartlandhomes.com</a>
      </p>
    </div>
  </section>
);

export default Support;
