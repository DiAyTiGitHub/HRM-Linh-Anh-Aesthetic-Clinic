export class SalaryTemplateItemGroup {
    id = null;
    name = null;
    description = null;
    salaryTemplateId = null;

    constructor() {
        this.id = crypto.randomUUID();
    }
}

