function emptyTable(tableId) {
    let table = document.getElementById(tableId);
    let rows = table.getElementsByTagName('tr');

    for (let i = rows.length - 1; i > 0; i--) {
        table.deleteRow(i);
    }
}