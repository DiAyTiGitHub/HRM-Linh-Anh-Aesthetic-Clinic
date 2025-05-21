export class SalaryResultItemGroup {
    id = null;
    name = null;
    description = null;
    salaryResultId = null;

    constructor() {
        this.id = crypto.randomUUID();
    }
}

