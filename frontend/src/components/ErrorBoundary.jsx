import React from 'react';

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error('❌ ErrorBoundary спіймав помилку:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="container text-center mt-5">
          <h2>Сталася помилка 🚨</h2>
          <p>{this.state.error?.message || 'Непередбачувана помилка'}</p>
        </div>
      );
    }

    return this.props.children;
  }
}

export default ErrorBoundary;
