import React from 'react';
import logo from '../../assets/shark-ia.png';
import '../../styles/processing-overlay.css';

export const ProcessingOverlay = ({ visible, message = 'Procesando...' }) => {
  if (!visible) return null;
  return (
    <div className="processing-overlay">
      <img src={logo} alt="SharkAI" className="processing-logo" />
      <span className="processing-message">{message}</span>
    </div>
  );
};
