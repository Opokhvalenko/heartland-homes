import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import {
  Phone,
  Mail,
  MapPin,
  FacebookIcon,
  InstagramIcon,
  TwitterIcon
} from 'lucide-react';
import '../styles/components/footer/_footer.scss';

const footerLinks = [
  {
    heading: 'Підтримка',
    links: [
      { to: '/faq', label: 'FAQ' },
      { to: '/support', label: "Зв'язатися з нами" },
      { to: '/about', label: 'Про нас' }
    ]
  },
  {
    heading: 'Пропозиції',
    links: [
      { to: '/offers', label: 'Акції та знижки' },
      { to: '/popular', label: 'Популярні напрямки' },
      { to: '/partners', label: 'Партнери' }
    ]
  },
  {
    heading: 'Інформація',
    links: [
      { to: '/terms', label: 'Умови використання' },
      { to: '/privacy', label: 'Політика конфіденційності' },
      { to: '/cookies', label: 'Файли Cookie' }
    ]
  }
];

const socialLinks = [
  { href: 'https://facebook.com', icon: <FacebookIcon />, label: 'Facebook' },
  { href: 'https://instagram.com', icon: <InstagramIcon />, label: 'Instagram' },
  { href: 'https://twitter.com', icon: <TwitterIcon />, label: 'Twitter' }
];

const Footer = () => {
  const { isAuthenticated } = useSelector((state) => state.auth);

  return (
    <footer className="footer" role="contentinfo">
      <div className="footer__inner">
        {isAuthenticated && (
          <section className="footer__property">
            <h5 className="footer__property__title">Зареєструвати своє помешкання</h5>
            <p className="footer__property__text">
              Отримайте доступ до мільйонів потенційних гостей по всьому світу.
            </p>
            <Link
              to="/accommodations/new"
              className="btn-secondary footer__property__btn"
            >
              Розпочати
            </Link>
          </section>
        )}

        {/* Основна сітка */}
        <div className="footer__main">
          {/* 1. Інформативні блоки */}
          <nav className="footer__links" aria-label="Корисні посилання">
            {footerLinks.map((col) => (
              <div key={col.heading} className="footer__column">
                <h5 className="footer__column__title">{col.heading}</h5>
                <ul className="footer__column__list">
                  {col.links.map((link) => (
                    <li key={link.to} className="footer__column__item">
                      <Link to={link.to} className="footer__column__link">
                        {link.label}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </nav>

          {/* 2. Контакти */}
          <address className="footer__contacts" aria-label="Контактна інформація">
            <a href="tel:+380671234567" className="footer__contacts__link">
              <Phone size={18} /> +380 67 123 45 67
            </a>
            <a
              href="mailto:support@heartlandhomes.com"
              className="footer__contacts__link"
            >
              <Mail size={18} /> support@heartlandhomes.com
            </a>
            <a
              href="https://goo.gl/maps/x9c6q"
              target="_blank"
              rel="noopener noreferrer"
              className="footer__contacts__link"
            >
              <MapPin size={18} /> Київ, вул. Хрещатик, 12
            </a>
          </address>

          {/* 3. Соцмережі */}
          <div className="footer__social" aria-label="Соціальні мережі">
            {socialLinks.map((social) => (
              <a
                key={social.label}
                href={social.href}
                target="_blank"
                rel="noopener noreferrer"
                className="footer__social__link"
                aria-label={social.label}
              >
                {social.icon}
              </a>
            ))}
          </div>
        </div>

        {/* 4. Копірайт */}
        <div className="footer__copyright">
          <p className="footer__copyright__text">
            Heartland Homes © {new Date().getFullYear()}. Проєкт створено для навчання.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
