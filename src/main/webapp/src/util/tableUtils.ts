export function getComparator(order: 'asc' | 'desc', orderBy: string, headCells: Array<{id: string, type?: string}>) {
    const sortByCell = headCells.find(cell => cell.id === orderBy);
    if (!sortByCell) {
        return;
    }

    if (sortByCell.type === "number" || sortByCell.type === "date") {
        return order === "desc"
            ? (a: any, b: any) => a[orderBy] - b[orderBy]
            : (a: any, b: any) => b[orderBy] - a[orderBy];
    }

    return order === "desc"
        ? (a: any, b: any) => b[orderBy].localeCompare(a[orderBy])
        : (a: any, b: any) => a[orderBy].localeCompare(b[orderBy]);
}
