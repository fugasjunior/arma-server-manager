import {useState} from "react";
import styles from "./ListBuilder.module.css";

const ListBuilder = props => {

    const [filter, setFilter] = useState("");

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    }

    const filteredAvailableOptions = props.availableOptions.filter(
            option => option.name.toLowerCase().startsWith(filter.toLowerCase()));

    return (
            <div className={styles.listbuilder}>
                <div className={styles.listbuilder__list}>
                    <div className={styles.listbuilder__filter}>
                        <input placeholder="Filter" onChange={handleFilterChange}/>
                    </div>
                    {filteredAvailableOptions.map(opt =>
                            <div className={styles.listbuilder__item}
                                 key={opt.id}
                                 onClick={() => props.onSelect(opt)}
                            >
                                {opt.name}
                            </div>
                    )}
                </div>
                <div className={styles.listbuilder__list}>
                    {props.selectedOptions.map(opt =>
                            <div className={styles.listbuilder__item}
                                 key={opt.id}
                                 onClick={() => props.onDeselect(opt)}
                            >
                                {opt.name}
                            </div>
                    )}
                </div>
            </div>
    )
}

export default ListBuilder;