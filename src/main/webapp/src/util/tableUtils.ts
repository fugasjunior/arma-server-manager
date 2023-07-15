import {EnhancedTableHeadCell, EnhancedTableRow} from "../UI/EnhancedTable/EnhancedTable.tsx";
import {isNumber} from "lodash";

export function getComparator(order: 'asc' | 'desc', orderBy: string, headCells: Array<EnhancedTableHeadCell>) {
    const sortByCell = headCells.find(cell => cell.id === orderBy);
    if (!sortByCell) {
        return;
    }

    const findCellValueToSortBy = (row: EnhancedTableRow) => {
        const cellToSortBy = row.cells.find(cell => cell.id === orderBy);
        if (!cellToSortBy) {
            throw new Error("Could not find cell to order by in table row.");
        }
        return cellToSortBy.value;
    }

    const orderSignum = order === "asc" ? 1 : -1;

    return (a: EnhancedTableRow, b: EnhancedTableRow) => {
        const first = findCellValueToSortBy(a);
        const second = findCellValueToSortBy(b);

        if (first instanceof Date && second instanceof Date) {
            return orderSignum * (+first - +second);
        }
        if (isNumber(first) && isNumber(second)) {
            return orderSignum * (first - second);
        }
        if (typeof first === "string" && typeof second === "string") {
            return orderSignum * (first.localeCompare(second));
        }
        return 0;
    }
}
