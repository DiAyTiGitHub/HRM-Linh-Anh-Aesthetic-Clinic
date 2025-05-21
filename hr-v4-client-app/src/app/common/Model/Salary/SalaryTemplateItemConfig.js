import LocalConstants from "app/LocalConstants";

export class SalaryTemplateItemConfig {
    id = null;
    templateItemId = null;
    itemValue = null;
    configType = null;
    thresholdValue = null;
    formula = null; //công thức tính
    operator = null; // toán tử so sánh. Chi tiết: HrConstants.SalaryTemplateItemConfigOperator

    constructor() {
        // this.id = crypto.randomUUID();
        this.thresholdValue = 0.0;
        this.configType = LocalConstants.ConfigType.FIX.value;
        this.itemValue = 0.0;
        this.operator = LocalConstants.SalaryTemplateItemConfigOperator.GREATER_THAN_OR_EQUALS.value;
    }
}

