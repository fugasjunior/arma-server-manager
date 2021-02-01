import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';

const PageSizeSelect = ({pageSize, onPageSizeChange, min, max, step}) => {
    const options = _.range(min, max+1, step);
    return (
            <div className="form-group">
                <label htmlFor="pageSize">Page size</label>
                <select className="ml-2" id="pageSize" onChange={onPageSizeChange}
                        value={pageSize}>
                    {options.map(option => <option key={option}>{option}</option>)}
                </select>
            </div>
    );
};

PageSizeSelect.propTypes = {
    pageSize: PropTypes.number.isRequired,
    onPageSizeChange: PropTypes.func.isRequired,
    min: PropTypes.number,
    max: PropTypes.number,
    step: PropTypes.number
}

PageSizeSelect.defaultProps = {
    min: 5,
    max: 50,
    step: 5
}

export default PageSizeSelect;