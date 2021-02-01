import React from 'react';
import PropTypes from 'prop-types';

const PageSizeSelect = ({pageSize, onPageSizeChange}) => {
    return (
            <div className="form-group">
                <label htmlFor="pageSize">Page size</label>
                <select className="ml-2" id="pageSize" onChange={onPageSizeChange}
                        value={pageSize}>
                    <option>5</option>
                    <option>10</option>
                    <option>15</option>
                    <option>20</option>
                    <option>50</option>
                </select>
            </div>
    );
};

PageSizeSelect.propTypes = {
    pageSize: PropTypes.number.isRequired,
    onPageSizeChange: PropTypes.func.isRequired
}

export default PageSizeSelect;