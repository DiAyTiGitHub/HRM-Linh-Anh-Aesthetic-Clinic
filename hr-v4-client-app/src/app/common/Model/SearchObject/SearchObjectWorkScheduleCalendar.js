import {
    getFirstDateOfWeek,
    getLastDateOfWeek
} from "app/LocalFunction";
import { SearchObject } from "./SearchObject";

export class SearchObjectWorkScheduleCalendar extends SearchObject {
    staffId = null;
    staff = null;

    organizationId = null;
    organization = null;

    departmentId = null;
    department = null;

    positionId = null;
    position = null;

    fromDate = null;
    toDate = null;
    shiftWorkId = null;
    workingDate = null;


    constructor() {
        super();

        this.fromDate = getFirstDateOfWeek();
        this.toDate = getLastDateOfWeek();
    }


}