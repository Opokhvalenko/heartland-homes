import React from 'react';
import { Shield } from 'lucide-react';
import '../../styles/components/_info-pages.scss';

const Privacy = () => (
  <section className="info-page container">
    <div className="info-header">
      <Shield className="info-icon" size={28} />
      <h1 className="page-title">Політика конфіденційності</h1>
    </div>

    <div className="info-block">
      <p>
        Ми поважаємо вашу приватність і дбаємо про безпеку ваших персональних даних. Жодна
        інформація не передається третім сторонам без вашої згоди.
      </p>
      <p>
        Ви можете звернутися до нас у будь-який час, щоб видалити або змінити ваші дані.
      </p>
    </div>
  </section>
);

export default Privacy;
