import React from 'react';
import { HelpCircle } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const faqs = [
  {
    q: 'Як забронювати житло?',
    a: 'Виберіть житло на сайті, натисніть “Бронювати” та завершіть оплату.'
  },
  {
    q: 'Чи можна скасувати бронювання?',
    a: 'Так, ви можете скасувати бронювання згідно з правилами обраного житла.'
  },
  {
    q: 'Які способи оплати доступні?',
    a: 'Ви можете оплатити карткою Visa, MasterCard або через онлайн-банкінг.'
  }
];

const FAQ = () => (
  <section className="info-page container">
    <div className="info-header">
      <HelpCircle className="info-icon" size={28} />
      <h1 className="page-title">Часті запитання</h1>
    </div>

    <div className="faq-grid">
      {faqs.map(({ q, a }, idx) => (
        <div key={idx} className="faq-card">
          <h3 className="faq-question">{q}</h3>
          <p className="faq-answer">{a}</p>
        </div>
      ))}
    </div>
  </section>
);

export default FAQ;
