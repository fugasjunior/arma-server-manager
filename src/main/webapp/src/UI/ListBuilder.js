import {useState} from "react";
import styles from "./ListBuilder.module.css";

const ListBuilder = props => {
    const {showFilter, availableOptions, selectedOptions, onSelect, onDeselect} = props;

    const [filter, setFilter] = useState("");

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    }

    const filteredAvailableOptions = availableOptions.filter(
            option => option.name.toLowerCase().startsWith(filter.toLowerCase()));

    const itemsLabel = props.itemsLabel ?? "options";

    return (
            <div className={styles.listbuilder}>
                <div className={styles.listbuilder__list}>
                    {showFilter && <div className={styles.listbuilder__filter}>
                        <input placeholder="Filter" onChange={handleFilterChange}/>
                    </div>}
                    {filteredAvailableOptions.length === 0 && <span>No {itemsLabel} available</span>}
                    {filteredAvailableOptions.map(opt =>
                            <div className={styles.listbuilder__item}
                                 key={opt.id}
                                 onClick={() => onSelect(opt)}
                            >
                                {opt.name}
                            </div>
                    )}
                </div>
                <div className={styles.listbuilder__list}>
                    {selectedOptions.length === 0 && <span>No {itemsLabel} selected</span>}
                    {selectedOptions.map(opt =>
                            <div className={styles.listbuilder__item}
                                 key={opt.id}
                                 onClick={() => onDeselect(opt)}
                            >
                                {opt.name}
                            </div>
                    )}
                </div>
            </div>
    )
}

export default ListBuilder;