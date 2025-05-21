import LocalConstants from "app/LocalConstants";
import { SearchObject } from "./SearchObject";

export class SearchSalaryOutcome extends SearchObject {
    staffId = null;
    staff = null;
    organizationId = null;
    organization = null;
    departmentId = null;
    department = null;
    positionTitleId = null;
    positionTitle = null;
    positionId = null;
    position = null;

    salaryPeriodId = null;
    salaryPeriod = null;
    salaryTemplateId = null;
    salaryTemplate = null;
    salaryResultId = null;
    salaryResult = null;

    staffs = [];

    agreementStatus = null;
    isExportExcel = null;


    constructor() {
        super();

        this.agreementStatus = LocalConstants.AgreementStatus.SIGNED.value;
        this.isExportExcel = false;
    }


}