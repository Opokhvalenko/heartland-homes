import React from 'react';

const Pagination = ({ page, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  return (
    <div className="pagination">
      <button
        className="btn btn-outline"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
      >
        ◀ Попередня
      </button>

      <span className="pagination-info">
        Сторінка <strong>{page + 1}</strong> із {totalPages}
      </span>

      <button
        className="btn btn-outline"
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
      >
        Наступна ▶
      </button>
    </div>
  );
};

export default Pagination;
