import React, { useEffect, useState } from 'react';
import logo from '../../assets/shark-ia.png';
import '../../styles/processing-overlay.css';

export const ProcessingOverlay = ({ visible, message = 'Procesando...' }) => {
  const [elapsed, setElapsed] = useState(0);

  useEffect(() => {
    let interval;
    if (visible) {
      interval = setInterval(() => setElapsed((e) => e + 1), 1000);
    } else {
      setElapsed(0);
    }
    return () => clearInterval(interval);
  }, [visible]);

  const formatTime = (sec) => {
    const m = Math.floor(sec / 60).toString().padStart(2, '0');
    const s = (sec % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  if (!visible) return null;
  return (
    <div className="processing-overlay">
      <img src={logo} alt="SharkAI" className="processing-logo" />
      <span className="processing-message">{`${message} ${formatTime(elapsed)}`}</span>
    </div>
  );
};
