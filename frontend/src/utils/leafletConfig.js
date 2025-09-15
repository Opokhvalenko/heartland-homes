// src/utils/leafletConfig.js
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

const setupLeaflet = () => {
  // Виправлення шляху до іконок Leaflet для Webpack
  delete L.Icon.Default.prototype._getIconUrl;

  L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
    iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png'
  });
};

export default setupLeaflet;
