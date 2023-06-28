class Pager {
    constructor(entityName, tableId, createTableRow) {
        this.currentPage = 1;
        this.totalPages = 1;
        this.entityName = entityName;
        this.tableId = tableId;
        this.createTableRow = createTableRow;
    }
    
    async init() {
        const response = await fetch(`http://localhost:8080/api/${this.entityName}/pagesCount`);
        const json = await response.json();
        this.totalPages = json.pagesCount;

        this.loadPage(1);
    }

    async loadPage(n) {
        let response = await fetch(`http://localhost:8080/api/${this.entityName}/paginated?page=${n}`);
        let json = await response.json();
        let table = document.getElementById(this.tableId);
        emptyTable(this.tableId);
        json.forEach((item, index) => {
            let row = this.createTableRow(item, index);
            table.appendChild(row);
        });

        this.currentPage = n;
        this.updatePageNumbers();
    }

    updatePageNumbers() {
        let pageNumbersDiv = document.getElementById('pageNumbers');
        pageNumbersDiv.innerHTML = '';

        let pageNumbers = [];
        if (this.currentPage > 2) {
            pageNumbers.push(1, '...');
        }
        if (this.currentPage > 1) {
            pageNumbers.push(this.currentPage - 1);
        }
        pageNumbers.push(this.currentPage);
        if (this.currentPage < this.totalPages) {
            pageNumbers.push(this.currentPage + 1);
        }
        if (this.currentPage < this.totalPages - 1) {
            pageNumbers.push('...', this.totalPages);
        }

        pageNumbers.forEach(number => {
            let button = document.createElement('button');
            if (number === '...') {
                button.textContent = '...';
                button.disabled = true;
            } else {
                button.textContent = number;
                button.onclick = () => {
                    this.loadPage(number);
                };
            }
            pageNumbersDiv.appendChild(button);
        });
    }
}