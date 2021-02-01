import React from 'react';
import _ from 'lodash';

const Pagination = props => {
    const {itemsCount, pageSize, currentPage, onPageChange} = props;
    const pagesCount = Math.ceil(itemsCount / pageSize);
    if(pagesCount < 2) {
        return null;
    }

    const pages = _.range(1, pagesCount + 1);
    return (
            <nav>
                <ul className="pagination justify-content-center">
                    <li className={currentPage === 1 ? 'page-item disabled' : 'page-item'}>
                        <a className="page-link" aria-label="Previous" onClick={() => onPageChange(currentPage + -1)}>
                            <span aria-hidden="true">&laquo;</span>
                            <span className="sr-only">Previous</span>
                        </a>
                    </li>
                    {pages.map(page => (
                            <li className={ page === currentPage ? 'page-item active' : 'page-item'} key={page}>
                                <a className="page-link" onClick={() => onPageChange(page)}>{page}</a>
                            </li>
                    ))}
                    <li className={currentPage >= pagesCount ? 'page-item disabled' : 'page-item'}>
                        <a className="page-link" aria-label="Next" onClick={() => onPageChange(currentPage + 1)}>
                            <span aria-hidden="true">&raquo;</span>
                            <span className="sr-only">Next</span>
                        </a>
                    </li>
                </ul>
            </nav>
    );
};

export default Pagination;